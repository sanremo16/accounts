package org.san.home.accounts;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class MicrometerTestConfiguration {
    @Bean
    public MeterRegistry registry() {
        return new SimpleMeterRegistry();
    }
}
