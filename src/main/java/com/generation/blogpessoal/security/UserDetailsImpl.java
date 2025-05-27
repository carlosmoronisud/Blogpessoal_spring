package com.generation.blogpessoal.security; // Declara o pacote onde a classe está localizada.
// É uma boa prática organizar classes relacionadas à segurança em um pacote '.security'.

import java.util.Collection; // Importa a interface Collection, usada para grupos de objetos (especificamente para as autoridades/roles do usuário).
import java.util.List; // Importa a interface List, um tipo de Collection que mantém a ordem (poderia ser usada para a lista de autoridades).

import org.springframework.security.core.GrantedAuthority; // Interface central do Spring Security que representa uma permissão ou role (papel) concedida a um usuário autenticado.
import org.springframework.security.core.userdetails.UserDetails; // Interface FUNDAMENTAL do Spring Security que define um "usuário".
// É a ponte entre o seu modelo de usuário e o mecanismo de autenticação e autorização do Spring Security.

import com.generation.blogpessoal.model.Usuario; // Importa sua classe de modelo 'Usuario', que representa o usuário no seu banco de dados.

// Esta classe 'UserDetailsImpl' é uma implementação CONCRETA da interface 'UserDetails' do Spring Security.
// Seu papel principal na aplicação atual é ATUAR COMO UM ADAPTADOR:
// Ela pega um objeto do seu modelo de domínio (a entidade 'Usuario' que vem do banco de dados)
// e o "traduz" para um formato que o Spring Security pode entender e usar para gerenciar o processo de autenticação e autorização.

// Conceito para Reuso em outros projetos:
// Em QUALQUER projeto Spring Security onde você tiver seus próprios usuários armazenados em um banco de dados (ou outra fonte),
// você SEMPRE precisará de uma implementação da interface 'UserDetails'. Esta classe 'UserDetailsImpl' serve como um
// TEMPLATE EXCELENTE para essa implementação. Você apenas adaptaria o construtor e os getters para o seu modelo de usuário
// específico (`user.getSeuCampoDeUsuario()` e `user.getSeuCampoDeSenha()`).
public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L; // Um identificador de versão para classes serializáveis.
	// É uma boa prática incluí-lo quando uma classe implementa 'Serializable' (o que 'UserDetails' indiretamente faz),
	// para garantir compatibilidade se a classe mudar e for serializada/desserializada.

	private String username; // Armazenará o nome de usuário que o Spring Security usará para identificação.
	// No seu caso, este será o campo 'usuario' (o e-mail) da sua entidade 'Usuario'.
	private String password; // Armazenará a senha do usuário.
	// ESSENCIAL: Esta senha DEVE ser a senha JÁ CRIPTOGRAFADA (hashed) como armazenada no seu banco de dados.
	private List<GrantedAuthority> authorities; // Armazenará as autoridades (roles/papéis) que o usuário possui.
	// Ex: ROLE_ADMIN, ROLE_USER. No projeto atual, esta lista pode estar vazia ou não populada explicitamente,
	// mas em projetos mais complexos, é aqui que as permissões do usuário seriam definidas para autorização.

	// Construtor principal.
	// Este construtor é projetado para receber um objeto 'Usuario' (vindo do seu banco de dados).
	// Ele é o ponto onde o seu modelo de usuário é "adaptado" para o formato 'UserDetails'.
	public UserDetailsImpl(Usuario user) {
		// Mapeia o campo 'usuario' (e-mail) da sua entidade 'Usuario' para o 'username' que o Spring Security espera.
		this.username = user.getUsuario();
		// Mapeia o campo 'senha' (já criptografada) da sua entidade 'Usuario' para o 'password' que o Spring Security espera.
		this.password = user.getSenha();
		// Inicializa a lista de autoridades. Por padrão, pode ser vazia se não houver roles específicas.
		// Para reuso: Se seu novo projeto tiver roles, você populava essa lista aqui (ex: authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"))).
		this.authorities = null; // Ou Collections.emptyList() se não houver roles.
	}

	// Construtor vazio (default constructor).
	// Muitas vezes necessário para frameworks que usam reflexão para instanciar objetos (como Spring ou JPA),
	// mesmo que você não o chame explicitamente em seu código.
	public UserDetailsImpl() {	}

	// --- Implementação dos Métodos da Interface UserDetails ---
	// O Spring Security chamará esses métodos para obter as informações necessárias sobre o usuário.

	@Override // Indica que este método está sobrescrevendo um método da interface 'UserDetails'.
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Retorna a coleção de autoridades (roles/permissões) concedidas ao usuário.
		// Spring Security usa isso para determinar se um usuário tem permissão para acessar um recurso.
		// Em projetos futuros: Se você tiver roles como "ADMIN" ou "USER", elas seriam retornadas aqui.
		return authorities; // Retorna a lista de autoridades que foi populada (ou não) no construtor.
	}

	@Override
	public String getPassword() {
		// Retorna a senha do usuário.
		// O Spring Security usa esta senha (que DEVE ser a versão criptografada do banco)
		// para compará-la com a senha que o usuário forneceu durante o login.
		return password;
	}

	@Override
	public String getUsername() {
		// Retorna o nome de usuário único.
		// É o identificador principal do usuário para o Spring Security durante o processo de autenticação.
		// No seu caso, é o e-mail do usuário.
		return username;
	}

	// --- Métodos de Status da Conta (para controle mais granular sobre a validade e status do usuário) ---
	// Por padrão, todos retornam 'true' nesta implementação simples, significando que a conta está sempre ativa e válida.

	@Override
	public boolean isAccountNonExpired() {
		// Indica se a conta do usuário não expirou.
		// Reuso: Em projetos futuros, você pode ter uma lógica para desativar contas após um período.
		return true; // true = conta não expirada.
	}

	@Override
	public boolean isAccountNonLocked() {
		// Indica se a conta do usuário não está bloqueada.
		// Reuso: Pode ser usado para bloquear contas após N tentativas de login falhas ou por ação administrativa.
		return true; // true = conta não bloqueada.
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// Indica se as credenciais (senha) do usuário não expiraram.
		// Reuso: Pode ser usado para forçar usuários a trocar suas senhas periodicamente.
		return true; // true = credenciais não expiradas.
	}

	@Override
	public boolean isEnabled() {
		// Indica se o usuário está habilitado (ativo) no sistema.
		// Reuso: Permite desativar um usuário sem removê-lo completamente do banco de dados.
		return true; // true = usuário habilitado.
	}
}