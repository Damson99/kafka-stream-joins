package com.cloud.kafkastreamjoins.service;

import com.cloud.kafkastreamjoins.binder.AdClickListenerBinder;
import com.cloud.kafkastreamjoins.model.input.AdClick;
import com.cloud.kafkastreamjoins.model.input.AdInventories;
import com.cloud.kafkastreamjoins.model.output.ClicksByNewsType;
import com.cloud.kafkastreamjoins.model.output.Top3NewsTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableBinding(AdClickListenerBinder.class)
@Profile("advert-click")
public class AdClickKafkaService {

//     GlobalKTable read data from all of the partitions in kafka so this is good for small set of information
//     that you want to be available to all your instances.
//     GlobalKTable will be available to all stream threads
    @StreamListener
    public void process(@Input("inventory-channel") GlobalKTable<String, AdInventories> invInStream,
                        @Input("click-channel") KStream<String, AdClick> adInStream) {

        adInStream.foreach((key, value) -> log.info("Click key: {}, Value: {}", key, value));

        KTable<String, Long> clicksByNewTypeKTable= adInStream.join(
                invInStream,
//                return new join key. In this example it is foreign key
                (clickKey, clickValue) -> clickKey,
                (clickValue, inventoryValue) -> inventoryValue)
                .groupBy((joinedKey, joinedValue) -> joinedValue.getNewsType(),
                        Grouped.with(Serdes.String(),
                                new JsonSerde<>(AdInventories.class)))
                .count();
//        sorting data by value and takes 3 first records order by count.
//        data is distributed over all stream threads across the instance of applications
//        have to bring data to one place using groupBy with a fixed key.
//        move old key(NewsType) to value with count of clicks

//        KGroupedTable is not a static database table, the new incoming records still replace old now
        clicksByNewTypeKTable.groupBy(
                (key, value) -> {
                    ClicksByNewsType clicksByNewsType= new ClicksByNewsType();
                    clicksByNewsType.setClicks(value);
                    clicksByNewsType.setNewsType(key);
//                    return new value for KTable. The key is a fixed String top3NewsTypes
                    return KeyValue.pair("top3NewsTypes", clicksByNewsType);
                },
                Grouped.with(Serdes.String(), new JsonSerde<>(ClicksByNewsType.class))
//                takes three arguments
        ).aggregate(
//                1. initializer | empty value to initiate
                Top3NewsTypes::new,
//                2. adder       | new value to aggregate / add
//                key, new value, aggregated value
                (key, value, aggVal) -> {
                    aggVal.add(value);
                    return aggVal;
                },
//                3. subtractor  | remove old record which was updated by aggregating
                (key, value, aggVal) -> {
                    aggVal.remove(value);
                    return aggVal;
                },
//                materializing intermediate data to a local KTable
//                using default Serde in yaml, so have to add additional configuration for Serde
                Materialized
//                        top3-clicks - name of the intermediate state store table
//                        KeyValueStore is the type of state store
                        .<String, Top3NewsTypes, KeyValueStore<Bytes, byte[]>>as("top3-clicks")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new JsonSerde<>(Top3NewsTypes.class))
        ).toStream()
                .foreach(
                (key, value) -> {
                    try {
                        log.info("key: "+key+" value: "+value.getTop3Sorted());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
    }
}
