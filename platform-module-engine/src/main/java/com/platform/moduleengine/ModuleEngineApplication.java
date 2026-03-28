package com.platform.moduleengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@ComponentScan(basePackages = {
	    "com.platform.moduleengine",
	    "com.platform.core"
	})
	@EntityScan(basePackages = {        
	    "com.platform.moduleengine",
	    "com.platform.core"
	})
	@EnableJpaRepositories(basePackages = {
	    "com.platform.moduleengine",
	    "com.platform.core"
	})
public class ModuleEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModuleEngineApplication.class, args);
    }
}