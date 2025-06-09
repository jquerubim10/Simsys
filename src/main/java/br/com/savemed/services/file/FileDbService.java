package br.com.savemed.services.file;

import br.com.savemed.model.file.FileDB;
import br.com.savemed.repositories.file.FileDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class FileDbService {

    private static final Logger LOGGER = Logger.getLogger(FileDbService.class.getName());

    FileDbRepository repository;

    @Autowired
    public FileDbService(FileDbRepository repository) {
        this.repository = repository;
    }

    public void storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());

        repository.save(FileDB);
    }

    public FileDB getFile(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public void removeFile(FileDB file) {
        repository.delete(file);
    }
}
