package com.generation.blogpessoal.model; // Declara o pacote onde a classe está localizada.
// Por ser um objeto que representa dados (neste caso, dados de login e autenticação), é comum que esteja no pacote '.model'.

// Esta classe, 'UsuarioLogin', é um Data Transfer Object (DTO).
// Ela não é uma entidade JPA (@Entity) e não será mapeada diretamente para uma tabela no banco de dados.
// Seu propósito principal é:
// 1. Receber as credenciais (usuário e senha) do cliente (frontend) no momento do login.
// 2. Enviar de volta ao cliente as informações do usuário autenticado, incluindo o token JWT.
public class UsuarioLogin {
	
	private Long id; // Atributo para armazenar o ID do usuário após o login (para ser retornado ao cliente).
	private String nome; // Atributo para armazenar o nome do usuário após o login (para ser retornado ao cliente).
	private String usuario; // Atributo para armazenar o nome de usuário (geralmente o e-mail) fornecido para login e/ou retornado ao cliente.
	private String senha; // Atributo para armazenar a senha fornecida para login. (Importante: esta senha não será armazenada nem retornada em texto puro no fluxo real).
	private String foto; // Atributo para armazenar a URL da foto do usuário após o login (para ser retornado ao cliente).
	private String token; // Atributo CRUCIAL: onde o Token JWT (Bearer Token) será armazenado após a autenticação bem-sucedida, para ser enviado de volta ao cliente.

	// Início dos Métodos Getters e Setters.
	// Estes métodos são fundamentais para que o Spring (especificamente o Jackson, que lida com JSON)
	// consiga mapear os dados do corpo da requisição HTTP (JSON) para este objeto,
	// e também para serializar este objeto de volta para JSON na resposta HTTP.

	public Long getId() {
		// Getter para o ID.
		return this.id;
	}

	public void setId(Long id) {
		// Setter para o ID.
		this.id = id;
	}

	public String getNome() {
		// Getter para o nome.
		return this.nome;
	}

	public void setNome(String nome) {
		// Setter para o nome.
		this.nome = nome;
	}

	public String getUsuario() {
		// Getter para o nome de usuário (e-mail).
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		// Setter para o nome de usuário (e-mail).
		this.usuario = usuario;
	}

	public String getSenha() {
		// Getter para a senha.
		// Importante: No fluxo real de autenticação, a senha recebida será verificada
		// contra a senha criptografada no banco. A senha original NUNCA deve ser
		// armazenada ou retornada publicamente.
		return this.senha;
	}

	public void setSenha(String senha) {
		// Setter para a senha.
		// Quando o cliente envia a senha, ela é definida aqui temporariamente.
		// Antes de ser usada para comparação, ela passará pelo BCryptPasswordEncoder.
		this.senha = senha;
	}

	public String getFoto() {
		// Getter para a URL da foto.
		return this.foto;
	}

	public void setFoto(String foto) {
		// Setter para a URL da foto.
		this.foto = foto;
	}

	public String getToken() {
		// Getter para o token JWT.
		// Este método é usado para recuperar o token gerado após a autenticação bem-sucedida,
		// que será incluído na resposta HTTP para o cliente.
		return this.token;
	}

	public void setToken(String token) {
		// Setter para o token JWT.
		// O token gerado pelo 'JwtService' e adicionado pelo 'UsuarioService' será definido aqui.
		this.token = token;
	}

	// Geralmente, para DTOs, não é necessário um construtor vazio explícito ou outros construtores,
	// a menos que haja uma lógica de inicialização específica. Jackson consegue instanciar
	// DTOs usando setters se um construtor vazio estiver disponível (que é o construtor padrão
	// se nenhum outro construtor for explicitamente definido).
}