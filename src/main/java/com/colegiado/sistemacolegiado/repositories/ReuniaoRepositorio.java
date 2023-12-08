package com.colegiado.sistemacolegiado.repositories;

import com.colegiado.sistemacolegiado.models.Reuniao;

import java.util.List;

import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReuniaoRepositorio extends JpaRepository<Reuniao, Integer> {

    @Query("SELECT r FROM Reuniao r INNER JOIN r.colegiado c WHERE c.id = :colegiadoId")
    List<Reuniao> listareuniaocolegiado (@Param("colegiadoId") int colegiadoId);

    @Query("SELECT r FROM Reuniao r WHERE r.status = :status AND r.colegiado.id = :idcolegiado")
    List<Reuniao> filtrarStatus(@Param("status") StatusReuniao status, @Param("idcolegiado") int idcolegiado);
}
