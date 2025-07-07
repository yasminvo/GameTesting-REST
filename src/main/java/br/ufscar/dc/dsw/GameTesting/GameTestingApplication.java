package br.ufscar.dc.dsw.GameTesting;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@OpenAPIDefinition(
		info = @Info(
				title = "Game Testing",
				version = "1",
				description = "API responsável pelo gerenciamento de sessões de teste do Game Testing."
		)
)
@SpringBootApplication
public class GameTestingApplication {

	private static final Logger log = LoggerFactory.getLogger(GameTestingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GameTestingApplication.class, args);
	}

}
