package br.com.savemed.controllers.ws;

import br.com.savemed.model.message.EntityReference;
import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.TipoMensagem;
import br.com.savemed.model.ws.*;
import br.com.savemed.services.UserSaveService;
import br.com.savemed.services.message.GenericMessageSender;
import br.com.savemed.services.message.MensagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/v1/ws")
@Tag(name = "Web Service", description = "Endpoints for Managing Web Service")
public class WebService {

    @Autowired
    UserSaveService service;
    private final MensagemService mensagemService;
    String base_url = "isaude.des.sp.gov.br";
    String id_url = "33";
    @Autowired
    private GenericMessageSender genericMessageSender;

    public WebService(MensagemService mensagemService) {
        this.mensagemService = mensagemService;
    }
    private String buildUrl(String path) {
        return "https://" + base_url + "/isaude-api/v1/" + id_url + path;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Autenticação de Sistema", description = "Autenticação de Sistema!", tags = {"ws"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public String authenticate(@RequestBody Authenticate authenticate) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        Map<String, String> req_payload = new HashMap<>();
        req_payload.put("login", authenticate.getLogin());
        req_payload.put("password",  authenticate.getPassword());

        HttpEntity<?> request = new HttpEntity<>(req_payload, headers);

        ResponseEntity<?> response = new RestTemplate()
                .postForEntity(buildUrl("/autenticar/app/"), request, String.class);

        return Objects.requireNonNull(response.getBody()).toString();
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Finds all", description = "Finds all", tags = {"ws"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = InformacaoPaciente.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public Page<InformacaoPaciente> listOfPatients() {

        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "id"));

        return service.listOfPatient(pageable);
    }

    @PostMapping(value = "patients",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post de paciente novo", description = "Post de paciente novo!", tags = {"ws"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public String postPatient(@RequestBody InformacaoPaciente paciente,
                     @RequestHeader(value = "token", required = false) String token) {
        var restClient = RestClient.create();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", "Bearer " + token);

        headers.setAll(map);

        ResponseEntity<String> result = restClient.post()
                .uri(buildUrl("/assistencia/paciente/"))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(paciente)
                .retrieve()
                .toEntity(String.class);

        System.out.println(result.getHeaders());
        System.out.println(result.getBody());
        return Objects.requireNonNull(result.getBody());
    }

    @PostMapping(value = "history/patients",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post de paciente novo", description = "Post de paciente novo!", tags = {"ws"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public String InfoHistoryPlace(@RequestBody InformacaoHistoricoLeitos leito,
                              @RequestHeader(value = "token", required = false) String token) {
        var restClient = RestClient.create();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", "Bearer " + token);

        headers.setAll(map);

        ResponseEntity<String> result = restClient.post()
                .uri(buildUrl("/assistencia/leitohist") )
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(leito)
                .retrieve()
                .toEntity(String.class);

        return Objects.requireNonNull(result.getBody());
    }

    @PostMapping(value = "history/out",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post de historico saida", description = "Post de historico saida", tags = {"ws"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public String InfoHistoryOut(@RequestBody InformacaoSaidaPaciente outPatients,
                                   @RequestHeader(value = "token", required = false) String token) {
        var restClient = RestClient.create();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", "Bearer " + token);

        headers.setAll(map);

        ResponseEntity<String> result = restClient.post()
                .uri(buildUrl("/assistencia/saida") )
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(outPatients)
                .retrieve()
                .toEntity(String.class);

        return Objects.requireNonNull(result.getBody());
    }

    @PostMapping("/send-generic")
    public ResponseEntity<Map<String, Object>> sendGeneric(
            @Valid @RequestBody SendMessageRequest request,
            @RequestParam CanalType canal,
            EntityReference entidadeRef,
            TipoMensagem tipoMensagem
    ) {
        return genericMessageSender.sendGenericMessage(request, canal,entidadeRef,tipoMensagem);
    }


}
