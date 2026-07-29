package br.com.noticiarioOficial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.noticiarioOficial.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional findUserByEmail(String email);
}