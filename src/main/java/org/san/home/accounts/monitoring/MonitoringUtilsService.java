package org.san.home.accounts.monitoring;

import com.google.common.base.Throwables;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public class MonitoringUtilsService {
    @Autowired
    private MeterRegistry registry;
    public static final String SUCCESS_REQ_COUNTER_METRIC_NAME = "requests_success";
    public static final String FAILED_REQ_COUNTER_METRIC_NAME = "requests_failed";
    public static final String TIMEOUT_COUNTER_METRIC_NAME = "errors_timeout";
    public static final String ERROR_COUNTER_METRIC_NAME = "errors";
    public static final String REQ_ACTIVE_GAUGE_METRIC_NAME = "requests_active";

    private Counter requestsFailedCounter;
    private Counter successRequestsCounter;
    private Counter timeoutCounter;
    private Counter errorsCounter;
    private Gauge requestsActive;
    private AtomicLong requestsActiveCounter = new AtomicLong(0);

    public MonitoringUtilsService(MeterRegistry registry) {
        this.registry = registry;
        successRequestsCounter = registry.counter(SUCCESS_REQ_COUNTER_METRIC_NAME);
        requestsFailedCounter = registry.counter(FAILED_REQ_COUNTER_METRIC_NAME);
        timeoutCounter = registry.counter(TIMEOUT_COUNTER_METRIC_NAME);
        errorsCounter = registry.counter(ERROR_COUNTER_METRIC_NAME);
        requestsActive = Gauge.builder(REQ_ACTIVE_GAUGE_METRIC_NAME, requestsActiveCounter, AtomicLong::get).register(registry);
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

    public void processException(@NotNull Exception e) {
        requestsFailedCounter.increment();
        processTimeoutException(e);
    }

    public void processTimeoutException(Exception e) {
        if (Throwables.getRootCause(e) instanceof SocketTimeoutException) {
            timeoutCounter.increment();
        }
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
