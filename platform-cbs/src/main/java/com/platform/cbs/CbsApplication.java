package com.platform.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
    "com.platform.cbs",
    "com.platform.core"
})
public class CbsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CbsApplication.class, args);
    }
}