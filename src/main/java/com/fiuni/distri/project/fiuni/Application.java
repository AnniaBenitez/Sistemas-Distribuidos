package com.fiuni.distri.project.fiuni;

import com.fiuni.distri.project.fiuni.config.AuthConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.security.PublicKey;

@EnableCaching
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import({AuthConfig.class})
public class Application {
	public static boolean LANZAR_EXCEPCION = false;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
