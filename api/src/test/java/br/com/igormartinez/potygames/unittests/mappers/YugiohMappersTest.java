package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.mappers.YugiohCardDTOMapper;
import br.com.igormartinez.potygames.mocks.MockYugiohCard;
import br.com.igormartinez.potygames.models.YugiohCard;

public class YugiohMappersTest {

    private MockYugiohCard yugiohCardMocker;
    private YugiohCardDTOMapper yugiohCardDTOMapper;

    @BeforeEach
    public void setup() {
        yugiohCardMocker = new MockYugiohCard();
        yugiohCardDTOMapper = new YugiohCardDTOMapper();
    }

    @Test
    public void testYugiohCardDTOMapper() {
        YugiohCard card = yugiohCardMocker.mockEntity(1);

        YugiohCardDTO cardDTO = yugiohCardDTOMapper.apply(card);
        assertNotNull(cardDTO);
        assertEquals("Name 1", cardDTO.name());
        assertEquals(1L, cardDTO.category());
        assertEquals(1L, cardDTO.type());
        assertEquals("DIVINE", cardDTO.attribute());
        assertEquals(1, cardDTO.levelRankLink());
        assertEquals("Effect lore text 1", cardDTO.effectLoreText());
        assertEquals(1, cardDTO.pendulumScale());
        assertTrue(Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "WN").containsAll(cardDTO.linkArrows()));
        assertEquals(1, cardDTO.atk());
        assertEquals(1, cardDTO.def());
    }
}
