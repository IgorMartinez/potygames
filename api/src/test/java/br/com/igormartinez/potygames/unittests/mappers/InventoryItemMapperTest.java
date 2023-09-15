package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.mappers.InventoryItemToInventoryItemDTOMapper;
import br.com.igormartinez.potygames.mocks.InventoryItemMocker;
import br.com.igormartinez.potygames.models.InventoryItem;

public class InventoryItemMapperTest {

    private InventoryItemToInventoryItemDTOMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new InventoryItemToInventoryItemDTOMapper();
    }

    @Test
    void testInventoryProductItemToDTO() {
        InventoryItem item = InventoryItemMocker.mockEntity(1);

        InventoryItemDTO itemDTO = mapper.apply(item);
        assertEquals(1L, itemDTO.id());
        assertEquals(1L, itemDTO.product());
        assertEquals("Version 1", itemDTO.version());
        assertEquals("Condition 1", itemDTO.condition());
        assertEquals(new BigDecimal("1.99"), itemDTO.price());
        assertEquals(1, itemDTO.quantity());
    }

}
