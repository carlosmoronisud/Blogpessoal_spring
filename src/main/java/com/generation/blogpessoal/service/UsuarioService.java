package com.generation.blogpessoal.service; // Declara o pacote onde a classe está. Indica que é parte da camada de serviço.

import java.util.List; // Importa List para coleções de objetos.
import java.util.Optional; // Importa Optional para lidar com valores que podem ser nulos, de forma segura.

import org.springframework.beans.factory.annotation.Autowired; // Anotação para injeção de dependência automática.
import org.springframework.http.HttpStatus; // Enumeração com códigos de status HTTP (usada aqui para exceções).
import org.springframework.security.authentication.AuthenticationManager; // Componente do Spring Security para gerenciar o processo de autenticação.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Objeto que encapsula o nome de usuário e a senha para autenticação.
import org.springframework.security.core.Authentication; // Interface que representa uma autenticação bem-sucedida ou em andamento.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Classe do Spring Security para criptografar senhas usando o algoritmo BCrypt.
import org.springframework.stereotype.Service; // Anotação para indicar que esta classe é um componente de serviço do Spring.
import org.springframework.web.server.ResponseStatusException; // Exceção do Spring para retornar um status HTTP específico em caso de erro.

import com.generation.blogpessoal.model.Usuario; // Importa a classe de modelo Usuario.
import com.generation.blogpessoal.model.UsuarioLogin; // Importa a classe de modelo UsuarioLogin.
import com.generation.blogpessoal.repository.UsuarioRepository; // Importa a interface do repositório de usuário para interagir com o banco de dados.
import com.generation.blogpessoal.security.JwtService; // Importa a classe de serviço JWT que você (ou o professor) criará para gerar os tokens JWT.

@Service // Esta anotação marca a classe como um "Service" (Serviço). Isso a torna um componente gerenciado pelo Spring, permitindo que seja injetada em outros componentes (como o UsuarioController).
// A camada de Serviço é onde a lógica de negócio complexa e a orquestração de operações (como interações com o banco de dados via Repository) são realizadas.
public class UsuarioService {

	@Autowired // Injeção de dependência: O Spring injeta uma instância do UsuarioRepository.
	private UsuarioRepository usuarioRepository; // Objeto para interagir com o banco de dados (buscar, salvar, etc. usuários).

	@Autowired // Injeção de dependência: O Spring injeta uma instância do JwtService.
	private JwtService jwtService; // Objeto para lidar com a geração e, possivelmente, validação de JSON Web Tokens (JWT).

	@Autowired // Injeção de dependência: O Spring injeta uma instância do AuthenticationManager.
	private AuthenticationManager authenticationManager; // Componente central do Spring Security para autenticar usuários. Ele coordenará a busca e verificação de credenciais.

	// Método para buscar todos os usuários.
	public List<Usuario> getAll() {
		// Delega a busca para o repositório, que interage com o banco de dados.
		return usuarioRepository.findAll();
	}

	// Método para buscar um usuário pelo ID.
	public Optional<Usuario> getById(Long id) {
		// Delega a busca para o repositório. Retorna um Optional, pois o usuário pode não existir.
		return usuarioRepository.findById(id);
	}

