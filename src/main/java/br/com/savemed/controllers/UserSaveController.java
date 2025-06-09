package br.com.savemed.controllers;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.auth.UtilLogin;
import br.com.savemed.services.UserSaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/user-save")
@Tag(name = "User Save", description = "Endpoints para Gerenciar Usuários Salvos")
public class UserSaveController {

    @Autowired
    private UserSaveService service;

    public UserSaveController(UserSaveService service) {
        this.service = service;
    }

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login", description = "Login com logon e senha", tags = {"User Save"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserSave.class))
                    ),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserSave> login(@RequestBody UtilLogin logon) {

        UserSave user = service.findByLogonSenha(logon.getLogon(), logon.getPassword());
        return ResponseEntity.ok(user);
    }
}

