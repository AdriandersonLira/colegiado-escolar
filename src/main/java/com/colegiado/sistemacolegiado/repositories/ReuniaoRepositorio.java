package com.colegiado.sistemacolegiado.repositories;

import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.Reuniao;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReuniaoRepositorio extends JpaRepository<Reuniao, Integer> {

    List<Reuniao> findAllByStatusInAndColegiado(List<StatusReuniao> status, Colegiado colegiado) ;

    List<Reuniao> findByStatus(StatusReuniao statusReuniao);
}
