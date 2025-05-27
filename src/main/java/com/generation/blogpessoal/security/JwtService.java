package com.generation.blogpessoal.security; // Pacote para classes de segurança.

import java.security.Key; // Interface para chaves criptográficas.
import java.util.Date; // Classe para representar datas e horas.
import java.util.HashMap; // Implementação de Map, usada para armazenar claims do JWT.
import java.util.Map; // Interface para mapeamento de chave-valor.
import java.util.function.Function; // Interface funcional para aplicar uma função a um argumento.

import org.springframework.security.core.userdetails.UserDetails; // Importa a interface UserDetails que representa os detalhes do usuário para o Spring Security.
import org.springframework.stereotype.Component; // Anotação do Spring para indicar que esta classe é um componente gerenciado pelo Spring.

import io.jsonwebtoken.Claims; // Interface do Jwts (io.jsonwebtoken) que representa as "claims" (reivindicações/cargas) de um JWT.
import io.jsonwebtoken.Jwts; // Classe principal da biblioteca jjwt para construir, parsear e assinar JWTs.
import io.jsonwebtoken.SignatureAlgorithm; // Enumeração que representa os algoritmos de assinatura suportados (ex: HS256).
import io.jsonwebtoken.io.Decoders; // Utilitário para decodificar strings (usado para a chave secreta).
import io.jsonwebtoken.security.Keys; // Utilitário para gerar chaves de segurança (usado para a chave secreta).

@Component // Esta anotação marca a classe como um componente Spring. Isso significa que o Spring a gerenciará e poderá injetá-la em outros lugares com @Autowired (como no UsuarioService).
// Papel na aplicação atual: Esta classe é o motor para todas as operações JWT (gerar, extrair informações, validar).
// Reuso em outros projetos: Esta classe é ALTAMENTE REUTILIZÁVEL. Você pode copiar e colar a maior parte dela para qualquer outro projeto Spring Boot que utilize JWTs para autenticação,
// precisando apenas ajustar a SECRET e talvez a duração do token.
public class JwtService {

	// SECRET: Esta é a CHAVE SECRETA usada para assinar e verificar a assinatura dos seus JWTs.
	// É CRÍTICO que esta chave seja FORTE (longa e aleatória) e MANTIDA EM SEGREDO absoluto.
	// Se a chave for comprometida, um atacante pode forjar tokens válidos.
	// O valor é uma string Base64 codificada da sua chave (512 bits ou 64 bytes).
	// Em projetos reais, este valor NUNCA deve estar hardcoded no código. Ele deve ser lido de variáveis de ambiente
	// ou de um serviço de gerenciamento de segredos (ex: Vault, AWS Secrets Manager).
	public static final String SECRET = "aaNk2gvKjlx85jkafwhaLwmayrScJFP98m6eZuogzkvFX0GGDN1c9GVqGRDc0uj2\r\n"
			+ "";

	// getSignKey(): Método privado auxiliar para obter a chave de assinatura em formato criptográfico.
	private Key getSignKey() {
		// Decodifica a SECRET (que está em Base64) para um array de bytes.
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		// Cria uma chave HMAC Sha para assinatura a partir dos bytes decodificados.
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// extractAllClaims(): Método privado para extrair todas as "claims" (cargas/dados) de um token JWT.
	private Claims extractAllClaims(String token) {
		// Jwts.parserBuilder(): Inicia o processo de construção de um parser de JWT.
		// .setSigningKey(getSignKey()): Define a chave que será usada para verificar a assinatura do token.
		// .build(): Constrói o parser.
		// .parseClaimsJws(token): Faz o parse do token, validando a assinatura e extraindo o JWS (JSON Web Signature).
		// .getBody(): Retorna o corpo do JWT, que contém todas as claims (informações) do token.
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	// extractClaim(): Método genérico para extrair uma claim específica de um token.
	// 'claimsResolver': Uma função que define qual claim extrair (ex: Claims::getSubject para o username, Claims::getExpiration para a data de expiração).
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token); // Extrai todas as claims do token.
		return claimsResolver.apply(claims); // Aplica a função de resolução para obter a claim específica.
	}

	// extractUsername(): Extrai o nome de usuário (subject) do token.
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject); // Usa extractClaim com a função Claims::getSubject.
	}

	// extractExpiration(): Extrai a data de expiração do token.
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration); // Usa extractClaim com a função Claims::getExpiration.
	}

	// isTokenExpired(): Verifica se o token expirou.
	private Boolean isTokenExpired(String token) {
		// Compara a data de expiração do token com a data/hora atual.
		return extractExpiration(token).before(new Date());
	}

	// validateToken(): Valida um token JWT.
	// Retorna true se o token for válido para o UserDetails fornecido, false caso contrário.
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token); // Extrai o nome de usuário do token.
		// Verifica se o nome de usuário do token é igual ao nome de usuário do UserDetails e se o token NÃO expirou.
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// createToken(): Método privado para construir e assinar um token JWT.
	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder() // Inicia a construção do JWT.
					.setClaims(claims) // Adiciona quaisquer claims personalizadas (vazio neste caso).
					.setSubject(userName) // Define o "subject" (assunto) do token, que geralmente é o nome de usuário.
					.setIssuedAt(new Date(System.currentTimeMillis())) // Define a data de emissão do token (agora).
					// Define a data de expiração do token: 1 hora a partir de agora (1000ms * 60s * 60min).
					// Reuso: A duração do token é um parâmetro importante. Pode ser configurável em projetos maiores.
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					// Assina o token usando a chave secreta e o algoritmo HS256.
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); // Compacta o JWT em sua forma final (string).
	}

	// generateToken(): Método público para gerar um novo token JWT.
	// É o método que será chamado por outras classes (como UsuarioService) para emitir tokens.
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>(); // Cria um mapa vazio para claims adicionais (se necessário).
		// Chama o método privado 'createToken' para construir e assinar o token.
		return createToken(claims, userName);
	}

}