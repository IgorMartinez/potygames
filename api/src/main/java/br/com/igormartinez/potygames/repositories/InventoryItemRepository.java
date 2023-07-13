package br.com.igormartinez.potygames.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.potygames.models.InventoryItem;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.product.id = :idProduct")
    int countByIdProduct(long idProduct);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.yugiohCard.id = :idYugiohCard")
    int countByIdYugiohCard(long idYugiohCard);
}
