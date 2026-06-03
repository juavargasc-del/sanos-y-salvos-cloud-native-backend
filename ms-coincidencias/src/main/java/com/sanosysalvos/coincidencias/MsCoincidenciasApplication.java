package com.sanosysalvos.coincidencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCoincidenciasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCoincidenciasApplication.class, args);
	}

}
