package com.generation.blogpessoal.repository; // Declara o pacote onde esta interface está localizada.
// Por convenção, interfaces de repositório (que interagem com o banco de dados) são colocadas em um pacote '.repository'.

import java.util.Optional; // Importa a classe Optional do Java.util.
// 'Optional' é usada como um tipo de retorno para métodos que podem ou não encontrar um resultado.
// Ajuda a evitar NullPointerExceptions e força o desenvolvedor a lidar com a ausência de um valor.

import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface principal do Spring Data JPA.
// Esta é a parte mais importante: JpaRepository fornece métodos CRUD (Create, Read, Update, Delete) predefinidos.

import com.generation.blogpessoal.model.Usuario; // Importa a classe de modelo 'Usuario'.
// Esta é a entidade JPA que esta interface de repositório irá gerenciar.

// Declara a interface 'UsuarioRepository'.
// Ela 'extends JpaRepository<Usuario, Long>'. Isso significa que 'UsuarioRepository' herda todos os métodos
// predefinidos pela JpaRepository e é especializada para operar com a entidade 'Usuario'.
// Os parâmetros genéricos de JpaRepository são:
// 1. 'Usuario': O tipo da entidade (classe modelo) que este repositório irá persistir e recuperar.
// 2. 'Long': O tipo da chave primária (ID) da entidade 'Usuario' (lembre-se que definimos 'private Long id;' na classe Usuario).
// O Spring Data JPA, ao iniciar a aplicação, criará automaticamente uma implementação em tempo de execução para esta interface.
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	// Este é um "Query Method" (Método de Consulta por Nome) do Spring Data JPA.
	// O Spring Data JPA é inteligente o suficiente para entender o nome deste método e gerar a consulta SQL apropriada para você.
	// Regras de nomeação:
	// - 'findBy': Indica que a operação é uma busca (SELECT).
	// - 'Usuario': Refere-se ao atributo 'usuario' (que usamos para o e-mail/username) dentro da sua classe 'Usuario'.
	//   O Spring Data JPA fará um "SELECT * FROM tb_usuarios WHERE usuario = ?"
	// - 'Optional<Usuario>': O tipo de retorno indica que o método pode encontrar um usuário (Optional.of(usuario))
	//   ou não encontrar nenhum (Optional.empty()), garantindo um tratamento seguro para o resultado da busca.
	// - 'String usuario': Este é o parâmetro do método, que corresponderá ao valor que será usado na cláusula WHERE.
	Optional<Usuario> findByUsuario(String usuario);
	
	/* Comentário de exemplo (não é código Java executável)
	 * SELECT * FROM tb_usuarios WHERE usuario = ? 
	 * Esta linha é um comentário informativo que mostra a query SQL equivalente que o Spring Data JPA
	 * irá gerar e executar no banco de dados quando você chamar o método 'findByUsuario(String usuario)'.
	 * O '?' (placeholder) será substituído pelo valor da String 'usuario' que você passar para o método.
	 */
}