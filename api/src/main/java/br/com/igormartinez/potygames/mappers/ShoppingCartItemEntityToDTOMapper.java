package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.models.ShoppingCartItem;

@Service
public class ShoppingCartItemEntityToDTOMapper implements Function<ShoppingCartItem, ShoppingCartItemResponseDTO> {

    @Override
    public ShoppingCartItemResponseDTO apply(ShoppingCartItem t) {
        if (t == null || t.getItem() == null)
            return new ShoppingCartItemResponseDTO(null, null, null, 
                null, null, null);
        
        return new ShoppingCartItemResponseDTO (
            t.getItem().getId(), 
            (t.getItem().getProduct() == null ? null : t.getItem().getProduct().getName()), 
            t.getItem().getVersion(), 
            t.getItem().getCondition(), 
            t.getItem().getPrice(), 
            t.getQuantity()
        );
    }
    
}
