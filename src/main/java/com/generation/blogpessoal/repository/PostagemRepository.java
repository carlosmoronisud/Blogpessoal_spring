// src/main/java/com/generation/blogpessoal/repository/PostagemRepository.java
package com.generation.blogpessoal.repository;

import java.util.List;

// IMPORTS CORRETOS PARA PAGINAÇÃO DO SPRING DATA JPA
import org.springframework.data.domain.Page; // Importar do pacote correto
import org.springframework.data.domain.Pageable; // Importar do pacote correto

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long>{
	
	List<Postagem> findAllByTituloContainingIgnoreCase(String titulo);

	// Não precisamos declarar este método explicitamente,
	// pois JpaRepository já fornece findAll(Pageable)
	// Mas se você quiser declará-lo para clareza, o tipo de retorno deve ser Page<Postagem>
	// Page<Postagem> findAll(Pageable pageable); // Se quiser declarar, use assim
	
	/* SELECT * FROM tb_postagens WHERE titulo LIKE "%?%"; */
			
}