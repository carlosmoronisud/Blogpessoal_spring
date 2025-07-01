// src/main/java/com/generation/blogpessoal/BlogPessoalApplication.java
package com.generation.blogpessoal;

import org.modelmapper.ModelMapper; // Adicione este import
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Adicione este import

@SpringBootApplication
public class BlogpessoalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogpessoalApplication.class, args);
	}

	// Adicione este método para configurar o ModelMapper como um Bean
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}