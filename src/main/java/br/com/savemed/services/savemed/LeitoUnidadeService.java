package br.com.savemed.services.savemed;

import br.com.savemed.model.savemed.LeitoUnidade;
import br.com.savemed.repositories.savemed.LeitoUnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class LeitoUnidadeService {
    private final Logger LOGGER = Logger.getLogger(LeitoUnidadeService.class.getName());

    LeitoUnidadeRepository repository;

    @Autowired
    private LeitoUnidadeService(LeitoUnidadeRepository repository) {
        this.repository = repository;
    }

    public Page<LeitoUnidade> findAll(Pageable pageable) {
        LOGGER.info("find all setor");
        return repository.findAll(pageable);
    }
}
