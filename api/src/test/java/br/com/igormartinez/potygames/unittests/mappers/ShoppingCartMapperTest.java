package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.mappers.ShoppingCartItemEntityToDTOMapper;
import br.com.igormartinez.potygames.mocks.ShoppingCartMocker;
import br.com.igormartinez.potygames.models.ShoppingCartItem;

public class ShoppingCartMapperTest {
    
    private ShoppingCartItemEntityToDTOMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new ShoppingCartItemEntityToDTOMapper();
    }

    @Test
    void testEntityToDTO() {
        ShoppingCartItem entity = ShoppingCartMocker.mockEntity(1);

        ShoppingCartItemResponseDTO dto = mapper.apply(entity);
        assertEquals(1L, dto.idInventoryItem());
        assertEquals("Product name 1", dto.name());
        assertEquals("Version 1", dto.version());
        assertEquals("Condition 1", dto.condition());
        assertEquals(0, dto.price().compareTo(new BigDecimal("1.99")));
        assertEquals(1, dto.quantity());
    }

    @Test
    void testEntityToDTOWithEntityNull() {
        ShoppingCartItemResponseDTO dto = mapper.apply(null);
        assertNull(dto.idInventoryItem());
        assertNull(dto.name());
        assertNull(dto.version());
        assertNull(dto.condition());
        assertNull(dto.price());
        assertNull(dto.quantity());
    }

    @Test
    void testEntityToDTOWithItemNull() {
        ShoppingCartItem entity = ShoppingCartMocker.mockEntity(1);
        entity.setItem(null);

        ShoppingCartItemResponseDTO dto = mapper.apply(entity);
        assertNull(dto.idInventoryItem());
        assertNull(dto.name());
        assertNull(dto.version());
        assertNull(dto.condition());
        assertNull(dto.price());
        assertNull(dto.quantity());
    }

    @Test
    void testEntityToDTOWithProductNull() {
        ShoppingCartItem entity = ShoppingCartMocker.mockEntity(1);
        entity.getItem().setProduct(null);

        ShoppingCartItemResponseDTO dto = mapper.apply(entity);
        assertEquals(1L, dto.idInventoryItem());
        assertNull(dto.name());
        assertEquals("Version 1", dto.version());
        assertEquals("Condition 1", dto.condition());
        assertEquals(0, dto.price().compareTo(new BigDecimal("1.99")));
        assertEquals(1, dto.quantity());
    }
}
