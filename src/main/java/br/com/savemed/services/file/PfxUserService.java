package br.com.savemed.services.file;

import br.com.savemed.model.file.PfxUser;
import br.com.savemed.repositories.file.PfxUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class PfxUserService {

    private static final Logger LOGGER = Logger.getLogger(PfxUserService.class.getName());

    PfxUserRepository repository;

    @Autowired
    public PfxUserService(PfxUserRepository repository) {
        this.repository = repository;
    }

    public void storeFile(MultipartFile file, String user) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        PfxUser pfxUser = new PfxUser(user, fileName, file.getBytes());
        repository.save(pfxUser);
    }

    public PfxUser getFile(String id) {
        return repository.findByPfx(id);
    }

    public void removeFile(PfxUser file) {
        repository.delete(file);
    }
}
