package com.cloud.kafkastreamjoins.binder;

import com.cloud.kafkastreamjoins.model.input.UserDetails;
import com.cloud.kafkastreamjoins.model.input.UserLogin;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;

public interface UserLoginListenerBinder {

    @Input("user-master-channel")
    KTable<String, UserDetails> userDetailsInputStream();

    @Input("user-login-channel")
    KTable<String, UserLogin> userLoginInputStream();
}
