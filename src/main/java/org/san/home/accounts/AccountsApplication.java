package org.san.home.accounts;

import org.san.home.accounts.config.ApiConfig;
import org.san.home.accounts.config.MonitoringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Import({MonitoringConfig.class, ApiConfig.class})
@EnableJpaRepositories
@SpringBootApplication
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
