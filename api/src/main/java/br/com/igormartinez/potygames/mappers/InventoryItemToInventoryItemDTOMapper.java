package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.models.InventoryItem;

@Service
public class InventoryItemToInventoryItemDTOMapper implements Function<InventoryItem, InventoryItemDTO> {

    @Override
    public InventoryItemDTO apply(InventoryItem item) {
        return new InventoryItemDTO(
            item.getId(), 
            (item.getProduct() == null) ? null : item.getProduct().getId(), 
            item.getVersion(), 
            item.getCondition(), 
            item.getPrice(), 
            item.getQuantity());
    }
    
}
