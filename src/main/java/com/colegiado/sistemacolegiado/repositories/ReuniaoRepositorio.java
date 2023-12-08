package com.colegiado.sistemacolegiado.repositories;

import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Reuniao;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReuniaoRepositorio extends JpaRepository<Reuniao, Integer> {

    @Query("SELECT r FROM Reuniao r INNER JOIN r.colegiado c WHERE c.id = :colegiadoId")
    List<Reuniao> listareuniaocolegiado (@Param("colegiadoId") int colegiadoId);

    @Query("SELECT r FROM Reuniao r WHERE r.status = :status AND r.colegiado.id = :idcolegiado")
    List<Reuniao> filtrarStatus(@Param("status") StatusReuniao status, @Param("idcolegiado") int idcolegiado);

    List<Reuniao> findAllByStatusInAndColegiado(List<StatusReuniao> status, Colegiado colegiado) ;

    List<Reuniao> findByStatus(StatusReuniao statusReuniao);
}
