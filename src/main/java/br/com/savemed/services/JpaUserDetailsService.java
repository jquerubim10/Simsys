package br.com.savemed.services;

import br.com.savemed.repositories.UserSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserSaveRepository userSaveRepository;

    @Override
    public UserDetails loadUserByUsername(String logon) throws UsernameNotFoundException {
        // Agora o repositório retorna a entidade UserDetails que implementamos em UserSave
        return userSaveRepository.findByLogon(logon);
    }
}