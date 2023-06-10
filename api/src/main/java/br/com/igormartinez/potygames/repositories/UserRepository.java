package br.com.igormartinez.potygames.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email IS NOT NULL AND u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

}
