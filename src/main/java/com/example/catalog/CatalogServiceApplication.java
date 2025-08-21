package com.example.catalog;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}


	//use below code when you don't see active profile in terminal
	/* @Autowired
	private Environment environment;
	public void run(String... args) {
		System.out.println("🚀 Active Profile: " + Arrays.toString(environment.getActiveProfiles()));
	}*/
}
