package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.mappers.InventoryItemToInventoryItemDTOMapper;
import br.com.igormartinez.potygames.mocks.MockInventoryItem;
import br.com.igormartinez.potygames.mocks.MockProduct;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.models.InventoryItem;

public class InventoryItemMapperTest {

    private MockInventoryItem mocker;
    private InventoryItemToInventoryItemDTOMapper mapper;

    @BeforeEach
    public void setup() {
        mocker = new MockInventoryItem(new MockProduct(new MockProductType()));
        mapper = new InventoryItemToInventoryItemDTOMapper();
    }

    @Test
    void testInventoryProductItemToDTO() {
        InventoryItem item = mocker.mockEntity(1);

        InventoryItemDTO itemDTO = mapper.apply(item);
        assertEquals(1L, itemDTO.id());
        assertEquals(1L, itemDTO.product());
        assertEquals("Version 1", itemDTO.version());
        assertEquals("Condition 1", itemDTO.condition());
        assertEquals(new BigDecimal("1.99"), itemDTO.price());
        assertEquals(1, itemDTO.quantity());
    }

}
