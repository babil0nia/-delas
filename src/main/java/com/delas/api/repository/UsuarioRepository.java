package com.delas.api.repository;
import com.delas.api.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    Optional<UsuarioModel> findByEmail(String email);

    Optional<UsuarioModel> findByResetToken(String resetToken);

}
