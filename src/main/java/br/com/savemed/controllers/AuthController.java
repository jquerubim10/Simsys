package br.com.savemed.controllers;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.auth.UtilLogin;
import br.com.savemed.model.dto.LoginResponseDTO;
import br.com.savemed.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UtilLogin data){
        // 1. Cria um token de autenticação com o logon e senha recebidos.
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getLogon(), data.getPassword());

        // 2. O Spring Security usa o JpaUserDetailsService e o PasswordEncoder para validar.
        // Se as credenciais estiverem erradas, ele lança uma exceção automaticamente.
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 3. Se a autenticação for bem-sucedida, pega o usuário principal (UserSave) e gera o token.
        var user = (UserSave) auth.getPrincipal();
        var token = tokenService.generateToken(user);

        // 4. Retorna o token em um objeto JSON.
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}