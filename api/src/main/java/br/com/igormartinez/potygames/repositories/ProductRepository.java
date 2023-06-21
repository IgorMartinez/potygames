package br.com.igormartinez.potygames.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.type.id = :idProductType")
    int countProductsByIdProductType(long idProductType);
}