	// Método para cadastrar um novo usuário.
	// Recebe um objeto Usuario que vem da requisição.
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		// Primeiro, verifica se já existe um usuário com o mesmo 'usuario' (que é o e-mail/username) no banco de dados.
		// 'findByUsuario' é um método que você precisará definir na sua interface UsuarioRepository.
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			// Se um usuário com o mesmo e-mail já existir, retorna um Optional vazio, indicando que o cadastro não pode prosseguir.
			// No Controller, isso será mapeado para um HttpStatus.BAD_REQUEST.
			return Optional.empty();
		}

		// Se o e-mail não estiver em uso, a senha do usuário é criptografada antes de ser salva no banco.
		// Isso é CRÍTICO para a segurança: NUNCA armazene senhas em texto puro!
		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		// Salva o novo usuário (com a senha criptografada) no banco de dados.
		// 'Optional.ofNullable' cria um Optional a partir do resultado, que pode ser nulo em alguns casos (embora 'save' geralmente não retorne nulo se a operação for bem-sucedida).
		return Optional.ofNullable(usuarioRepository.save(usuario));
	}

	// Método para atualizar um usuário existente.
	// Recebe um objeto Usuario com as informações a serem atualizadas. O ID do usuário DEVE estar presente neste objeto.
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		// Verifica se o usuário com o ID fornecido realmente existe no banco de dados.
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {

			// Se o usuário existir, verifica se o e-mail (campo 'usuario') que ele está tentando usar para atualização
			// já pertence a outro usuário (evitando que dois usuários tenham o mesmo e-mail).
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			// Se 'buscaUsuario' estiver presente (ou seja, existe outro usuário com o mesmo e-mail) E
			// o ID do usuário encontrado com esse e-mail for DIFERENTE do ID do usuário que está sendo atualizado:
			if ( (buscaUsuario.isPresent()) && ( buscaUsuario.get().getId() != usuario.getId()))
				// Lança uma exceção com status HTTP 400 BAD REQUEST, indicando que o e-mail já está em uso por outro usuário.
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			// Se a validação de e-mail duplicado passar, a senha do usuário é criptografada.
			// Isso é importante caso a senha também tenha sido alterada na atualização.
			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			// Salva as alterações no banco de dados.
			// 'Optional.ofNullable' envolve o usuário salvo em um Optional.
			return Optional.ofNullable(usuarioRepository.save(usuario));
			
		}

		// Se o usuário com o ID fornecido para atualização não for encontrado no banco, retorna um Optional vazio.
		// No Controller, isso será mapeado para um HttpStatus.NOT_FOUND.
		return Optional.empty();
	}

	// Método para autenticar um usuário (processo de login).
	// Recebe um Optional<UsuarioLogin> que contém o usuário (e-mail) e a senha.
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		// Cria um objeto 'UsernamePasswordAuthenticationToken' com as credenciais do usuário.
		// Este objeto é o que o Spring Security usa para representar a tentativa de autenticação.
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
				usuarioLogin.get().getSenha());

		// Tenta autenticar o usuário usando o 'AuthenticationManager' do Spring Security.
		// O 'authenticationManager' vai encontrar um 'AuthenticationProvider' (como o DaoAuthenticationProvider)
		// que sabe como verificar essas credenciais (ex: buscando no banco e comparando senhas).
		Authentication authentication = authenticationManager.authenticate(credenciais);

		// Se a autenticação for bem-sucedida (as credenciais são válidas):
		if (authentication.isAuthenticated()) {

			// Busca o usuário completo no banco de dados novamente usando o e-mail/username.
			// Isso é feito para obter todos os detalhes do usuário (ID, nome, foto) que podem ser retornados ao cliente.
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

			// Se o usuário for encontrado no banco (o que deve acontecer se a autenticação foi bem-sucedida):
			if (usuario.isPresent()) {

				// Preenche o objeto 'UsuarioLogin' que será retornado ao cliente com as informações do usuário.
				usuarioLogin.get().setId(usuario.get().getId()); // Seta o ID do usuário.
				usuarioLogin.get().setNome(usuario.get().getNome()); // Seta o nome do usuário.
				usuarioLogin.get().setFoto(usuario.get().getFoto()); // Seta a URL da foto do usuário.
				usuarioLogin.get().setSenha(""); // Limpa a senha do objeto 'UsuarioLogin' antes de retornar, por segurança (NUNCA retorne a senha!).
				// Gera o Token JWT para o usuário autenticado e o adiciona ao objeto 'UsuarioLogin'.
				usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario())); // Usa o método privado 'gerarToken'.

				// Retorna o Optional contendo o 'UsuarioLogin' completo (com o token).
				return usuarioLogin;
			}
		}

		// Se a autenticação falhar (usuário não encontrado ou senha incorreta), retorna um Optional vazio.
		// No Controller, isso será mapeado para um HttpStatus.UNAUTHORIZED.
		return Optional.empty();
	}

	// Método privado auxiliar para gerar o Token JWT.
	// Recebe o 'usuario' (e-mail/username) como sujeito do token.
	private String gerarToken(String usuario) {
		// Concatena "Bearer " (com o espaço) com o token JWT gerado pelo JwtService.
		// Isso segue o formato padrão de um Bearer Token para o cabeçalho Authorization.
		return "Bearer " + jwtService.generateToken(usuario);
	}

	// Método privado auxiliar para criptografar uma senha.
	// Usa o 'BCryptPasswordEncoder' do Spring Security, que é robusto e recomendado para hash de senhas.
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // Cria uma nova instância do encoder.
		return encoder.encode(senha); // Retorna a senha criptografada (hashed).
	}
}