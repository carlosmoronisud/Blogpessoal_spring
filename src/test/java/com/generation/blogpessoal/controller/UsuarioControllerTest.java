package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals; // Importa o método estático assertEquals para asserções de igualdade
import static org.junit.jupiter.api.Assertions.assertNotNull; // Importa o método estático assertNotNull para asserções de não-nulidade
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional; // Importa a classe Optional para lidar com valores que podem ou não estar presentes

import org.junit.jupiter.api.BeforeAll; // Importa a anotação BeforeAll para configurar métodos que são executados uma vez antes de todos os testes
import org.junit.jupiter.api.DisplayName; // Importa a anotação DisplayName para fornecer um nome legível para os testes
import org.junit.jupiter.api.Test; // Importa a anotação Test para marcar métodos como testes
import org.junit.jupiter.api.TestInstance; // Importa a anotação TestInstance para configurar o ciclo de vida da instância de teste
import org.springframework.beans.factory.annotation.Autowired; // Importa a anotação Autowired para injeção de dependência
import org.springframework.boot.test.context.SpringBootTest; // Importa a anotação SpringBootTest para carregar o contexto da aplicação Spring Boot para testes
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment; // Importa a enum WebEnvironment para configurar o ambiente web do teste
import org.springframework.boot.test.web.client.TestRestTemplate; // Importa TestRestTemplate para realizar chamadas HTTP em testes de integração
import org.springframework.http.HttpEntity; // Importa HttpEntity para representar uma requisição ou resposta HTTP
import org.springframework.http.HttpMethod; // Importa HttpMethod para especificar o método HTTP (GET, POST, PUT, DELETE, etc.)
import org.springframework.http.HttpStatus; // Importa HttpStatus para representar os códigos de status HTTP
import org.springframework.http.ResponseEntity; // Importa ResponseEntity para representar a resposta HTTP completa
import org.springframework.web.bind.annotation.RequestBody;

import com.generation.blogpessoal.model.Usuario; // Importa a classe de modelo Usuario
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository; // Importa a interface do repositório UsuarioRepository
import com.generation.blogpessoal.service.UsuarioService; // Importa a classe de serviço UsuarioService
import com.generation.blogpessoal.util.TestBuilder; // Importa uma classe utilitária para construir objetos de teste

