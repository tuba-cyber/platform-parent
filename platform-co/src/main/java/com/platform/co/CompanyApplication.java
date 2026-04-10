package com.platform.co;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.platform.co",
    "com.platform.core"
})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
    "com.platform.co",
    "com.platform.core"
})
@EntityScan(basePackages = {
    "com.platform.co",
    "com.platform.core"
})
public class CompanyApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompanyApplication.class, args);
    }
}
