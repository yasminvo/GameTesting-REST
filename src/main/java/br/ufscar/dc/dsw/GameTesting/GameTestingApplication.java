package br.ufscar.dc.dsw.GameTesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GameTestingApplication {

	private static final Logger log = LoggerFactory.getLogger(GameTestingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GameTestingApplication.class, args);
	}

}
