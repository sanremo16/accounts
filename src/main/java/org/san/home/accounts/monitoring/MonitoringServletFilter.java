package org.san.home.accounts.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Component
@Order(100)
public class MonitoringServletFilter implements Filter {
    @Autowired
    private MonitoringUtilsService monitoringUtilsService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            monitoringUtilsService.incrementRequestsActiveCounter();
            chain.doFilter(request, response);
        } finally {
            processHttpResponseStatus((HttpServletResponse) response);
            monitoringUtilsService.decrementRequestsActiveCounter();
        }
    }

    private void processHttpResponseStatus(@NotNull HttpServletResponse response) {
        int status = response.getStatus();
        if (HttpStatus.OK.value() == status || HttpStatus.CREATED.value() == status) {
            monitoringUtilsService.getSuccessRequestsCounter().increment();
        } else if (HttpStatus.REQUEST_TIMEOUT.value() == status) {
            monitoringUtilsService.getTimeoutCounter().increment();
            monitoringUtilsService.getRequestsFailedCounter().increment();
        } else {
            monitoringUtilsService.getErrorsCounter().increment();
            monitoringUtilsService.getRequestsFailedCounter().increment();
        }
    }
}
