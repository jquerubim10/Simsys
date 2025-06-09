package br.com.savemed.services.savemed;

import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.model.savemed.MedicoSavemed;
import br.com.savemed.repositories.savemed.CentroCustoSavemedRepository;
import br.com.savemed.repositories.savemed.MedicoSavemedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class MedicoSavemedService {

    private final Logger LOGGER = Logger.getLogger(MedicoSavemedService.class.getName());

    @Autowired
    MedicoSavemedRepository repository;

    public Page<MedicoSavemed> findAll(Pageable pageable) {
        LOGGER.info("find all medicos");
        return repository.findAll(pageable);
    }
}
