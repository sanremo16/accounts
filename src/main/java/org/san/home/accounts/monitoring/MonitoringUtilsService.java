package org.san.home.accounts.monitoring;

import com.avpines.dynamic.meters.counter.DynamicCounter;
import com.google.common.base.Throwables;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
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
    public static final String SOURCE_TAG_NAME = "source";

    private DynamicCounter requestsFailedCounter;
    private DynamicCounter successRequestsCounter;
    private DynamicCounter timeoutCounter;
    private DynamicCounter errorsCounter;
    private Gauge requestsActive;
    private AtomicLong requestsActiveCounter = new AtomicLong(0);

    public MonitoringUtilsService(MeterRegistry registry) {
        this.registry = registry;
        successRequestsCounter = buildCounter(SUCCESS_REQ_COUNTER_METRIC_NAME);
        //successRequestsCounter = registry.counter(SUCCESS_REQ_COUNTER_METRIC_NAME);
        requestsFailedCounter = buildCounter(FAILED_REQ_COUNTER_METRIC_NAME);
        timeoutCounter = buildCounter(TIMEOUT_COUNTER_METRIC_NAME);
        errorsCounter = buildCounter(ERROR_COUNTER_METRIC_NAME);
        requestsActive = Gauge.builder(REQ_ACTIVE_GAUGE_METRIC_NAME, requestsActiveCounter, AtomicLong::get).register(registry);
    }

    private DynamicCounter buildCounter(String meterName) {
        return DynamicCounter.builder(registry, meterName).tagKeys(SOURCE_TAG_NAME).build();
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

    public void processException(@NotNull Exception e, String source) {
        requestsFailedCounter.getOrCreate(source).increment();
        processTimeoutException(e, source);
    }

    public void processTimeoutException(Exception e, String source) {
        if (Throwables.getRootCause(e) instanceof SocketTimeoutException) {
            timeoutCounter.getOrCreate(source).increment();
        }
    }

    /**public void addLabel(String key, String value) {
        successRequestsCounter.
        registry.config().meterFilter(MeterFilter.allaccept()).
    }*/

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
