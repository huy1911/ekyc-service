package com.lpb.mid.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.lpb.mid")
@EnableJpaRepositories(basePackages = "com.lpb.mid")
public class EkycServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkycServiceApplication.class, args);
	}

}
