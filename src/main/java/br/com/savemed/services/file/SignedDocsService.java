package br.com.savemed.services.file;

import br.com.savemed.controllers.interfaces.ListResultTransformer;
import br.com.savemed.model.file.FileDB;
import br.com.savemed.model.file.SignedDocs;
import br.com.savemed.model.query.QueryReturn;
import br.com.savemed.repositories.sign.SignedDocsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class SignedDocsService {

    private static final Logger LOGGER = Logger.getLogger(SignedDocsService.class.getName());

    SignedDocsRepository repository;

    @Autowired
    public SignedDocsService(SignedDocsRepository repository) {
        this.repository = repository;
    }

    public SignedDocs persistSignedFile(Long signedUserId, String sourceTable, Long sourceTableId, String documentHash, MultipartFile signedDocument, String whereClauseColumn, String signColumnName) throws IOException {
        LOGGER.config("Persisting signed document");
        SignedDocs signedDocs = new SignedDocs(sourceTable, signedUserId, sourceTableId, documentHash, signedDocument.getBytes(), whereClauseColumn, signColumnName);
        return repository.save(signedDocs);
    }

    //:TODO
    public void persistRevoFile(Long signedUserId, String sourceTable, Long sourceTableId, String documentHash, MultipartFile signedDocument) throws IOException {
        SignedDocs signedDocs = new SignedDocs(sourceTable, signedUserId, sourceTableId, documentHash, signedDocument.getBytes());
        repository.save(signedDocs);
    }

    public SignedDocs getSignedDocument(Long id) {
        return repository.findById(id).orElseThrow();
    }
}
