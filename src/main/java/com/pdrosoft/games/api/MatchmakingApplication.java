package com.pdrosoft.games.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = { //
		"com.pdrosoft.games.api.controller", //
		"com.pdrosoft.games.api.service", //
		"com.pdrosoft.games.api.dao", //
		"com.pdrosoft.games.api.security", //
		"com.pdrosoft.games.api.exception", //

		"com.pdrosoft.games.api.stratego.controller", //
		"com.pdrosoft.games.api.stratego.service", //
		"com.pdrosoft.games.api.stratego.dao", //
		
		"com.pdrosoft.games.api.swagger", //
		"com.pdrosoft.games.api.chat.config", //
		"com.pdrosoft.games.api.chat.service", //
})
public class MatchmakingApplication {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") //
						.allowedOrigins("*") //
						.allowedMethods("*") //
						.allowedHeaders("Authorization", "Content-Type") //
						.exposedHeaders("Authorization");
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingApplication.class, args);
	}

}
