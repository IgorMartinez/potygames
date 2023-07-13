package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.InventoryItemDTO;
import br.com.igormartinez.potygames.models.InventoryItem;

@Service
public class InventoryItemDTOMapper implements Function<InventoryItem, InventoryItemDTO> {

    @Override
    public InventoryItemDTO apply(InventoryItem item) {
        return new InventoryItemDTO(
            item.getId(), 
            (item.getProduct() == null) ? null : item.getProduct().getId(), 
            (item.getYugiohCard() == null) ? null : item.getYugiohCard().getId(), 
            item.getVersion(), 
            item.getCondition(), 
            item.getPrice(), 
            item.getQuantity());
    }
    
}
