package br.com.savemed.services;

import br.com.savemed.model.auth.UserSave;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Esta propriedade deve estar no seu application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserSave user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("savemed-api")
                    .withSubject(user.getLogon()) // O "dono" do token é o logon do usuário
                    .withClaim("userId", user.getUsuario()) // Adicionamos o ID do usuário como uma "claim"
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("savemed-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Retorna o logon se o token for válido
        } catch (JWTVerificationException exception){
            return ""; // Retorna vazio se o token for inválido
        }
    }

    private Instant genExpirationDate(){
        // Token expira em 2 horas
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}