package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.enums.YugiohCardAttribute;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.YugiohCardDTOMapper;
import br.com.igormartinez.potygames.mocks.MockYugiohCard;
import br.com.igormartinez.potygames.models.YugiohCard;
import br.com.igormartinez.potygames.models.YugiohCardCategory;
import br.com.igormartinez.potygames.models.YugiohCardType;
import br.com.igormartinez.potygames.repositories.YugiohCardCategoryRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.YugiohCardService;; 

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class YugiohCardServiceTest {
    private MockYugiohCard cardMocker;
    private YugiohCardService service;

    @Mock
    private YugiohCardRepository repository;
    
    @Mock
    private YugiohCardCategoryRepository categoryRepository;
    
    @Mock
    private YugiohCardTypeRepository typeRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        cardMocker = new MockYugiohCard();

        service = new YugiohCardService(
            repository, categoryRepository, typeRepository, 
            new YugiohCardDTOMapper(), securityContextManager);
    }

    @Test
    void testPrepareEntityWithCardDTONull() {
        Exception output = assertThrows(IllegalArgumentException.class, () -> {
            service.prepareEntity(null);
        });
        String expectedMessage = "The YugiohCardDTO argument must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithNameNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, null, null, null, null, 
            null, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card name must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithNameBlank() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, " ", null, null, null, 
            null, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card name must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithCategoryNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", null, null, null, 
            null, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card category must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithCategoryNotFound() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, null, null, 
            null, null, null, 
            null, null, null);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card category was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithMonsterAndTypeNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, null, null, 
            null, null, null, 
            null, null, null);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card type must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithMonsterAndTypeNotFound() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, null, 
            null, null, null, 
            null, null, null);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The card type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithMonsterAndAttributeNotFound() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "ERROR", 
            null, null, null, 
            null, null, null);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The attribute of monster card must be DARK, DIVINE, EARTH, FIRE, LIGHT, WATER or WIND.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithMonsterAndAtkNegative() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, null, 
            null, -1800, null);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The attack of monster card must be null, zero or positive.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithMonsterAndDefNegative() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, null, 
            null, 1800, -1200);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The defense of monster card must be null, zero or positive.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithPendulumMonsterAndPendulumScaleNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, null, 
            null, 1800, 1200);
        YugiohCardCategory category = cardMocker.mockCategoryPendulumMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The pendulum scale of a pendulum monster must be zero or positive.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithPendulumMonsterAndPendulumScaleNegative() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, -2, 
            null, 1800, 1200);
        YugiohCardCategory category = cardMocker.mockCategoryPendulumMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The pendulum scale of a pendulum monster must be zero or positive.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithLinkMonsterAndLinkArrowsNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, null, 
            null, 1800, 1200);
        YugiohCardCategory category = cardMocker.mockCategoryLinkMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The link arrows array of a link monster must be not null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithLinkMonsterAndLinkArrowsInvalid() {
        List<String> linkArrows = Arrays.asList("LOREM", "IPSUM");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Name card", 1L, 1L, "LIGHT", 
            null, null, null, 
            linkArrows, 1800, 1200);
        YugiohCardCategory category = cardMocker.mockCategoryLinkMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(cardDTO);
        });
        String expectedMessage = "The link arrows array of a link monster must contain valid coordinates.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithReturnMonster() {
        List<String> linkArrows = Arrays.asList("N", "S", "LOREM");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", 3, 
            linkArrows, 1800, 1200);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Monster");
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        YugiohCard card = service.prepareEntity(cardDTO);
        assertNotNull(card);
        assertNull(card.getId());
        assertNull(card.getIdYgoprodeck());
        assertEquals("Crystal Beast Sapphire Pegasus", card.getName());
        assertEquals(1L, card.getCategory().getId());
        assertEquals("Monster", card.getCategory().getCategory());
        assertEquals("Subcategory 1", card.getCategory().getSubCategory());
        assertFalse(card.getCategory().getMainDeck());
        assertEquals(1L, card.getType().getId());
        assertEquals("Monster type 1", card.getType().getDescription());
        assertEquals(YugiohCardAttribute.WIND, card.getAttribute());
        assertEquals(4, card.getLevelRankLink());
        assertEquals("Effect lore text", card.getEffectLoreText());
        assertNull(card.getPendulumScale());
        assertNull(card.getLinkArrows());
        assertEquals(1800, card.getAtk());
        assertEquals(1200, card.getDef());
    }

    @Test
    void testPrepareEntityWithReturnPendulumMonster() {
        List<String> linkArrows = Arrays.asList("N", "S", "LOREM");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Master", 1L, 1L, "DARK", 
            4, "Effect lore text", 5, 
            linkArrows, 1300, 1000);
        YugiohCardCategory category = cardMocker.mockCategoryPendulumMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        YugiohCard card = service.prepareEntity(cardDTO);
        assertNotNull(card);
        assertNull(card.getId());
        assertNull(card.getIdYgoprodeck());
        assertEquals("Crystal Master", card.getName());
        assertEquals(1L, card.getCategory().getId());
        assertEquals("Monster", card.getCategory().getCategory());
        assertEquals("Pendulum", card.getCategory().getSubCategory());
        assertTrue(card.getCategory().getMainDeck());
        assertEquals(1L, card.getType().getId());
        assertEquals("Monster type 1", card.getType().getDescription());
        assertEquals(YugiohCardAttribute.DARK, card.getAttribute());
        assertEquals(4, card.getLevelRankLink());
        assertEquals("Effect lore text", card.getEffectLoreText());
        assertEquals(5, card.getPendulumScale());
        assertNull(card.getLinkArrows());
        assertEquals(1300, card.getAtk());
        assertEquals(1000, card.getDef());
    }

    @Test
    void testPrepareEntityWithReturnLinkMonster() {
        List<String> linkArrows = Arrays.asList("SW", "S", "LOREM");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Onyx Gorilla", 1L, 1L, "EARTH", 
            2, "Effect lore text", null, 
            linkArrows, 2000, 1000);
        YugiohCardCategory category = cardMocker.mockCategoryLinkMonster(1);
        YugiohCardType type = cardMocker.mockType(1);

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));
        when(typeRepository.findById(cardDTO.type())).thenReturn(Optional.of(type));

        YugiohCard card = service.prepareEntity(cardDTO);
        assertNotNull(card);
        assertNull(card.getId());
        assertNull(card.getIdYgoprodeck());
        assertEquals("Crystal Beast Onyx Gorilla", card.getName());
        assertEquals(1L, card.getCategory().getId());
        assertEquals("Monster", card.getCategory().getCategory());
        assertEquals("Link", card.getCategory().getSubCategory());
        assertFalse(card.getCategory().getMainDeck());
        assertEquals(1L, card.getType().getId());
        assertEquals("Monster type 1", card.getType().getDescription());
        assertEquals(YugiohCardAttribute.EARTH, card.getAttribute());
        assertEquals(2, card.getLevelRankLink());
        assertEquals("Effect lore text", card.getEffectLoreText());
        assertNull(card.getPendulumScale());
        assertTrue(Arrays.asList("S", "SW").containsAll(card.getLinkArrows()));
        assertEquals(2000, card.getAtk());
        assertNull(card.getDef());
    }

    @Test
    void testPrepareEntityWithReturnNotMonster() {
        List<String> linkArrows = Arrays.asList("SW", "S", "LOREM");
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Golden Rule", 1L, 1L, "EARTH", 
            2, "Effect lore text", 3, 
            linkArrows, 2000, 1000);
        YugiohCardCategory category = cardMocker.mockCategory(1, "Spell");

        when(categoryRepository.findById(cardDTO.category())).thenReturn(Optional.of(category));

        YugiohCard card = service.prepareEntity(cardDTO);
        assertNotNull(card);
        assertNull(card.getId());
        assertNull(card.getIdYgoprodeck());
        assertEquals("Golden Rule", card.getName());
        assertEquals(1L, card.getCategory().getId());
        assertEquals("Spell", card.getCategory().getCategory());
        assertEquals("Subcategory 1", card.getCategory().getSubCategory());
        assertFalse(card.getCategory().getMainDeck());
        assertNull(card.getType());
        assertNull(card.getAttribute());
        assertNull(card.getLevelRankLink());
        assertEquals("Effect lore text", card.getEffectLoreText());
        assertNull(card.getPendulumScale());
        assertNull(card.getLinkArrows());
        assertNull(card.getAtk());
        assertNull(card.getDef());
    }

    @Test
    void testFindAllPage0() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<YugiohCard> page = cardMocker.mockEntityPage(94, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<YugiohCardDTO> outputPage = service.findAll(pageable);
        assertEquals(0, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<YugiohCardDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("Name 1", outputPosition0.name());
        assertEquals(1L, outputPosition0.category());
        assertEquals(1L, outputPosition0.type());
        assertEquals("DIVINE", outputPosition0.attribute());
        assertEquals(1, outputPosition0.levelRankLink());
        assertEquals("Effect lore text 1", outputPosition0.effectLoreText());
        assertEquals(1, outputPosition0.pendulumScale());
        assertEquals(8, outputPosition0.linkArrows().size());
        assertEquals(1, outputPosition0.atk());
        assertEquals(1, outputPosition0.def());

        YugiohCardDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals("Name 5", outputPosition4.name());
        assertEquals(5L, outputPosition4.category());
        assertEquals(5L, outputPosition4.type());
        assertEquals("WATER", outputPosition4.attribute());
        assertEquals(5, outputPosition4.levelRankLink());
        assertEquals("Effect lore text 5", outputPosition4.effectLoreText());
        assertEquals(5, outputPosition4.pendulumScale());
        assertEquals(8, outputPosition4.linkArrows().size());
        assertEquals(5, outputPosition4.atk());
        assertEquals(5, outputPosition4.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("Name 10", outputPosition9.name());
        assertEquals(10L, outputPosition9.category());
        assertEquals(10L, outputPosition9.type());
        assertEquals("FIRE", outputPosition9.attribute());
        assertEquals(10, outputPosition9.levelRankLink());
        assertEquals("Effect lore text 10", outputPosition9.effectLoreText());
        assertEquals(10, outputPosition9.pendulumScale());
        assertEquals(8, outputPosition9.linkArrows().size());
        assertEquals(10, outputPosition9.atk());
        assertEquals(10, outputPosition9.def());
    }

    @Test
    void testFindAllWithCardsPage4() {
        Pageable pageable = PageRequest.of(4, 10, Sort.by(Direction.ASC, "id"));
        Page<YugiohCard> page = cardMocker.mockEntityPage(94, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<YugiohCardDTO> outputPage = service.findAll(pageable);
        assertEquals(4, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<YugiohCardDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(10, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(41L, outputPosition0.id());
        assertEquals("Name 41", outputPosition0.name());
        assertEquals(41L, outputPosition0.category());
        assertEquals(41L, outputPosition0.type());
        assertEquals("WIND", outputPosition0.attribute());
        assertEquals(41, outputPosition0.levelRankLink());
        assertEquals("Effect lore text 41", outputPosition0.effectLoreText());
        assertEquals(41, outputPosition0.pendulumScale());
        assertEquals(8, outputPosition0.linkArrows().size());
        assertEquals(41, outputPosition0.atk());
        assertEquals(41, outputPosition0.def());

        YugiohCardDTO outputPosition4 = output.get(4);
        assertEquals(45L, outputPosition4.id());
        assertEquals("Name 45", outputPosition4.name());
        assertEquals(45L, outputPosition4.category());
        assertEquals(45L, outputPosition4.type());
        assertEquals("FIRE", outputPosition4.attribute());
        assertEquals(45, outputPosition4.levelRankLink());
        assertEquals("Effect lore text 45", outputPosition4.effectLoreText());
        assertEquals(45, outputPosition4.pendulumScale());
        assertEquals(8, outputPosition4.linkArrows().size());
        assertEquals(45, outputPosition4.atk());
        assertEquals(45, outputPosition4.def());

        YugiohCardDTO outputPosition9 = output.get(9);
        assertEquals(50L, outputPosition9.id());
        assertEquals("Name 50", outputPosition9.name());
        assertEquals(50L, outputPosition9.category());
        assertEquals(50L, outputPosition9.type());
        assertEquals("DIVINE", outputPosition9.attribute());
        assertEquals(50, outputPosition9.levelRankLink());
        assertEquals("Effect lore text 50", outputPosition9.effectLoreText());
        assertEquals(50, outputPosition9.pendulumScale());
        assertEquals(8, outputPosition9.linkArrows().size());
        assertEquals(50, outputPosition9.atk());
        assertEquals(50, outputPosition9.def());
    }

    @Test
    void testFindAllWithCardsPage9() {
        Pageable pageable = PageRequest.of(9, 10, Sort.by(Direction.ASC, "id"));
        Page<YugiohCard> page = cardMocker.mockEntityPage(94, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<YugiohCardDTO> outputPage = service.findAll(pageable);
        assertEquals(9, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<YugiohCardDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(4, output.size());

        YugiohCardDTO outputPosition0 = output.get(0);
        assertEquals(91L, outputPosition0.id());
        assertEquals("Name 91", outputPosition0.name());
        assertEquals(91L, outputPosition0.category());
        assertEquals(91L, outputPosition0.type());
        assertEquals("DARK", outputPosition0.attribute());
        assertEquals(91, outputPosition0.levelRankLink());
        assertEquals("Effect lore text 91", outputPosition0.effectLoreText());
        assertEquals(91, outputPosition0.pendulumScale());
        assertEquals(8, outputPosition0.linkArrows().size());
        assertEquals(91, outputPosition0.atk());
        assertEquals(91, outputPosition0.def());

        YugiohCardDTO outputPosition3 = output.get(3);
        assertEquals(94L, outputPosition3.id());
        assertEquals("Name 94", outputPosition3.name());
        assertEquals(94L, outputPosition3.category());
        assertEquals(94L, outputPosition3.type());
        assertEquals("FIRE", outputPosition3.attribute());
        assertEquals(94, outputPosition3.levelRankLink());
        assertEquals("Effect lore text 94", outputPosition3.effectLoreText());
        assertEquals(94, outputPosition3.pendulumScale());
        assertEquals(8, outputPosition3.linkArrows().size());
        assertEquals(94, outputPosition3.atk());
        assertEquals(94, outputPosition3.def());
    }

    @Test
    void testFindAllWithoutCards() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<YugiohCard> page = cardMocker.mockEntityPage(0, pageable);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<YugiohCardDTO> outputPage = service.findAll(pageable);assertEquals(0, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(0, outputPage.getTotalPages());
        assertEquals(0, outputPage.getTotalElements());

        List<YugiohCardDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testFindByIdWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(-10L);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithCardFound() {
        YugiohCard card = cardMocker.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(card));

        YugiohCardDTO cardDTO = service.findById(1L);
        assertEquals(1L, cardDTO.id());
        assertEquals("Name 1", cardDTO.name());
        assertEquals(1L, cardDTO.category());
        assertEquals(1L, cardDTO.type());
        assertEquals("DIVINE", cardDTO.attribute());
        assertEquals(1, cardDTO.levelRankLink());
        assertEquals("Effect lore text 1", cardDTO.effectLoreText());
        assertEquals(1, cardDTO.pendulumScale());
        assertEquals(8, cardDTO.linkArrows().size());
        assertEquals(1, cardDTO.atk());
        assertEquals(1, cardDTO.def());
    }

    @Test
    void testFindByIdWithCardNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The card was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTONull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "The request body must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", null, 
            null, 1800, 1200);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(cardDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        YugiohCardService spyService = Mockito.spy(service);

        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", null, 
            null, 1800, 1200);
        YugiohCard preparedCard = cardMocker.mockPrepareEntity(cardDTO);
        YugiohCard persistedCard = cardMocker.mockPrepareEntity(cardDTO);
        persistedCard.setId(1L);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        doReturn(preparedCard).when(spyService).prepareEntity(cardDTO);
        when(repository.save(preparedCard)).thenReturn(persistedCard);

        YugiohCardDTO output = spyService.create(cardDTO);
        assertEquals(1L, output.id());
        assertEquals("Crystal Beast Sapphire Pegasus", output.name());
        assertEquals(1L, output.category());
        assertEquals(1L, output.type());
        assertEquals("WIND", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertEquals("Effect lore text", output.effectLoreText());
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(1200, output.def());

        ArgumentCaptor<YugiohCard> argumentCaptor = ArgumentCaptor.forClass(YugiohCard.class);
        verify(repository).save(argumentCaptor.capture());
        YugiohCard capturedObject = argumentCaptor.getValue();
        assertNull(capturedObject.getId());
        assertNull(capturedObject.getIdYgoprodeck()); 
    }

    @Test
    void testUpdateWithParamIdNull() {
        YugiohCardDTO cardDTO = cardMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, cardDTO);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        YugiohCardDTO cardDTO = cardMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, cardDTO);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        YugiohCardDTO cardDTO = cardMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-10L, cardDTO);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTONull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, null);
        });
        String expectedMessage = "The request body must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdNull() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            null, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", null, 
            null, 1800, 1200);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, cardDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the yugioh-card-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdMismatchParamId() {
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            2L, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", null, 
            null, 1800, 1200);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, cardDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the yugioh-card-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        YugiohCardDTO cardDTO = cardMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, cardDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        YugiohCardService spyService = Mockito.spy(service);
        
        YugiohCard card = cardMocker.mockEntity(1);
        YugiohCardDTO cardDTO = new YugiohCardDTO(
            1L, "Crystal Beast Sapphire Pegasus", 1L, 1L, "WIND", 
            4, "Effect lore text", null, 
            null, 1800, 1200);
        YugiohCard preparedCard = cardMocker.mockPrepareEntity(cardDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.of(card));
        doReturn(preparedCard).when(spyService).prepareEntity(cardDTO);
        when(repository.save(preparedCard)).thenReturn(preparedCard);

        YugiohCardDTO output = spyService.update(1L, cardDTO);
        assertEquals(1L, output.id());
        assertEquals("Crystal Beast Sapphire Pegasus", output.name());
        assertEquals(1L, output.category());
        assertEquals(1L, output.type());
        assertEquals("WIND", output.attribute());
        assertEquals(4, output.levelRankLink());
        assertEquals("Effect lore text", output.effectLoreText());
        assertNull(output.pendulumScale());
        assertNull(output.linkArrows());
        assertEquals(1800, output.atk());
        assertEquals(1200, output.def());

        ArgumentCaptor<YugiohCard> argumentCaptor = ArgumentCaptor.forClass(YugiohCard.class);
        verify(repository).save(argumentCaptor.capture());
        YugiohCard capturedObject = argumentCaptor.getValue();
        assertEquals(1L, capturedObject.getId());
        assertEquals(1L, capturedObject.getIdYgoprodeck());
    }

    @Test
    void testUpdateWithProductNotFound() {
        YugiohCardDTO cardDTO = cardMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, cardDTO);
        });
        String expectedMessage = "The card was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(-10L);
        });
        String expectedMessage = "The yugioh-card-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithoutPermission() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithPermission() {
        YugiohCard card = cardMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.of(card));

        service.delete(1L);
    }

    @Test
    void testDeleteWithProductTypeNotFound() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The yugioh card was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
