package org.san.home.accounts.monitoring;

import com.avpines.dynamic.meters.counter.DynamicCounter;
import com.google.common.base.Throwables;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public class MonitoringUtilsService {

    @Getter
    public enum Metric {
        SUCCESS_REQ_COUNTER("requests_success", "Success requests counter by http status"),
        FAILED_REQ_COUNTER("requests_failed", "Failed requests counter without timeout"),
        TIMEOUT_COUNTER("errors_timeout", "Timeout requests counter"),
        ERROR_COUNTER("errors", "Error request  counter without timeout"),
        REQ_ACTIVE_GAUGE("requests_active", "Active request gauge"),
        RESP_SIZE_DISTRIBUTION("response_size", "Response size in bytes"),
        REQ_SIZE_DISTRIBUTION("request_size", "Request size in bytes");


        private String name;
        private String descr;

        Metric(String name, String descr){
            this.name = name;
            this.descr = descr;
        }
    }
    public static final String SOURCE_TAG_NAME = "source";

    @Autowired
    private MeterRegistry registry;
    @Autowired
    private DataSource dataSource;


    private DynamicCounter requestsFailedCounter;
    private DynamicCounter successRequestsCounter;
    private DynamicCounter timeoutCounter;
    private DynamicCounter errorsCounter;
    private Gauge requestsActive;
    private AtomicLong requestsActiveCounter = new AtomicLong(0);
    private DistributionSummary responseSizeDistribution;
    private DistributionSummary requestSizeDistribution;


    public MonitoringUtilsService(MeterRegistry registry) {
        this.registry = registry;
        successRequestsCounter = buildCounter(Metric.SUCCESS_REQ_COUNTER);
        requestsFailedCounter = buildCounter(Metric.FAILED_REQ_COUNTER);
        timeoutCounter = buildCounter(Metric.TIMEOUT_COUNTER);
        errorsCounter = buildCounter(Metric.ERROR_COUNTER);
        requestsActive = Gauge.builder(Metric.REQ_ACTIVE_GAUGE.getName(), requestsActiveCounter, AtomicLong::get)
                .description(Metric.REQ_ACTIVE_GAUGE.getDescr()).register(registry);
        responseSizeDistribution = createSizeDistribution(Metric.RESP_SIZE_DISTRIBUTION);
        requestSizeDistribution = createSizeDistribution(Metric.REQ_SIZE_DISTRIBUTION);

    }

    private DistributionSummary createSizeDistribution(final Metric metric) {
        return DistributionSummary
                .builder(metric.getName())
                .description(metric.getDescr())
                .baseUnit("bytes")
                .scale(100)
                .register(registry);
    }

    private DynamicCounter buildCounter(Metric metric) {
        return DynamicCounter.builder(registry, metric.getName())
                .customizer(b -> b.description(metric.getDescr()))
                .tagKeys(SOURCE_TAG_NAME)
                .build();
    }

    //is not usefull for Spring boot 2.7.*
    /**@PostConstruct
    public void setupHikariWithMetrics() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).setMetricRegistry(registry);
            //((HikariDataSource) dataSource).setHealthCheckRegistry(registry);
        }
    }*/

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
}
