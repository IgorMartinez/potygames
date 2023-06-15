package br.com.igormartinez.potygames.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long>  {
    
    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId")
    List<UserAddress> findAllByUserId(Long userId);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.id = :addressId AND ua.user.id = :userId")
    Optional<UserAddress> findByIdAndUserId(Long addressId, Long userId);
}
