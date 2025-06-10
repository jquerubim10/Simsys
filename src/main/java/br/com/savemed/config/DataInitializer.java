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
        if (perfilRepo.count() == 0) {
            System.out.println("Nenhum perfil encontrado, inicializando dados...");

            // 1. Criar Perfis
            PerfilUsuario adminProfile = new PerfilUsuario();
            adminProfile.setNome("ADMIN");
            adminProfile.setDescricao("Administrador do Sistema com acesso total.");
            adminProfile.setAtualizacao("SYSTEM");
            perfilRepo.save(adminProfile);

            // 2. Criar Itens de Menu
            NavigationItem dashboard = new NavigationItem();
            dashboard.setIdName("dashboard");
            dashboard.setTitle("Dashboard");
            dashboard.setType(Types.basic); // Supondo que você tenha um Enum 'Types'
            dashboard.setIcon("heroicons_outline:home");
            dashboard.setLink("/dashboard");
            navigationRepo.save(dashboard);

            NavigationItem settings = new NavigationItem();
            settings.setIdName("settings");
            settings.setTitle("Configurações");
            settings.setType(Types.collapsable);
            settings.setIcon("heroicons_outline:cog");
            navigationRepo.save(settings);

            NavigationItem usersMenu = new NavigationItem();
            usersMenu.setIdName("settings.users");
            usersMenu.setTitle("Usuários");
            usersMenu.setType(Types.basic);
            usersMenu.setIcon("heroicons_outline:users");
            usersMenu.setLink("/settings/users");
            usersMenu.setParent(settings); // Define 'Configurações' como pai
            navigationRepo.save(usersMenu);

            // 3. Criar Permissões (Vincular Perfil ADMIN aos menus)
            grantFullPermission(adminProfile, dashboard.getId());
            grantFullPermission(adminProfile, settings.getId());
            grantFullPermission(adminProfile, usersMenu.getId());

            System.out.println("Dados iniciais criados com sucesso.");
        }
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