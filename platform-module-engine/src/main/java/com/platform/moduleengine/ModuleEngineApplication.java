package com.platform.moduleengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
    "com.platform.moduleengine",
    "com.platform.core"
})
public class ModuleEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleEngineApplication.class, args);
    }
}