package com.generation.blogpessoal.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_tema") //CREATE TABLE tb_tema();
public class Tema {
	

	@OneToMany(mappedBy = "tema", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // Define o lado 1:N
	private List<Postagem> postagem;
	
	

	
	
	
	
	@Id // Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
	private Long id;
	
	@Column(length = 100)
	@NotBlank( message = "O atributo é obrigatório")
	@Size(min = 10, max = 1000, message = "O atributo deve ter no mínimo 10 e no maximo 100 caracteres")
	private String descricao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Postagem> getPostagem() {
		return postagem;
	}

	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	

}
