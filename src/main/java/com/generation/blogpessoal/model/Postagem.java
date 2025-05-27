package com.generation.blogpessoal.model; // Declara o pacote onde a classe está localizada.
// É um modelo (entidade) JPA que será mapeada para uma tabela no banco de dados.

import java.time.LocalDateTime; // Importa a classe para trabalhar com data e hora.

import org.hibernate.annotations.UpdateTimestamp; // Anotação do Hibernate para atualizar automaticamente o timestamp.

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Anotação da biblioteca Jackson para controle de serialização JSON.

import jakarta.persistence.Column; // Anotação JPA para mapear um campo para uma coluna no banco de dados.
import jakarta.persistence.Entity; // Anotação JPA para indicar que esta classe é uma entidade.
import jakarta.persistence.GeneratedValue; // Anotação JPA para configurar a estratégia de geração de IDs.
import jakarta.persistence.GenerationType; // Enumeração para estratégias de geração de IDs.
import jakarta.persistence.Id; // Anotação JPA para marcar o campo como chave primária.
import jakarta.persistence.JoinColumn; // Anotação JPA para especificar a coluna da chave estrangeira.
import jakarta.persistence.ManyToOne; // Anotação JPA para mapear relacionamentos Muitos-para-Um.
import jakarta.persistence.Table; // Anotação JPA para especificar o nome da tabela no banco de dados.
import jakarta.validation.constraints.NotBlank; // Anotação de validação: campo não pode ser nulo, vazio ou conter apenas espaços em branco.
import jakarta.validation.constraints.Size; // Anotação de validação: para definir o tamanho mínimo e máximo de uma string.
 
@Entity // Indica ao JPA que esta classe é uma entidade e deve ser mapeada para uma tabela no banco de dados.
@Table(name = "tb_postagens") // Mapeia esta entidade para a tabela "tb_postagens" no banco de dados.
// Comentário da sua classe: //CREATE TABLE tb_postagens();
public class Postagem {
	
	// Relacionamento ManyToOne com Tema:
	// Uma Postagem pertence a um Tema.
	@ManyToOne // Anotação que define o lado "muitos" de um relacionamento Muitos-para-Um.
	// @JoinColumn(name = "tema_id") // Embora não esteja no seu código, é uma boa prática para definir o nome da coluna da chave estrangeira.
	@JsonIgnoreProperties("postagem") // CRUCIAL para evitar loops infinitos de serialização JSON.
	// Quando uma Postagem é serializada, Jackson vai ignorar o atributo 'postagem' dentro do objeto 'Tema'
	// para evitar o loop Tema -> Postagem -> Tema.
	private Tema tema; // Atributo que representa o Tema ao qual esta Postagem pertence.
	
	// NOVO ATRIBUTO: Relacionamento ManyToOne com Usuario
	// Esta é a CORREÇÃO para o erro "mappedBy... does not exist in the target entity".
	// Uma Postagem pertence a um Usuário.
	@ManyToOne // Define o lado "muitos" de um relacionamento Muitos-para-Um.
	@JoinColumn(name = "usuario_id") // Define o nome da coluna da chave estrangeira na tabela 'tb_postagens' que apontará para o ID do usuário.
	// O nome 'usuario_id' é uma convenção comum e corresponde ao que o 'mappedBy' da classe Usuario espera.
	@JsonIgnoreProperties({"postagem", "senha"}) // CRUCIAL para evitar loops infinitos de serialização JSON e expor dados sensíveis.
	// Quando uma Postagem é serializada, Jackson vai:
	// - Ignorar o atributo 'postagem' dentro do objeto 'Usuario' (para evitar o loop Usuario -> Postagem -> Usuario).
	// - Ignorar o atributo 'senha' dentro do objeto 'Usuario' (para nunca expor a senha do usuário).
	private Usuario usuario; // Este é o atributo 'usuario' que o 'mappedBy = "usuario"' na classe Usuario estava procurando!
		
	@Id // Marca o atributo 'id' como a chave primária da tabela.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a estratégia de geração do valor do ID.
	// GenerationType.IDENTITY: O banco de dados gerará automaticamente um valor único (auto-incremento) para a chave primária.
	// Comentário da sua classe: // AUTO_INCREMENT
	private Long id; // Chave primária da Postagem. Tipo Long é o mais adequado para IDs em Java.
	
	@Column(length = 100) // Define o comprimento máximo da coluna 'titulo' no banco de dados como 100 caracteres.
	@NotBlank(message = "O atributo título é obrigatório!") // Validação: Garante que o campo 'titulo' não seja nulo, vazio ou contenha apenas espaços em branco.
	@Size(min = 1, max = 100, message = "O atributo título deve ter no mínimo 5 e no máximo 100 caracteres!") // Validação: Define o tamanho mínimo e máximo da string 'titulo'.
	private String titulo; // Atributo para o título da postagem.
	
	@Column(length = 1000) // Define o comprimento máximo da coluna 'texto' no banco de dados como 1000 caracteres.
	@NotBlank(message = "O atributo texto é obrigatório!") // Validação: Garante que o campo 'texto' não seja nulo, vazio ou contenha apenas espaços em branco.
	@Size(min = 1, max = 1000, message = "O atributo texto deve ter no mínimo 10 e no máximo 1000 caracteres!") // Validação: Define o tamanho mínimo e máximo da string 'texto'.
	// OBS: A mensagem de erro em seu código original para 'texto' estava "O atributo título de ter...", corrija para "O atributo texto deve ter...".
	private String texto; // Atributo para o conteúdo do texto da postagem.
	
	@UpdateTimestamp // Anotação do Hibernate que faz com que este campo seja preenchido automaticamente com a data e hora
	// atuais sempre que a entidade for criada ou atualizada no banco de dados.
	private LocalDateTime data; // Atributo para armazenar a data e hora da postagem.

	// Construtor Vazio (Default Constructor):
	// É NECESSÁRIO para o JPA/Hibernate poder instanciar objetos da entidade quando recupera dados do banco de dados.
	public Postagem() {}

	// --- Métodos Getters e Setters ---
	// São métodos públicos que permitem acessar (get) e modificar (set) os valores dos atributos privados da classe.
	// Essenciais para o JPA e para a serialização/desserialização JSON (Jackson).

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setText(String texto) { // Correção aqui: deve ser setTexto(String texto)
		this.texto = texto;
	}
	// Correção: O método correto deve ser setTexto para o atributo texto.
	public void setTexto(String texto) {
		this.texto = texto;
	}


	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public Usuario getUsuario() { // Getter para o novo atributo 'usuario'.
		return usuario;
	}

	public void setUsuario(Usuario usuario) { // Setter para o novo atributo 'usuario'.
		this.usuario = usuario;
	}
}