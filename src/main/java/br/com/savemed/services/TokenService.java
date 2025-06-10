package br.com.savemed.services;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import br.com.savemed.model.auth.UserSave;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.itextpdf.text.pdf.security.SecurityConstants.Algorithm;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserSave user){
        try{
            Algorithm algorithm = com.auth0.jwt.algorithms.Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("savemed-api")
                    .withSubject(user.getLogon())
                    .withClaim("userId", user.getUsuario()) // Adicionando o ID do usuário ao token
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = com.auth0.jwt.algorithms.Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("savemed-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}