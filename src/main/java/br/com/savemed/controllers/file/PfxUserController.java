package br.com.savemed.controllers.file;

import br.com.savemed.model.file.FileDB;
import br.com.savemed.model.file.PfxUser;
import br.com.savemed.services.file.FileDbService;
import br.com.savemed.services.file.PfxUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SuppressWarnings({"unchecked", "unsafe"})
@RequestMapping("/api/v1/pfx/files")
@Tag(name = "Pfx", description = "Endpoints for Managing Pfx")
public class PfxUserController {

    PfxUserService service;

    @Autowired
    public PfxUserController(PfxUserService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Upload", description = "Upload", tags = {"Pfx"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("username") String username) {
        try {
            service.storeFile(file, username);
            return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Upload", description = "Upload", tags = {"Pfx"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        PfxUser file = service.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getTitle() + "\"")
                .body(file.getData());
    }
}
