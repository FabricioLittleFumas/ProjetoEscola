package br.com.escolaDuartina.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.escolaDuartina.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findUserByEmail(String email);

	List<User> findAllByOrderByNameAsc();

	List<User> findByApprovedFalseOrderByNameAsc();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE User u SET u.approved = :approved WHERE u.id = :id")
	int atualizarStatusAprovacao(@Param("id") Integer id, @Param("approved") boolean approved);
}
