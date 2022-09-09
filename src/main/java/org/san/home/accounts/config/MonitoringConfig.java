package org.san.home.accounts.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(@Value("${info.app.service-id}") String serviceId,
                                                                          @Value("${info.app.api-version}") String apiVersion,
                                                                          @Value("${info.app.release-id}") String releaseId) {
        return registry -> registry.config().commonTags(
                "serviceId", serviceId,
                "version", apiVersion,
                "releaseId", releaseId);
    }
}
