package com.cloud.kafkastreamjoins.binder;

import com.cloud.kafkastreamjoins.model.input.AdClick;
import com.cloud.kafkastreamjoins.model.input.AdInventories;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface AdClickListenerBinder {

    @Input("inventory-channel")
    GlobalKTable<String, AdInventories> inventoriesInputStream();

    @Input("click-channel")
    KStream<String, AdClick> clickInputStream();
}
