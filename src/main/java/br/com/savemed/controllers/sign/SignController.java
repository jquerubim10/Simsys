package br.com.savemed.controllers.sign;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.auth.UtilLogin;
import br.com.savemed.model.file.SignedDocs;
import br.com.savemed.services.TokenService;
import br.com.savemed.services.sign.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sign")
@Tag(name = "sign", description = "Endpoints for Sign document")
public class SignController {

    private final SignService service;

    @Autowired
    public SignController(SignService service) {
        this.service = service;
    }
    @Autowired
    private TokenService tokenService;

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login", description = "Login com logon e senha", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserSave.class))
                    ),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserSave> login(@RequestBody UtilLogin logon) {

        UserSave user = service.findByUserPass(logon.getLogon(), logon.getPassword());

        var token = tokenService.generateToken(user);

        // 2. SETA O TOKEN NO CAMPO TRANSIENT DO OBJETO
        user.setToken(token);

        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "login/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login com get", description = "Login com logon e senha", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserSave.class))
                    ),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserSave> loginWithGet(@RequestParam("logon") String logon, @RequestParam("pass") String pass) {

        UserSave user = service.findByUserPass(logon, pass);
        return ResponseEntity.ok(user);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign Login", description = "Sign with login", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Sucesso", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserSave.class))
                    ),
                    @ApiResponse(description = "Não Autorizado", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Não Encontrado", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Erro Interno", responseCode = "500", content = @Content)
            })
    public ResponseEntity<UserSave> SignLogin(@RequestBody UtilLogin logon,
                                              @RequestParam("pfxPassword") String pfxPassword,
                                              @RequestParam("file") MultipartFile file) throws IOException {

        UserSave user = service.findByUserPass(logon.getLogon(), logon.getPassword());
        //service.signUser(user, pfxPsword, file);

        return ResponseEntity.ok(user);
    }

    @SneakyThrows
    @PostMapping()
    @Operation(summary = "Signature", description = "End point to signature a document", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public void sign(@RequestParam("file") MultipartFile file,
                     @RequestParam("pfx") MultipartFile pfx,
                     @RequestParam("pfxPassword") String pfxPassword) {
        service.signPdf(file, pfx, pfxPassword);
    }

    @SneakyThrows
    @PostMapping(value = "new")
    @Operation(summary = "Signature", description = "End point to signature a document", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public void signPdfUser(@RequestParam("file") MultipartFile file,
                            @RequestParam("userData") String userData,
                            @RequestParam("pin") String pin) {
        service.signPdfUser(file, pin, userData);
    }

    @SneakyThrows
    @PostMapping(name = "api/signing/document", value = "signing/document")
    @Operation(summary = "Signature", description = "End point to signature a document", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<SignedDocs> signedPdfSave(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("pin") String pin, @RequestParam("tableName") String tableName,
                                                    @RequestParam("tableId") Long tableId, @RequestParam("userId") Long userId,
                                                    @RequestParam("whereClauseColumn") String whereClauseColumn) {
        SignedDocs response = service.signingSaveTo(file, pin, tableName, tableId, userId, whereClauseColumn);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getDocumentHash() + "\"");
        responseHeaders.set("id-return", response.getId().toString());


        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }


    @SneakyThrows
    @GetMapping(name = "api/signed", value = "signed/{id}")
    @Operation(summary = "Return pdf signed", description = "End point get a document signed", tags = {"sign"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<byte[]> getPdfSigned(@PathVariable Long id) {
        SignedDocs response = service.getSignedFile(id);
        //service.signPdfUser(file, pin, userData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getDocumentHash() + "\"")
                .body(response.getSignedDocument());
    }
}
