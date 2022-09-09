package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.san.home.accounts.monitoring.MonitoringServletFilter;
import org.san.home.accounts.monitoring.MonitoringUtilsService;
import static org.san.home.accounts.monitoring.MonitoringUtilsService.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MicrometerTestConfiguration.class)
@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AccountMetricsTest {
    @LocalServerPort
    private int port;

    @LocalManagementPort
    private int mngPort;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MeterRegistry registry;

    @Test
    @SneakyThrows
    @DatabaseSetup({"/dataset/account.xml"})
    public void successCounter() {
        registry.clear();
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts")).andDo(print());
        assertEquals(1, getMeterValue(Metric.SUCCESS_REQ_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.FAILED_REQ_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.ERROR_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.TIMEOUT_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.REQ_ACTIVE_GAUGE.getName()));

    }

    @Test
    @SneakyThrows
    public void errorsCounter() {
        registry.clear();
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/999")).andDo(print());
        assertEquals(0, getMeterValue(Metric.SUCCESS_REQ_COUNTER.getName()));
        assertEquals(1, getMeterValue(Metric.FAILED_REQ_COUNTER.getName()));
        assertEquals(1, getMeterValue(Metric.ERROR_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.TIMEOUT_COUNTER.getName()));
        assertEquals(0, getMeterValue(Metric.REQ_ACTIVE_GAUGE.getName()));

    }

    private double getMeterValue(String meterName) {
        Optional<Meter> meter = registry.getMeters().stream()
                .filter((m -> meterName.equals(m.getId().getName())))
                .findFirst();
        return meter.isPresent() ? meter.get().measure().iterator().next().getValue() : 0d;
    }

    private String getMeterTagValue(String meterName, String tagName) {
        return registry.getMeters().stream()
                .filter((m -> meterName.equals(m.getId().getName())))
                .findFirst().get().getId().getTag(tagName);
    }


    @Test
    @SneakyThrows
    public void meterWithSourceHeader() {
        registry.clear();
        this.mockMvc.perform(
                MyTestRequestFactory.get("http://localhost:"+ port + "/accounts",
                    MonitoringServletFilter.SOURCE_HEADER_NAME, "source1")).andDo(print());
        registry.getMeters().stream().peek(m -> System.out.println(m.getId() + ", " + m.getId().getTag(MonitoringUtilsService.SOURCE_TAG_NAME))).collect(Collectors.toList());
        assertEquals("source1", getMeterTagValue(Metric.SUCCESS_REQ_COUNTER.getName(), MonitoringUtilsService.SOURCE_TAG_NAME));
    }

}
