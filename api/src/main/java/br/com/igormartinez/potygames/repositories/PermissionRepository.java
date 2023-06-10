package br.com.igormartinez.potygames.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;

@Repository
public interface PermissionRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT p FROM Permission p WHERE p.description = :description")
    Permission findByDescription(@Param("description") String description);
}
