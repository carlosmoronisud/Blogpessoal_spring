package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Indica que esta classe é uma fonte de definições de beans para o contêiner Spring. O Spring a escaneia para encontrar métodos anotados com @Bean.
@EnableWebSecurity // Habilita e configura a segurança web do Spring Security. Isso integra o framework de segurança ao seu aplicativo.
//Papel na Aplicação Atual:
//Esta classe é o ponto central onde toda a lógica de segurança do Spring Security é configurada.
//Ela age como um "hub" que conecta seus componentes de segurança (JwtAuthFilter, UserDetailsService, PasswordEncoder, etc.)
//e define as regras de como as requisições HTTP são protegidas.
//Reuso em Outros Projetos:
//Esta classe é EXTREMAMENTE REUTILIZÁVEL e serve como um modelo padrão para a configuração de segurança
//em praticamente qualquer aplicação Spring Boot RESTful que utiliza JWTs.
//A maior parte da estrutura e dos beans pode ser copiada e colada.
public class BasicSecurityConfig {

    @Autowired // O Spring injeta automaticamente uma instância do seu filtro personalizado JwtAuthFilter.
    private JwtAuthFilter authFilter; // Este filtro será adicionado à cadeia de segurança para processar os JWTs em cada requisição.

    
    // @Bean: Declara que o objeto retornado por este método deve ser registrado como um Bean no contêiner Spring.
    // userDetailsService(): Define qual implementação de UserDetailsService o Spring Security deve usar.
    @Bean
    UserDetailsService userDetailsService() {
    	// Retorna uma nova instância do seu UserDetailsServiceImpl.
        // O Spring Security chamará o método loadUserByUsername() desta instância quando precisar carregar os detalhes de um usuário.
        // Reuso: Em outros projetos, você retornaria sua implementação de UserDetailsService para o contexto daquele projeto.
        return new UserDetailsServiceImpl();
    }
    
     // @Bean: Declara o PasswordEncoder como um Bean.
    // passwordEncoder(): Define o algoritmo de criptografia de senhas a ser usado.
    @Bean
    PasswordEncoder passwordEncoder() {
    	 // Retorna uma instância de BCryptPasswordEncoder.
        // BCrypt é o algoritmo Padrão-Ouro para criptografar senhas, oferecendo excelente segurança.
        // Ele será usado tanto para criptografar senhas ao cadastrar usuários quanto para verificar senhas durante o login.
        // Reuso: BCryptPasswordEncoder é a escolha recomendada e pode ser reutilizada em praticamente todos os projetos.
        return new BCryptPasswordEncoder();
    }
    
     // @Bean: Declara o AuthenticationProvider como um Bean.
    // authenticationProvider(): Configura como a autenticação de usuários será realizada.
    @Bean
    AuthenticationProvider authenticationProvider() {
    	 // Cria uma instância de DaoAuthenticationProvider. Este provedor de autenticação é projetado
        // para autenticar usuários com base em dados de usuário armazenados em um banco de dados ou similar.
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Define qual UserDetailsService o DaoAuthenticationProvider deve usar para carregar os detalhes do usuário.
        authenticationProvider.setUserDetailsService(userDetailsService());
         // Define qual PasswordEncoder o DaoAuthenticationProvider deve usar para verificar a senha fornecida pelo usuário
        // contra a senha criptografada armazenada.
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        // Reuso: Este provedor é padrão para autenticação baseada em username/password com UserDetailsService.
        return authenticationProvider;
    }

     // @Bean: Declara o AuthenticationManager como um Bean.
    // authenticationManager(): O gerenciador central que orquestra o processo de autenticação.
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
    	// Obtém a instância do AuthenticationManager configurada pelo Spring Security.
        // Este é o mesmo 'AuthenticationManager' que é injetado no UsuarioService para iniciar o processo de login.
        // Reuso: O padrão de obtenção do AuthenticationManager é o mesmo em diferentes projetos.
        return authenticationConfiguration.getAuthenticationManager();
    }

    // @Bean: Declara o SecurityFilterChain como um Bean.
    // filterChain(): Este é o método mais importante na configuração de segurança, pois ele define
    // a cadeia de filtros que as requisições HTTP passarão e as regras de autorização.
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	 // .sessionManagement(): Configura como o Spring Security gerencia as sessões HTTP.
    	http
    	
	        .sessionManagement(management -> management
	        		
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        		.csrf(csrf -> csrf.disable())
	        		.cors(withDefaults());

    	http
	        .authorizeHttpRequests((auth) -> auth
	                .requestMatchers("/usuarios/logar").permitAll()
	                .requestMatchers("/usuarios/cadastrar").permitAll()
	                .requestMatchers("/error/**").permitAll()
	                .requestMatchers(HttpMethod.OPTIONS).permitAll()
	                .anyRequest().authenticated())
	        .authenticationProvider(authenticationProvider())
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	        .httpBasic(withDefaults());

		return http.build();

    }

}