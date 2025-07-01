// src/main/java/com/generation/blogpessoal/controller/PostagemController.java
package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

// IMPORTS CORRETOS PARA PAGINAÇÃO DO SPRING DATA JPA
import org.springframework.data.domain.Page; // Importar do pacote correto
import org.springframework.data.domain.Pageable; // Importar do pacote correto
import org.springframework.data.domain.Sort; // Importar do pacote correto (para Sort.Direction)
import org.springframework.data.web.PageableDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private TemaRepository temaRepository;

	@GetMapping // Mantém a mesma URL, mas agora aceita parâmetros de paginação
	// NOVO: O tipo de retorno deve ser Page<Postagem>
    public ResponseEntity<Page<Postagem>> getAll(
        @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        // O findAll(Pageable) retorna diretamente um Page<Postagem>
        Page<Postagem> postagensPage = postagemRepository.findAll(pageable);

        // Se a página estiver vazia, retorna NO_CONTENT
        if (postagensPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content se não houver postagens na página
        }

        return ResponseEntity.ok(postagensPage); // Retorna a página de postagens
    }

	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {

		/**
		 * O Método executará a consulta: SELECT * FROM tb_postagens WHERE id = ?; A
		 * interrogação representa o valor inserido no parâmetro id do método getById
		 */
		return postagemRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada!"));
	}

	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) {

		/**
		 * O Método executará a consulta: SELECT * FROM tb_postagens WHERE titulo LIKE
		 * '%?%"; A interrogação representa o valor inserido no parâmetro titulo do
		 * método getAllByTitulo
		 */
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));

	}

	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {

		/** * Verifica se o tema existe antes de persistir a postagem no Banco de dados
		 * */
		if (temaRepository.existsById(postagem.getTema().getId())) {

			/**
			 * O Método executará a consulta: INSERT INTO tb_postagens VALUES (titulo,
			 * texto, data) VALUES (?, ?, ?); As interrogações representam os valores
			 * inseridos nos respectivos atributos do objeto postagem, parâmetro do método
			 * post.
			 */
			return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
		}
		
		/** * Caso o tema não exista, retorna um Bad Request informando que o tema não existe
		 * */
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe!", null);
	}

	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {

		// Verifica se o ID da postagem existe
		if (postagemRepository.existsById(postagem.getId())) {
			// Verifica se o tema da postagem existe
			if (temaRepository.existsById(postagem.getTema().getId())) {
				return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe!", null);
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada para atualização!", null);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // Esta anotação já define o status HTTP
	public void delete(@PathVariable Long id) {

		/**
		 * Busca a postagem pelo id e guarda o resultado no Optional postagem
		 */
		Optional<Postagem> postagem = postagemRepository.findById(id);

		/**
		 * Verifica se o Optional postagem está vazio. Se estiver vazio, retorna o HTTP
		 * Status 404 - NOT_FOUND
		 */
		if (postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada para exclusão!");

		/**
		 * Caso contrário, o Método executará a consulta: DELETE FROM tb_postagens WHERE
		 * id = ?; A interrogação representa parâmetro id do método delete.
		 */
		postagemRepository.deleteById(id);

	}
}