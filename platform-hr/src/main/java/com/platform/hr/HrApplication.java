package com.platform.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.platform.hr",
    "com.platform.core"
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
    "com.platform.hr",
    "com.platform.core"
})
@EntityScan(basePackages = {
    "com.platform.hr",
    "com.platform.core"
})
public class HrApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrApplication.class, args);
    }
}
