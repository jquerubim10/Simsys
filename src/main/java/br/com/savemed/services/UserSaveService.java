package br.com.savemed.services;

import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.auth.UserSave;
import br.com.savemed.model.ws.InformacaoPaciente;
import br.com.savemed.repositories.UserSaveRepository;
import br.com.savemed.util.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

@Service
public class UserSaveService {
    private final Logger logger = Logger.getLogger(UserSaveService.class.getName());

    private final UserSaveRepository userSaveRepository;

    @Autowired
    public UserSaveService(UserSaveRepository userSaveRepository) {
        this.userSaveRepository = userSaveRepository;
    }

    public Page<UserSave> findAll(Pageable pageable) {
        logger.info("find all menus");

        return userSaveRepository.findAll(pageable);
    }

    public UserSave findById(Integer id) throws Exception {
        logger.info("Finding one menu!");

        return userSaveRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Records"));
    }

    public Page<InformacaoPaciente> listOfPatient(Pageable pageable) {
        logger.info("find all pacientes");

        return userSaveRepository.findAllPatients(pageable);
    }

    public UserSave salvaruserSave(UserSave usuario) {
        return userSaveRepository.save(usuario);
    }

    public UserSave findByLogonSenha(String logon, String senha) {
        logger.info("Buscando usuário por logon e senha!");

        UserSave optionalUser = userSaveRepository.findByLogon(logon);

        if (Objects.nonNull(optionalUser) && optionalUser.getSenha().equals(PasswordEncryptor.cryptPassword(senha, true))) {
            return optionalUser;
        } else {
            throw new ResourceNotFoundException("Usuario ou senha Invalido");
        }
    }
}
