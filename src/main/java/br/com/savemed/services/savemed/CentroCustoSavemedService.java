package br.com.savemed.services.savemed;

import br.com.savemed.model.savemed.CentroCustoSavemed;
import br.com.savemed.repositories.savemed.CentroCustoSavemedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class CentroCustoSavemedService {

    private final Logger LOGGER = Logger.getLogger(CentroCustoSavemedService.class.getName());

    @Autowired
    CentroCustoSavemedRepository repository;

    public Page<CentroCustoSavemed> findAll(Pageable pageable) {
        LOGGER.info("find all centro custo");
        return repository.findAll(pageable);
    }

    public Page<CentroCustoSavemed> findAllSetor(Pageable pageable) {
        LOGGER.info("find all centrocusto Save");
        return repository.findAll(pageable);
    }

}
