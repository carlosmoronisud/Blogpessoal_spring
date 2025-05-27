package com.generation.blogpessoal.security; // Pacote para classes de segurança.

import java.util.Optional; // Importa Optional para lidar com valores que podem ou não estar presentes.

import org.springframework.beans.factory.annotation.Autowired; // Anotação para injeção de dependência.
import org.springframework.http.HttpStatus; // Enumeração com códigos de status HTTP, usada para lançar exceções.
import org.springframework.security.core.userdetails.UserDetails; // Interface UserDetails, que você já implementou em UserDetailsImpl.
import org.springframework.security.core.userdetails.UserDetailsService; // Interface central do Spring Security para carregar detalhes do usuário.
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exceção padrão do Spring Security para quando o usuário não é encontrado.
import org.springframework.stereotype.Service; // Anotação para indicar que esta classe é um componente de serviço do Spring.
import org.springframework.web.server.ResponseStatusException; // Exceção para retornar um status HTTP específico.

import com.generation.blogpessoal.model.Usuario; // Importa a classe de modelo Usuario.
import com.generation.blogpessoal.repository.UsuarioRepository; // Importa a interface do repositório de usuário.

@Service // Esta anotação marca a classe como um "Service" do Spring. Isso a torna um componente gerenciado que pode ser injetado em outros lugares (como o AuthenticationManager).
// Papel na aplicação atual: Esta classe é a responsável por carregar os dados de um usuário a partir do seu banco de dados
// e "traduzi-los" para o formato que o Spring Security entende (UserDetailsImpl).
// Reuso em outros projetos: Esta classe é ALTAMENTE REUTILIZÁVEL. Em qualquer projeto Spring Security que use um banco de dados
// para usuários, você precisará de uma implementação de UserDetailsService. A lógica de buscar o usuário
// e adaptá-lo para UserDetailsImpl será muito similar.
public class UserDetailsServiceImpl implements UserDetailsService { // 'implements UserDetailsService' é o contrato.
    // O Spring Security espera uma classe que implemente esta interface para saber como carregar informações do usuário.

	@Autowired // Injeção de Dependência: O Spring automaticamente injeta uma instância de UsuarioRepository.
	private UsuarioRepository usuarioRepository; // Objeto para acessar o banco de dados e buscar informações de usuário.

	@Override // Indica que este método está sobrescrevendo um método da interface UserDetailsService.
	// loadUserByUsername(): Este é o único método da interface UserDetailsService que você precisa implementar.
	// Ele é chamado pelo Spring Security (especialmente pelo AuthenticationManager)
	// toda vez que precisa carregar detalhes de um usuário com base no seu nome de usuário (geralmente o e-mail no seu caso).
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 'throws UsernameNotFoundException': O contrato da interface exige que esta exceção seja lançada
        // se o usuário não for encontrado.

		// Busca o usuário no banco de dados usando o 'username' (que é o e-mail/campo 'usuario' da sua entidade).
		// 'findByUsuario' é o método de consulta personalizado que você definiu no UsuarioRepository.
		// Retorna um Optional<Usuario> para lidar com a possibilidade do usuário não existir.
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(username);

		// Verifica se o Optional contém um usuário (se o usuário foi encontrado no banco de dados).
		if (usuario.isPresent())
			// Se o usuário for encontrado:
			// 1. Pega o objeto 'Usuario' do Optional (usuario.get()).
			// 2. Cria uma nova instância de UserDetailsImpl, passando o objeto 'Usuario' para o construtor.
			//    Lembre-se que UserDetailsImpl é o adaptador que converte seu Usuario em um UserDetails.
			// 3. Retorna este UserDetailsImpl. É este objeto que o Spring Security usará para
			//    comparar a senha fornecida na requisição com a senha criptografada do banco.
			return new UserDetailsImpl(usuario.get());
		else
			// Se o usuário NÃO for encontrado no banco de dados:
			// Lança uma exceção 'ResponseStatusException' com o status HTTP 401 UNAUTHORIZED (Não Autorizado).
			// Isso indica que as credenciais fornecidas (o username, neste caso) não correspondem a nenhum usuário válido.
			// Você também poderia lançar uma 'UsernameNotFoundException' diretamente, que é o padrão do Spring Security.
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erro ao Autenticar o Usuário");
			
	}
}