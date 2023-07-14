package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.InventoryItemDTO;
import br.com.igormartinez.potygames.mappers.InventoryItemDTOMapper;
import br.com.igormartinez.potygames.mocks.MockInventoryItem;
import br.com.igormartinez.potygames.mocks.MockProduct;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.mocks.MockYugiohCard;
import br.com.igormartinez.potygames.models.InventoryItem;

public class InventoryItemMapperTest {

    private MockInventoryItem mocker;
    private InventoryItemDTOMapper mapper;

    @BeforeEach
    public void setup() {
        mocker = new MockInventoryItem(new MockProduct(new MockProductType()), new MockYugiohCard());
        mapper = new InventoryItemDTOMapper();
    }

    @Test
    void testInventoryProductItemToDTO() {
        InventoryItem item = mocker.mockEntityWithProduct(1);

        InventoryItemDTO itemDTO = mapper.apply(item);
        assertEquals(1L, itemDTO.id());
        assertEquals(1L, itemDTO.product());
        assertNull(itemDTO.yugiohCard());
        assertEquals("Version 1", itemDTO.version());
        assertEquals("Condition 1", itemDTO.condition());
        assertEquals(new BigDecimal("1.99"), itemDTO.price());
        assertEquals(1, itemDTO.quantity());
    }

    @Test
    void testInventoryYugiohCardItemToDTO() {
        InventoryItem item = mocker.mockEntityWithYugiohCard(1);

        InventoryItemDTO itemDTO = mapper.apply(item);
        assertEquals(1L, itemDTO.id());
        assertNull(itemDTO.product());
        assertEquals(1L, itemDTO.yugiohCard());
        assertEquals("Version 1", itemDTO.version());
        assertEquals("Condition 1", itemDTO.condition());
        assertEquals(new BigDecimal("1.99"), itemDTO.price());
        assertEquals(1, itemDTO.quantity());
    }

}
