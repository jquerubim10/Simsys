package br.com.savemed.repositories.message;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.EntidadeType;
import br.com.savemed.model.message.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    Page<Mensagem> findByEntidadeTypeAndEntidadeId(
            EntidadeType entidadeType,
            Long entidadeId,
            Pageable pageable
    );
    @Query("SELECT DISTINCT m.numeroTelefone FROM Mensagem m WHERE m.numeroTelefone IS NOT NULL AND (:canal IS NULL OR m.tipoCanal = :canal)"
            + " UNION "
            + "SELECT DISTINCT m.email FROM Mensagem m WHERE m.email IS NOT NULL AND (:canal IS NULL OR m.tipoCanal = :canal)"
            + " UNION "
            + "SELECT DISTINCT m.sistemaExternoId FROM Mensagem m WHERE m.sistemaExternoId IS NOT NULL AND (:canal IS NULL OR m.tipoCanal = :canal)")
    List<String> findContatosAgrupados(@Param("canal") CanalType canal, Sort sort);

    @Query("SELECT m FROM Mensagem m WHERE "
            + "(m.numeroTelefone = :identificador OR m.email = :identificador OR m.sistemaExternoId = :identificador) "
            + "AND (:canal IS NULL OR m.tipoCanal = :canal) "
            + "ORDER BY m.dataHoraEnvio DESC")
    List<Mensagem> findByDestinatario(
            @Param("identificador") String identificador,
            @Param("canal") CanalType canal,
            Sort sort
    );
    @Query("SELECT m FROM Mensagem m WHERE m.entidadeRef.type = :type AND m.entidadeRef.id = :id")
    List<Mensagem> findByEntidadeRefTypeAndEntidadeRefId(
            @Param("type") EntidadeType type,
            @Param("id") Long id
    );
    Optional<Mensagem> findBySistemaExternoId(String sistemaExternoId);
    Page<Mensagem> findByMensagemPaiId(Long mensagemPaiId, Pageable pageable);
}