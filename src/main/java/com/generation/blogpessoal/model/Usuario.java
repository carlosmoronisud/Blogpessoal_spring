package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "tb_usuario") //CREATE TABLE tb_postagens();
public class Usuario {
			
		@Id // Primary Key
		@GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
		private Long id;
		
		@Column(length = 100)
		@NotBlank(message = "O atributo nome é obrigatório!")
		@Size(min = 1, max = 255, message =  "O atributo nome de ter no minimo 5 e no maximo 255 caracteres!")
		private String nome;
		
		@Column(length = 100)
		@NotBlank(message = "O atributo usuario é obrigatório!")
		@Size(min = 1, max = 255, message =  "O email de ter no minimo 5 e no maximo 255 caracteres!")
		@Email(message = "O Atributo Usuário deve ser um e-mail válido!")
		private String usuario; // Email
		
		@Column(length = 1000)
		@NotBlank(message = "O atributo título é obrigatório!")
		@Size(min = 1,  message = "O Atributo Senha deve conter no mínimo 8 caracteres")
		private String senha;
		
		@Size(max = 5000, message = "O Atributo Foto não pode ter mais de 5000 caracteres")
	    private String foto;
		
		// Relacionamento OneToMany com Postagem
	    // Um Usuário pode ter muitas Postagens
	    // Mapeado pelo campo 'usuario' na classe Postagem (a classe Postagem terá um campo 'private Usuario usuario;')
		@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    	@JsonIgnoreProperties("usuario") // Evita recursão infinita ao serializar/deserializar JSON
	    private List<Postagem> postagem;
		
		
		
		// Teste 1 - Construtor vazio padrão, necessário para o JPA/Hibernate 
	    public Usuario() {
		       
		    }
	    
		public Usuario(
				@NotBlank(message = "O atributo usuario é obrigatório!") @Size(min = 5, max = 255, message = "O email de ter no minimo 5 e no maximo 255 caracteres!") @Email(message = "O Atributo Usuário deve ser um e-mail válido!") String usuario) {
			super();
			this.usuario = usuario;
		}
		
		// ------------------------------------------
		
		
		public Usuario(Long id,
			@NotBlank(message = "O atributo nome é obrigatório!") @Size(min = 5, max = 255, message = "O atributo nome de ter no minimo 5 e no maximo 255 caracteres!") String nome,
			@NotBlank(message = "O atributo usuario é obrigatório!") @Size(min = 5, max = 255, message = "O email de ter no minimo 5 e no maximo 255 caracteres!") @Email(message = "O Atributo Usuário deve ser um e-mail válido!") String usuario,
			@NotBlank(message = "O atributo título é obrigatório!") @Size(min = 8, message = "O Atributo Senha deve conter no mínimo 8 caracteres") String senha,
			@Size(max = 5000, message = "O Atributo Foto não pode ter mais de 5000 caracteres") String foto,
			List<Postagem> postagem) {
			super();
			this.id = id;
			this.nome = nome;
			this.usuario = usuario;
			this.senha = senha;
			this.foto = foto;
			this.postagem = postagem;
			
			
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getUsuario() {
			return usuario;
		}

		public void setUsuario(String usuario) {
			this.usuario = usuario;
		}

		public String getSenha() {
			return senha;
		}

		public void setSenha(String senha) {
			this.senha = senha;
		}

		public String getFoto() {
			return foto;
		}

		public void setFoto(String foto) {
			this.foto = foto;
		}

		public List<Postagem> getPostagem() {
			return postagem;
		}

		public void setPostagem(List<Postagem> postagem) {
			this.postagem = postagem;
		}
		

}
