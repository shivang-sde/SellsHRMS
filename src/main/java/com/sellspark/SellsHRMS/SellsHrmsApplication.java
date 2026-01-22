package com.sellspark.SellsHRMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SellsHrmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SellsHrmsApplication.class, args);

		

	}

}