// Anotação que indica que esta é uma classe de teste Spring Boot.
// webEnvironment = WebEnvironment.RANDOM_PORT: Inicia um servidor web em uma porta aleatória,
// garantindo que os testes não interfiram uns com os outros se executados em paralelo.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Anotação que configura o ciclo de vida da instância de teste.
// TestInstance.Lifecycle.PER_CLASS: Uma única instância da classe de teste é criada e
// reutilizada para todos os métodos de teste. Isso permite que @BeforeAll e @AfterAll
// sejam usados em métodos não estáticos.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	// Injeta uma instância de TestRestTemplate, usada para fazer requisições HTTP
	// para a aplicação em teste.
	@Autowired
	private TestRestTemplate testRestTemplate;

	// Injeta uma instância de UsuarioService, usada para interagir com a lógica de negócio
	// dos usuários diretamente no teste (por exemplo, para cadastrar usuários de setup).
	@Autowired
	private UsuarioService usuarioService;

	// Injeta uma instância de UsuarioRepository, usada para interagir com o banco de dados
	// diretamente no teste (por exemplo, para limpar dados antes dos testes).
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// Constantes para credenciais de um usuário root e a URL base para as requisições de usuário.
	private static final String USUARIO_ROOT_EMAIL = "root@email.com";
	private static final String USUARIO_ROOT_SENHA = "rootroot";
	private static final String BASE_URL_USUARIOS = "/usuarios";

	// Método anotado com @BeforeAll, que é executado uma vez antes de todos os métodos de teste
	// nesta classe. É usado para configurar o ambiente de teste.
	
	@BeforeAll
	void start(){
		// Deleta todos os usuários existentes no banco de dados para garantir um estado limpo antes dos testes.
		usuarioRepository.deleteAll();
		// Cadastra um usuário "root" usando o serviço de usuário. Este usuário pode ser usado
		// para autenticação em testes que exigem permissões.
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
	}
	
	// Método de teste para verificar o cadastro de um novo usuário.
	@Test
	@DisplayName("Deve cadastrar um novo usuário com sucesso") // Nome legível para o teste
	public void deveCadastrarUsuario() {
		
		// Given (Dado): Prepara os dados de entrada para o teste.
		// Cria um novo objeto Usuario com dados de teste, usando a classe TestBuilder.
		Usuario usuario = TestBuilder.criarUsuario(null, "Paulo Antunes",
				"paulo_antunes@email.com.br", "13465278");

		// When (Quando): Executa a ação que está sendo testada.
		// Cria uma HttpEntity com o objeto Usuario, que será o corpo da requisição.
		HttpEntity<Usuario> requisicao = new HttpEntity<>(usuario);
		// Envia uma requisição POST para a URL de cadastro de usuários.
		// Espera uma resposta do tipo Usuario.
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

		// Then (Então): Verifica os resultados da ação.
		// AssertEquals: Verifica se o código de status da resposta é CREATED (201).
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		// AssertEquals: Verifica se o nome do usuário na resposta corresponde ao esperado.
		assertEquals("Paulo Antunes", resposta.getBody().getNome());
		// AssertEquals: Verifica se o email do usuário na resposta corresponde ao esperado.
		assertEquals("paulo_antunes@email.com.br", resposta.getBody().getUsuario());
	}
	// Método de teste para verificar a não duplicação de usuários.
		@Test
		@DisplayName("Não deve permitir duplicação do usuário") // Nome legível para o teste
		public void naoDeveDuplicarUsuario() {
			
			//Given (Dado): Prepara os dados de entrada.
			// Cria um novo objeto Usuario.
			Usuario usuario = TestBuilder.criarUsuario(null, "Maria da Silva",
					"maria_silva@email.com.br", "13465278");
			// Cadastra o usuário uma vez para simular um usuário já existente.
			usuarioService.cadastrarUsuario(usuario);

			//When (Quando): Executa a ação que está sendo testada.
			// Tenta cadastrar o mesmo usuário novamente.
			HttpEntity<Usuario> requisicao = new HttpEntity<>(usuario);
			ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
					BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);

			//Then (Então): Verifica os resultados.
			// AssertEquals: Espera que o código de status seja BAD_REQUEST (400), indicando
			// que a duplicação não foi permitida.
			assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		}
		// Método de teste para verificar a atualização de um usuário existente.
		@Test
		@DisplayName("Deve atualizar um usuário existente") // Nome legível para o teste
		public void deveAtualizarUmUsuario() {
			
			//Given (Dado): Prepara os dados.
			// Cria um usuário inicial para ser cadastrado e depois atualizado.
			Usuario usuario = TestBuilder.criarUsuario(null, "Juliana Andrews", "juliana_andrews@email.com.br",
					"juliana123");
			// Cadastra o usuário e obtém o Optional<Usuario> retornado pelo serviço.
			Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
			
			// Cria um objeto Usuario com os dados atualizados.
			// É importante usar o ID do usuário recém-cadastrado para a atualização.
			Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Juliana Ramos", 
					"juliana_ramos@email.com.br", "juliana123");

			//When (Quando): Executa a ação.
			// Cria uma HttpEntity com os dados do usuário atualizado.
			HttpEntity<Usuario> requisicao = new HttpEntity<>(usuarioUpdate);

			// Envia uma requisição PUT para a URL de atualização de usuários.
			// Usa autenticação básica com as credenciais do usuário root.
			ResponseEntity<Usuario> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
					.exchange(BASE_URL_USUARIOS + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);

			//Then (Então): Verifica os resultados.
			// AssertEquals: Verifica se o código de status da resposta é OK (200).
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
			// AssertEquals: Verifica se o nome do usuário na resposta foi atualizado corretamente.
			assertEquals("Juliana Ramos", resposta.getBody().getNome());
			// AssertEquals: Verifica se o email do usuário na resposta foi atualizado corretamente.
			assertEquals("juliana_ramos@email.com.br", resposta.getBody().getUsuario());
		}
		// Método de teste para verificar a listagem de todos os usuários.
		@Test
		@DisplayName("Deve listar todos os usuários") // Nome legível para o teste
		public void deveListarTodosUsuarios() {
			
			//Given (Dado): Prepara os dados.
			// Cadastra dois usuários adicionais para serem listados.
			usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Ana Clara",
					"ana@email.com", "senha123"));
			usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Carlos Souza",
					"carlos@email.com", "senha123"));

			//When (Quando): Executa a ação.
			// Envia uma requisição GET para a URL de listagem de todos os usuários.
			// Usa autenticação básica com as credenciais do usuário root.
			// Espera uma resposta que é um array de objetos Usuario.
			ResponseEntity<Usuario[]> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
					.exchange(BASE_URL_USUARIOS + "/all", HttpMethod.GET, null, Usuario[].class);

			//Then (Então): Verifica os resultados.
			// AssertEquals: Verifica se o código de status da resposta é OK (200).
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
			// AssertNotNull: Verifica se o corpo da resposta (o array de usuários) não é nulo.
			// Poderia-se adicionar mais asserções para verificar o tamanho do array ou o conteúdo específico.
			assertNotNull(resposta.getBody());
			
		}
		
		@Test
		@DisplayName("Deve retornar status NOT_FOUND ao buscar usuário por ID inexistente")
		public void deveRetornarNotFoundAoBuscarPorIdInexistente() {
			
			// Given
			// Um ID que sabemos que não existe no banco de dados (ex: um ID muito grande ou negativo)
			Long idInexistente = 999999999L; 

			// When
			// Envia uma requisição GET para um ID que não existe
			ResponseEntity<Usuario> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
					.exchange(BASE_URL_USUARIOS + "/" + idInexistente, HttpMethod.GET, null, Usuario.class);

			// Then
			// Verifica se o status da resposta é NOT_FOUND (404)
			assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
		}
		
		@Test
		@DisplayName("Deve retornar status NOT_FOUND ao buscar usuário por ID inexistente")
		public void deveRetornar() {
			
			// Given
			// Um ID que sabemos que não existe no banco de dados (ex: um ID muito grande ou negativo)
			Long idInexistente = 999999999L; 

			// When
			// Envia uma requisição GET para um ID que não existe
			ResponseEntity<Usuario> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
					.exchange(BASE_URL_USUARIOS + "/" + idInexistente, HttpMethod.GET, null, Usuario.class);

			// Then
			// Verifica se o status da resposta é NOT_FOUND (404)
			assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
		}
		
		@Test
		@DisplayName("Deve buscar um usuário pelo ID com sucesso")
		public void deveBuscarUsuarioPorId() {
			
			// Given
			// Cria um usuário para ser buscado
			Usuario usuarioParaBuscar = TestBuilder.criarUsuario(null, "Roberto Carlos",
					"roberto_carlos@email.com.br", "rc12345");
			// Cadastra o usuário no banco de dados para que ele possa ser encontrado
			Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuarioParaBuscar);
			
			// Verifica se o usuário foi realmente cadastrado e tem um ID
			assertTrue(usuarioCadastrado.isPresent(), "Usuário deveria ter sido cadastrado.");
			Long idUsuarioCadastrado = usuarioCadastrado.get().getId();

			// When
			// Envia uma requisição GET para o endpoint de busca por ID
			// É necessário passar o ID do usuário na URL
			ResponseEntity<Usuario> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA) // Geralmente, buscar por ID pode exigir autenticação
					.exchange(BASE_URL_USUARIOS + "/" + idUsuarioCadastrado, HttpMethod.GET, null, Usuario.class);

			// Then
			// Verifica se o status da resposta é OK (200)
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
			// Verifica se o corpo da resposta não é nulo
			assertNotNull(resposta.getBody());
			// Verifica se o ID do usuário retornado é o mesmo que foi buscado
			assertEquals(idUsuarioCadastrado, resposta.getBody().getId());
			// Verifica se o nome do usuário retornado é o esperado
			assertEquals("Roberto Carlos", resposta.getBody().getNome());
			// Verifica se o email do usuário retornado é o esperado
			assertEquals("roberto_carlos@email.com.br", resposta.getBody().getUsuario());
		}

		
		@Test
		@DisplayName("Não deve autenticar usuário com credenciais inválidas")
		public void naoDeveAutenticarComCredenciaisInvalidas() {
			
			// Given
			// Não precisamos cadastrar um usuário válido, pois estamos testando credenciais inválidas
			UsuarioLogin usuarioLoginInvalido = new UsuarioLogin();
			usuarioLoginInvalido.setUsuario("usuario_inexistente@email.com");
			usuarioLoginInvalido.setSenha("senhaErrada");

			// When
			HttpEntity<UsuarioLogin> requisicaoLogin = new HttpEntity<>(usuarioLoginInvalido);
			ResponseEntity<UsuarioLogin> respostaLogin = testRestTemplate.exchange(
					BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicaoLogin, UsuarioLogin.class);

			// Then
			// Espera-se que o status da resposta seja UNAUTHORIZED (401)
			// Ou BAD_REQUEST (400) dependendo da sua implementação para credenciais inválidas
			assertEquals(HttpStatus.UNAUTHORIZED, respostaLogin.getStatusCode());
			// O corpo da resposta pode ser nulo ou conter uma mensagem de erro
			// Se o corpo não é nulo, você pode verificar a mensagem de erro se houver.
		}
		
		@Test
		@DisplayName("Deve autenticar usuário com credenciais inválidas")
		public void autenticarComCredenciaisValidas() {
			
			// Given
			Usuario usuarioCadastrar = TestBuilder.criarUsuario(null, "Ana Login", "ana_login@email.com", "senhaSegura123");
			usuarioService.cadastrarUsuario(usuarioCadastrar);

			// AQUI É A MELHORIA: Use o método do TestBuilder para criar o objeto UsuarioLogin
			UsuarioLogin credenciaisLogin = TestBuilder.criarUsuarioLogin("ana_login@email.com", "senhaSegura123");

			// When
			HttpEntity<UsuarioLogin> requisicaoLogin = new HttpEntity<>(credenciaisLogin); // Use o objeto criado
			ResponseEntity<UsuarioLogin> respostaLogin = testRestTemplate.exchange(
					BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicaoLogin, UsuarioLogin.class);

			// Then
			assertEquals(HttpStatus.OK, respostaLogin.getStatusCode());
			assertNotNull(respostaLogin.getBody());
			assertNotNull(respostaLogin.getBody().getToken());
			assertTrue(respostaLogin.getBody().getToken().length() > 0);
			assertEquals("Ana Login", respostaLogin.getBody().getNome());
		}
			
			
		
	
}
