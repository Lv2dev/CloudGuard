package com.lv2dev.cloudguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.lv2dev.cloudguard.persistence")
public class CloudGuardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGuardApplication.class, args);
	}

}
