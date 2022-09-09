package org.san.home.accounts.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                //.withDetail("status", "pass")
                .build();
    }
}
