package com.parde4.climaapi.repository;

import com.parde4.climaapi.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByIdAndDeletedAtIsNull(Integer id);

    Optional<Usuario> findByCorreoAndDeletedAtIsNull(String correo);
}