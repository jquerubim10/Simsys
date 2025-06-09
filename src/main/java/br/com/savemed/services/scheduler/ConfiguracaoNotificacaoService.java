package br.com.savemed.services.scheduler;

import br.com.savemed.exceptions.RequiredObjectIsNullException;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.query.QueryBody;
import br.com.savemed.model.scheduler.ConfiguracaoNotificacao;
import br.com.savemed.repositories.scheduler.ConfiguracaoNotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class ConfiguracaoNotificacaoService {

    private final Logger logger = Logger.getLogger(ConfiguracaoNotificacaoService.class.getName());

    @Autowired
    private ConfiguracaoNotificacaoRepository repository;

    // Retorna todas as configurações (paginadas)
    public Page<ConfiguracaoNotificacao> findAll(Pageable pageable) {
        logger.info("Buscando todas as configurações de notificação");
        return repository.findAll(pageable);
    }

    // Busca configurações com filtros dinâmicos via SQL/JPQL
    public Page<ConfiguracaoNotificacao> findAllBySql(Pageable pageable, QueryBody queryBody) {
        logger.info("Buscando configurações por SQL personalizado");
        return repository.findAllBySql(pageable, queryBody); // Passa o QueryBody diretamente
    }

    // Busca uma configuração por ID
    public ConfiguracaoNotificacao findById(Long id) throws Exception {
        logger.info("Buscando configuração com ID: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada"));
    }

    // Retorna a configuração ativa mais recente
    public ConfiguracaoNotificacao getConfiguracaoAtiva() {
        logger.info("Buscando configuração ativa mais recente");
        return repository.findTopByOrderByConfigIDDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma configuração cadastrada"));
    }

    // Cria uma nova configuração
    public ConfiguracaoNotificacao create(ConfiguracaoNotificacao item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Criando nova configuração");
        return repository.save(item);
    }

    // Atualiza uma configuração existente
    public ConfiguracaoNotificacao update(ConfiguracaoNotificacao item) {
        if (Objects.isNull(item)) throw new RequiredObjectIsNullException();
        logger.info("Atualizando configuração ID: " + item.getConfigID());

        ConfiguracaoNotificacao existing = repository.findById(item.getConfigID())
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada"));

        return repository.save(item);
    }

    // Deleta uma configuração por ID
    public void delete(Long id) {
        logger.info("Deletando configuração ID: " + id);
        ConfiguracaoNotificacao config = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada"));
        repository.delete(config);
    }

    // Retorna todas as configurações (não paginado)
    public List<ConfiguracaoNotificacao> findAll() {
        logger.info("Buscando todas as configurações (lista completa)");
        return repository.findAll();
    }
    public ConfiguracaoNotificacao findByTipoEnvio(CanalType tipoEnvio) throws Exception {
        logger.info("Buscando configuração com  tipoEnvio: " + tipoEnvio);
        return repository.findByTipoEnvio(tipoEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada para o tipo informado"));
    }
}