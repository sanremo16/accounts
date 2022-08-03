package org.san.home.accounts.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;

@Component
@Order(100)
public class MonitoringServletFilter implements Filter {
    @Autowired
    private MonitoringUtilsService monitoringUtilsService;
    public static final String SOURCE_HEADER_NAME = "module_id";
    private static final String UNKNOWN_SOURCE = "unknown";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            monitoringUtilsService.incrementRequestsActiveCounter();
            chain.doFilter(request, response);
        } finally {
            processHttpResponseStatus((HttpServletRequest) request, (HttpServletResponse) response);
            monitoringUtilsService.decrementRequestsActiveCounter();
        }
    }

    private void processHttpResponseStatus(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        final int status = response.getStatus();
        final String source = Objects.requireNonNullElse(request.getHeader(SOURCE_HEADER_NAME), UNKNOWN_SOURCE);
        if (HttpStatus.OK.value() == status || HttpStatus.CREATED.value() == status) {
            monitoringUtilsService.getSuccessRequestsCounter().getOrCreate(source).increment();
        } else if (HttpStatus.REQUEST_TIMEOUT.value() == status) {
            monitoringUtilsService.getTimeoutCounter().getOrCreate(source).increment();
            monitoringUtilsService.getRequestsFailedCounter().getOrCreate(source).increment();
        } else {
            monitoringUtilsService.getErrorsCounter().getOrCreate(source).increment();
            monitoringUtilsService.getRequestsFailedCounter().getOrCreate(source).increment();
        }
    }
}
