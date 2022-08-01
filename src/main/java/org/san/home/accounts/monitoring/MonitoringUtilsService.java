package org.san.home.accounts.monitoring;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public class MonitoringUtilsService {
    @Autowired
    private MeterRegistry registry;
    private Counter requestsFailedCounter;
    private Counter requestsCounter;
    private Gauge requestsActive;
    private AtomicLong requestsActiveCounter = new AtomicLong(0);

    public MonitoringUtilsService(MeterRegistry registry) {
        this.registry = registry;
        requestsCounter = registry.counter("requests");
        requestsFailedCounter = registry.counter("requests_failed");
        requestsActive = Gauge.builder("requests_active", requestsActiveCounter, AtomicLong::get).register(registry);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    public void incrementRequestsActiveCounter() {
        requestsActiveCounter.incrementAndGet();
    }

    public void decrementRequestsActiveCounter() {
        requestsActiveCounter.decrementAndGet();
    }

    /**
     * private void registerMetricsFilter(MeterRegistry registry) {
     *         registry.config().meterFilter(new MeterFilter() {
     *             @Override
     *             public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
     *                 if (id.getName().equals("api.request.duration")) {
     *                     return DistributionStatisticConfig.builder()
     *                             .sla(Duration.ofMillis(10).toNanos(),
     *                                     Duration.ofMillis(25).toNanos(),
     *                                     Duration.ofMillis(50).toNanos(),
     *                                     Duration.ofMillis(100).toNanos(),
     *                                     Duration.ofMillis(500).toNanos(),
     *                                     Duration.ofMillis(1000).toNanos(),
     *                                     Duration.ofMillis(5000).toNanos())
     *                             .build()
     *                             .merge(config);
     *                 }
     *                 return config;
     *             }
     *         });
     *     }
     */
}
