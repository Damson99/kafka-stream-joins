package com.cloud.kafkastreamjoins.service;


import com.cloud.kafkastreamjoins.binder.UserLoginListenerBinder;
import com.cloud.kafkastreamjoins.model.input.UserDetails;
import com.cloud.kafkastreamjoins.model.input.UserLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Slf4j
@Service
@Profile("user-login")
@RequiredArgsConstructor
@EnableBinding(UserLoginListenerBinder.class)
public class UserLoginKafkaService {

    @StreamListener
    public void process(@Input("user-master-channel")KTable<String, UserDetails> uDetailsStream,
                        @Input("user-login-channel")KTable<String, UserLogin> uLoginStream) {

        uDetailsStream.toStream().foreach((key, value) -> log.info("user key: {}, last login: {}, value{}",
                key, Instant.ofEpochMilli(value.getLastLogin()).atOffset(ZoneOffset.UTC), value));

        uLoginStream.toStream().foreach((key, value) -> log.info("login key: {}, last login: {}, value{}",
                key, Instant.ofEpochMilli(value.getCreatedTime()).atOffset(ZoneOffset.UTC), value));

//        key is login id
        uLoginStream.join(
//                other stream to join
                uDetailsStream,
//                ValueJoiner lambda
                (loginArg, detailsArg) -> {
                    detailsArg.setLastLogin(loginArg.getCreatedTime());
                    return detailsArg;
                })
                .toStream()
                .foreach((key, value) -> log.info("Updated last login key: {}, last login: {}", key,
                        Instant.ofEpochMilli(value.getLastLogin()).atOffset(ZoneOffset.UTC)));
    }
}
