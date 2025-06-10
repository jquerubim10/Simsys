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

    /**
     * O Spring Security chama este método quando o AuthenticationManager precisa
     * validar um usuário. Nós simplesmente delegamos a busca ao nosso repositório.
     */
    @Override
    public UserDetails loadUserByUsername(String logon) throws UsernameNotFoundException {
        var user = userSaveRepository.findByLogon(logon);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário com logon '" + logon + "' não encontrado.");
        }
        return user;
    }
}