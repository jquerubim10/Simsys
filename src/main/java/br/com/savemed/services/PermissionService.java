package br.com.savemed.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Serviço centralizado para verificar permissões de usuário (autorização).
 * Este serviço implementa uma lógica de precedência de 3 níveis:
 * 1. Negação Específica (tem a maior prioridade).
 * 2. Permissão Adicional Específica.
 * 3. Permissão de Perfil (tem a menor prioridade).
 * Se nenhuma regra for encontrada, o acesso é negado por padrão.
 */
@Service
public class PermissionService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * DTO interno e privado para carregar as flags de permissão do banco de dados.
     * Simplifica o manuseio dos resultados das queries.
     */
    private static class PermissionFlags {
        boolean canView, canCreate, canEdit, canDelete, canPrint;
    }

    // --- MÉTODOS PÚBLICOS DE VERIFICAÇÃO ---
    // Estes são os métodos que outros serviços irão chamar.

    public boolean canView(Integer userId, Long resourceId, String resourceType) {
        return hasPermission(userId, resourceId, resourceType, p -> p.canView);
    }

    public boolean canCreate(Integer userId, Long resourceId, String resourceType) {
        return hasPermission(userId, resourceId, resourceType, p -> p.canCreate);
    }

    public boolean canEdit(Integer userId, Long resourceId, String resourceType) {
        return hasPermission(userId, resourceId, resourceType, p -> p.canEdit);
    }

    public boolean canDelete(Integer userId, Long resourceId, String resourceType) {
        return hasPermission(userId, resourceId, resourceType, p -> p.canDelete);
    }

    public boolean canPrint(Integer userId, Long resourceId, String resourceType) {
        return hasPermission(userId, resourceId, resourceType, p -> p.canPrint);
    }

    /**
     * Motor de permissão centralizado que segue a lógica de precedência.
     * @param userId ID do usuário legado (int).
     * @param resourceId ID do recurso (ex: ID do NavigationItem).
     * @param resourceType Tipo do recurso (ex: 'NAVIGATION_ITEM').
     * @param actionCheck Predicado que define qual ação verificar (ex: p -> p.canEdit).
     * @return true se o acesso for permitido, false caso contrário.
     */
    private boolean hasPermission(Integer userId, Long resourceId, String resourceType, Predicate<PermissionFlags> actionCheck) {
        if (userId == null) {
            return false;
        }

        // 1. VERIFICAR NEGAÇÃO ESPECÍFICA (MAIOR PRIORIDADE)
        PermissionFlags restriction = findRestrictionForUser(userId, resourceId, resourceType);
        if (actionCheck.test(restriction)) { // Se a flag de negação para a ação for 'true'
            return false; // Acesso explicitamente NEGADO.
        }

        // 2. VERIFICAR PERMISSÃO ADICIONAL (SEGUNDA PRIORIDADE)
        PermissionFlags additionalGrant = findAdditionalPermissionForUser(userId, resourceId, resourceType);
        if (additionalGrant != null) {
            return actionCheck.test(additionalGrant); // Usa a permissão específica do usuário.
        }

        // 3. VERIFICAR PERMISSÃO DE PERFIL (ÚLTIMA OPÇÃO)
        PermissionFlags profileGrant = findPermissionFromProfiles(userId, resourceId, resourceType);
        if (profileGrant != null) {
            return actionCheck.test(profileGrant); // Usa a permissão herdada do(s) perfil(is).
        }

        // 4. PADRÃO: NEGAR
        return false;
    }

    // --- MÉTODOS PRIVADOS PARA ACESSO AO BANCO ---

    /**
     * Busca por negações explícitas na tabela RESTRICAO_ESPECIFICA_USUARIO.
     */
    private PermissionFlags findRestrictionForUser(Integer userId, Long resourceId, String resourceType) {
        List<Object[]> result = entityManager.createNativeQuery(
                        "SELECT NEGAR_VISUALIZAR, NEGAR_CRIAR, NEGAR_EDITAR, NEGAR_EXCLUIR, NEGAR_IMPRIMIR " +
                                "FROM RESTRICAO_ESPECIFICA_USUARIO WHERE ID_USUARIO = ?1 AND ID_RECURSO = ?2 AND TIPO_RECURSO = ?3")
                .setParameter(1, userId).setParameter(2, resourceId).setParameter(3, resourceType)
                .getResultList();

        PermissionFlags flags = new PermissionFlags(); // Todas as flags são 'false' por padrão
        if (!result.isEmpty()) {
            Object[] r = result.get(0);
            flags.canView   = (boolean) r[0]; // Mapeando NEGAR_VISUALIZAR para a flag canView
            flags.canCreate = (boolean) r[1];
            flags.canEdit   = (boolean) r[2];
            flags.canDelete = (boolean) r[3];
            flags.canPrint  = (boolean) r[4];
        }
        return flags;
    }

    /**
     * Busca por concessões explícitas na tabela PERMISSAO_ADICIONAL_USUARIO.
     */
    private PermissionFlags findAdditionalPermissionForUser(Integer userId, Long resourceId, String resourceType) {
        List<Object[]> result = entityManager.createNativeQuery(
                        "SELECT PODE_VISUALIZAR, PODE_CRIAR, PODE_EDITAR, PODE_EXCLUIR, PODE_IMPRIMIR " +
                                "FROM PERMISSAO_ADICIONAL_USUARIO WHERE ID_USUARIO = ?1 AND ID_RECURSO = ?2 AND TIPO_RECURSO = ?3")
                .setParameter(1, userId).setParameter(2, resourceId).setParameter(3, resourceType)
                .getResultList();

        if (result.isEmpty()) return null; // Retorna null se não houver permissão adicional explícita
        Object[] r = result.get(0);
        PermissionFlags flags = new PermissionFlags();
        flags.canView   = (boolean) r[0];
        flags.canCreate = (boolean) r[1];
        flags.canEdit   = (boolean) r[2];
        flags.canDelete = (boolean) r[3];
        flags.canPrint  = (boolean) r[4];
        return flags;
    }

    /**
     * Busca por permissões herdadas dos perfis do usuário.
     * Usa MAX() para combinar permissões caso o usuário tenha múltiplos perfis.
     */
    private PermissionFlags findPermissionFromProfiles(Integer userId, Long resourceId, String resourceType) {
        List<Object[]> result = entityManager.createNativeQuery(
                        "SELECT MAX(CAST(P.PODE_VISUALIZAR AS INT)), MAX(CAST(P.PODE_CRIAR AS INT)), MAX(CAST(P.PODE_EDITAR AS INT)), MAX(CAST(P.PODE_EXCLUIR AS INT)), MAX(CAST(P.PODE_IMPRIMIR AS INT)) " +
                                "FROM PERMISSAO P JOIN USUARIO_PERFIL UP ON P.ID_PERFIL = UP.ID_PERFIL " +
                                "WHERE UP.ID_USUARIO = ?1 AND P.ID_RECURSO = ?2 AND P.TIPO_RECURSO = ?3")
                .setParameter(1, userId).setParameter(2, resourceId).setParameter(3, resourceType)
                .getResultList();

        if (result.isEmpty() || result.get(0)[0] == null) return null; // Retorna null se não houver permissões de perfil
        Object[] r = result.get(0);
        PermissionFlags flags = new PermissionFlags();
        flags.canView   = (Integer)r[0] > 0;
        flags.canCreate = (Integer)r[1] > 0;
        flags.canEdit   = (Integer)r[2] > 0;
        flags.canDelete = (Integer)r[3] > 0;
        flags.canPrint  = (Integer)r[4] > 0;
        return flags;
    }
}