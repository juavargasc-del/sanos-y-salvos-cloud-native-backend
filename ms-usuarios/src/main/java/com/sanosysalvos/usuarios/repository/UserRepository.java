package com.sanosysalvos.usuarios.repository;

import com.sanosysalvos.usuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}