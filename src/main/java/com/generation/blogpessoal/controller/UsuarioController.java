package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.service.UsuarioService;

import jakarta.validation.Valid;

@RestController // Anotação para indicar que esta classe é um controlador REST
@RequestMapping("/usuarios") // Mapeia todas as requisições para /usuarios para este controller
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite requisições de qualquer origem (CORS)
public class UsuarioController {
	
	@Autowired // Injeção de Dependência: O Spring automaticamente encontra e fornece uma instância de UsuarioService.
	// Isso significa que você não precisa criar 'new UsuarioService()' manualmente; o Spring faz isso por você, gerenciando o ciclo de vida do objeto.
	private UsuarioService usuarioService; // Declara uma instância de UsuarioService, que conterá a lógica de negócio para operações de usuário.

	// Mapeia requisições GET para /usuarios/all
	@GetMapping("/all")
	public ResponseEntity<List<Usuario>> getAll() {
		// Retorna uma ResponseEntity com status 200 OK e o corpo contendo uma lista de todos os usuários.
		// A chamada 'usuarioService.getAll()' delega a lógica de buscar todos os usuários para o serviço.
		return ResponseEntity.ok(usuarioService.getAll());
	}

	// Mapeia requisições GET para /usuarios/{id}
	// '{id}' é uma variável de caminho (PathVariable) que será extraída da URL.
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getById(@PathVariable Long id) {
		// Chama o serviço para buscar um usuário pelo ID.
		// O método 'getById' no serviço retorna um Optional<Usuario> para lidar com o caso de o usuário não ser encontrado.
		return usuarioService.getById(id) // Se o usuário for encontrado (Optional.isPresent()):
				.map(resposta -> ResponseEntity.ok(resposta)) // Mapeia o Optional para uma ResponseEntity com status 200 OK e o usuário no corpo.
				// Se o usuário NÃO for encontrado (Optional.isEmpty()):
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Retorna uma ResponseEntity com status 404 NOT FOUND e sem corpo.
	}

	// Mapeia requisições POST para /usuarios/cadastrar
	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> post(@Valid @RequestBody Usuario usuario) {
		// '@Valid': Ativa as validações definidas na classe 'Usuario' (ex: @NotBlank, @Size, @Email).
		// Se as validações falharem, o Spring lançará uma exceção antes mesmo de o método ser executado.
		// '@RequestBody': Mapeia o corpo da requisição HTTP (que deve ser um JSON representando um Usuario) para o objeto 'usuario'.
		// Chama o serviço para tentar cadastrar o usuário.
		// O método 'cadastrarUsuario' no serviço provavelmente lida com criptografia de senha e verificação de e-mail duplicado.
		return usuarioService.cadastrarUsuario(usuario) // Se o cadastro for bem-sucedido (Optional.isPresent()):
				.map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(resposta)) // Retorna status 201 CREATED e o usuário cadastrado no corpo.
				// Se o cadastro falhar (ex: e-mail já existe) (Optional.isEmpty()):
				.orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()); // Retorna status 400 BAD REQUEST e sem corpo.
	}

	// Mapeia requisições PUT para /usuarios/atualizar
	@PutMapping("/atualizar")
	public ResponseEntity<Usuario> put(@Valid @RequestBody Usuario usuario) {
		// Similar ao POST, mas para atualização.
		// '@Valid' e '@RequestBody' com as mesmas funções.
		// Chama o serviço para tentar atualizar o usuário.
		// O serviço deve verificar se o ID do usuário existe antes de tentar atualizar.
		return usuarioService.atualizarUsuario(usuario) // Se a atualização for bem-sucedida (Optional.isPresent()):
				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta)) // Retorna status 200 OK e o usuário atualizado no corpo.
				// Se o usuário não for encontrado para atualização (Optional.isEmpty()):
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Retorna status 404 NOT FOUND e sem corpo.
	}

	// Mapeia requisições POST para /usuarios/logar
	@PostMapping("/logar")
	public ResponseEntity<UsuarioLogin> autenticar(@Valid @RequestBody Optional<UsuarioLogin> usuarioLogin) {
		// Este endpoint é para autenticação de usuário (login).
		// Recebe um 'UsuarioLogin' no corpo da requisição, que conterá o e-mail/usuário e a senha.
		// O 'Optional' ao redor de 'UsuarioLogin' aqui é um pouco incomum para @RequestBody, mas indica que o corpo da requisição pode ser opcional.
		// É mais comum usar apenas @RequestBody UsuarioLogin. O Optional pode estar ali para lidar com um corpo de requisição totalmente vazio ou nulo de forma mais explícita.
		// Chama o serviço para autenticar o usuário.
		// O serviço 'autenticarUsuario' fará a verificação da senha (comparando com a criptografada) e, se válido, gerará um JWT (Bearer Token).
		return usuarioService.autenticarUsuario(usuarioLogin) // Se a autenticação for bem-sucedida (Optional.isPresent()):
				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(resposta)) // Retorna status 200 OK e o 'UsuarioLogin' (que agora conterá o token JWT) no corpo.
				// Se a autenticação falhar (usuário/senha inválidos) (Optional.isEmpty()):
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // Retorna status 401 UNAUTHORIZED (Não Autorizado) e sem corpo.
	}

}
