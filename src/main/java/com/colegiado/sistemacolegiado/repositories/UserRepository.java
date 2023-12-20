package com.colegiado.sistemacolegiado.repositories;

import com.colegiado.sistemacolegiado.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public List<User> findByEnabledTrue();
}
