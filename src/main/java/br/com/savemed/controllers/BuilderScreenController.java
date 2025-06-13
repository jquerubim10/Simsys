package br.com.savemed.controllers;

import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.builder.BuilderScreen;
import br.com.savemed.services.BuilderScreenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/builder-screen") // Endpoint mais genérico
@Tag(name = "Builder Screen", description = "Endpoints para Gerenciar Telas Dinâmicas")
public class BuilderScreenController {

    @Autowired
    private BuilderScreenService service;

    @GetMapping
    public ResponseEntity<Page<BuilderScreen>> findAll(Pageable pageable, @AuthenticationPrincipal UserSave user) {
        return ResponseEntity.ok(service.findAllForUser(pageable, user.getUsuario()));
    }

    @GetMapping(value = "/{id}")
    public BuilderScreen findById(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.findByIdForUser(id, user.getUsuario());
    }

    @GetMapping(value = "/previews")
    public List<BuilderScreen> buildingScreenWithPreview(@AuthenticationPrincipal UserSave user) {
        return service.buildingScreenWithPreviewForUser(user.getUsuario());
    }

    @GetMapping(value = "/{id}/complete")
    public BuilderScreen buildingScreenComplete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return service.buildingScreenCompleteForUser(id, user.getUsuario());
    }

    @GetMapping(value = "/component")
    public ResponseEntity<Page<BuilderScreen>> findAllComponent(Pageable pageable, @AuthenticationPrincipal UserSave user) {
        return ResponseEntity.ok(service.findAllComponentForUser(pageable, user.getUsuario()));
    }

    @GetMapping(value = "/component/group/{id}")
    public ResponseEntity<Page<BuilderScreen>> findComponentByGroup(Pageable pageable, @PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        return ResponseEntity.ok(service.findComponentByGroupForUser(pageable, id, user.getUsuario()));
    }

    @PostMapping
    public BuilderScreen create(@RequestBody BuilderScreen item, @AuthenticationPrincipal UserSave user) {
        return service.create(item, user.getUsuario());
    }

    @PutMapping
    public BuilderScreen update(@RequestBody BuilderScreen item, @AuthenticationPrincipal UserSave user) {
        return service.update(item, user.getUsuario());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id, @AuthenticationPrincipal UserSave user) {
        service.delete(id, user.getUsuario());
        return ResponseEntity.noContent().build();
    }
}