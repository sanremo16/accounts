package org.san.home.accounts;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.san.home.accounts.monitoring.MonitoringUtilsService;
import org.san.home.accounts.service.AccountService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
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
    private AccountService accountService;

    @Autowired
    MeterRegistry registry;

    @Test
    @Order(2)
    @SneakyThrows
    @DatabaseSetup({"/dataset/account.xml"})
    public void successCounter() {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/list")).andDo(print());
        assertEquals(1, getMeterValue(MonitoringUtilsService.SUCCESS_REQ_COUNTER_METRIC_NAME));
        assertEquals(1, getMeterValue(MonitoringUtilsService.FAILED_REQ_COUNTER_METRIC_NAME));
        assertEquals(1, getMeterValue(MonitoringUtilsService.ERROR_COUNTER_METRIC_NAME));
        assertEquals(0, getMeterValue(MonitoringUtilsService.TIMEOUT_COUNTER_METRIC_NAME));
        assertEquals(0, getMeterValue(MonitoringUtilsService.REQ_ACTIVE_GAUGE_METRIC_NAME));

    }

    @Test
    @Order(1)
    @SneakyThrows
    public void errorsCounter() {
        this.mockMvc.perform(get("http://localhost:"+ port + "/accounts/show/999")).andDo(print());
        assertEquals(0, getMeterValue(MonitoringUtilsService.SUCCESS_REQ_COUNTER_METRIC_NAME));
        assertEquals(1, getMeterValue(MonitoringUtilsService.FAILED_REQ_COUNTER_METRIC_NAME));
        assertEquals(1, getMeterValue(MonitoringUtilsService.ERROR_COUNTER_METRIC_NAME));
        assertEquals(0, getMeterValue(MonitoringUtilsService.TIMEOUT_COUNTER_METRIC_NAME));
        assertEquals(0, getMeterValue(MonitoringUtilsService.REQ_ACTIVE_GAUGE_METRIC_NAME));

    }

    private double getMeterValue(String meterName) {
        return ((SimpleMeterRegistry)registry).getMeters().stream()
                .filter((m -> meterName.equals(m.getId().getName())))
                .findFirst().get().measure().iterator().next().getValue();
    }

}