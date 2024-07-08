package com.group15.roborally.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.group15.roborally.common.model"})
@EnableJpaRepositories(basePackages = {"com.group15.roborally.server.repository"})
public class StartServer {
	public static void main(String[] args) {
		SpringApplication.run(StartServer.class, args);
	}
}
