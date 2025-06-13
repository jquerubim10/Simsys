package br.com.savemed.config;

import br.com.savemed.model.NavigationItem;
import br.com.savemed.model.auth.permission.PerfilUsuario;
import br.com.savemed.model.auth.permission.Permissao;
import br.com.savemed.model.enums.Types;
import br.com.savemed.repositories.NavigationItemRepository;
import br.com.savemed.repositories.auth.permission.PerfilUsuarioRepository;
import br.com.savemed.repositories.auth.permission.PermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Anotação para que o Spring gerencie esta classe
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PerfilUsuarioRepository perfilRepo;

    @Autowired
    private NavigationItemRepository navigationRepo;

    @Autowired
    private PermissaoRepository permissaoRepo;

    /**
     * Este método será executado automaticamente quando a aplicação iniciar.
     */
    @Override
    public void run(String... args) throws Exception {
        // Verifica se os perfis já existem para não criar dados duplicados

    }

    private void grantFullPermission(PerfilUsuario perfil, Long resourceId) {
        Permissao permissao = new Permissao();
        permissao.setPerfil(perfil);
        permissao.setIdRecurso(resourceId);
        permissao.setTipoRecurso("NAVIGATION_ITEM");
        permissao.setPodeVisualizar(true);
        permissao.setPodeCriar(true);
        permissao.setPodeEditar(true);
        permissao.setPodeExcluir(true);
        permissao.setPodeImprimir(true);
        permissaoRepo.save(permissao);
    }
}