package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.enums.YugiohCardAttribute;
import br.com.igormartinez.potygames.models.YugiohCard;
import br.com.igormartinez.potygames.models.YugiohCardCategory;
import br.com.igormartinez.potygames.models.YugiohCardType;

public class MockYugiohCard {
    
    public YugiohCard mockEntity(int number) {
        YugiohCard card = new YugiohCard();
        card.setId(Long.valueOf(number));
        card.setIdYgoprodeck(Long.valueOf(number));
        card.setName("Name " + number);
        card.setCategory(mockCategory(number));
        card.setType(mockType(number));
        card.setAttribute(YugiohCardAttribute.values()[(number%7)]);
        card.setLevelRankLink(number);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(number);
        card.setLinkArrows(Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "WN"));
        card.setAtk(number);
        card.setDef(number);

        return card;
    }

    public YugiohCard mockMonsterEntity(int number) {
        YugiohCard card = new YugiohCard();
        card.setId(Long.valueOf(number));
        card.setIdYgoprodeck(Long.valueOf(number));
        card.setName("Name " + number);
        card.setCategory(mockCategory(number, "Monster", "Normal", Boolean.TRUE));
        card.setType(mockType(number));
        card.setAttribute(YugiohCardAttribute.values()[(number%7)]);
        card.setLevelRankLink(number);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(null);
        card.setLinkArrows(null);
        card.setAtk(number);
        card.setDef(number);

        return card;
    }

    public YugiohCard mockLinkMonsterEntity(int number) {
        YugiohCard card = new YugiohCard();
        card.setId(Long.valueOf(number));
        card.setIdYgoprodeck(Long.valueOf(number));
        card.setName("Name " + number);
        card.setCategory(mockCategoryLinkMonster(number));
        card.setType(mockType(number));
        card.setAttribute(YugiohCardAttribute.values()[(number%7)]);
        card.setLevelRankLink(number);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(null);
        card.setLinkArrows(Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "WN"));
        card.setAtk(number);
        card.setDef(null);

        return card;
    }

    public YugiohCard mockPendulumMonsterEntity(int number) {
        YugiohCard card = new YugiohCard();
        card.setId(Long.valueOf(number));
        card.setIdYgoprodeck(Long.valueOf(number));
        card.setName("Name " + number);
        card.setCategory(mockCategoryPendulumMonster(number));
        card.setType(mockType(number));
        card.setAttribute(YugiohCardAttribute.values()[(number%7)]);
        card.setLevelRankLink(number);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(number);
        card.setLinkArrows(null);
        card.setAtk(number);
        card.setDef(number);

        return card;
    }

    public YugiohCard mockSpellEntity(int number) {
        YugiohCard card = new YugiohCard();
        card.setId(Long.valueOf(number));
        card.setIdYgoprodeck(Long.valueOf(number));
        card.setName("Name " + number);
        card.setCategory(mockCategory(number, "Spell", "Normal", Boolean.TRUE));
        card.setType(null);
        card.setAttribute(null);
        card.setLevelRankLink(null);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(null);
        card.setLinkArrows(null);
        card.setAtk(null);
        card.setDef(null);

        return card;
    }

    public YugiohCard mockPrepareEntity(YugiohCardDTO cardDTO) {
        YugiohCard card = new YugiohCard();
        card.setId(null);
        card.setIdYgoprodeck(null);
        card.setName(cardDTO.name());
        card.setCategory(mockCategory(cardDTO.category().intValue()));
        card.setType(mockType(cardDTO.type().intValue()));
        card.setAttribute(YugiohCardAttribute.valueOf(cardDTO.attribute()));
        card.setLevelRankLink(cardDTO.levelRankLink());
        card.setEffectLoreText(cardDTO.effectLoreText());
        card.setPendulumScale(cardDTO.pendulumScale());
        card.setLinkArrows(cardDTO.linkArrows());
        card.setAtk(cardDTO.atk());
        card.setDef(cardDTO.def());

        return card;
    }

    public List<YugiohCard> mockEntityList(int startNumber, int endNumber) {
        List<YugiohCard> list = new ArrayList<>();
        for (int i=startNumber; i<=endNumber; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }

    public Page<YugiohCard> mockEntityPage(int totalElements, Pageable pageable) {
        int sizePage = pageable.getPageSize();
        int numberPage = pageable.getPageNumber();

        int startNumber = 1 + (sizePage * numberPage);
        int endNumber = (numberPage + 1) * sizePage;
        List<YugiohCard> mockList = mockEntityList(startNumber, Math.min(totalElements, endNumber));
        
        Page<YugiohCard> page = new PageImpl<>(mockList, pageable, totalElements);
        return page;
    }

    public YugiohCardCategory mockCategory(int number) {
        return mockCategory(
            number, 
            "Category " + number,
            "Subcategory " + number,
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE);
    }

    public YugiohCardCategory mockCategoryPendulumMonster(int number) {
        return mockCategory(
            number, 
            "Monster",
            "Pendulum",
            Boolean.TRUE);
    }

    public YugiohCardCategory mockCategoryLinkMonster(int number) {
        return mockCategory(
            number, 
            "Monster",
            "Link",
            Boolean.FALSE);
    }

    public YugiohCardCategory mockCategory(int number, String category) {
        return mockCategory(
            number, 
            category,
            "Subcategory " + number,
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE);
    }

    public YugiohCardCategory mockCategory(int number, String category, String subcategory, Boolean mainDeck) {
        YugiohCardCategory cardCategory = new YugiohCardCategory();
        cardCategory.setId(Long.valueOf(number));
        cardCategory.setCategory(category);
        cardCategory.setSubCategory(subcategory);
        cardCategory.setMainDeck(mainDeck);
        return cardCategory;
    }

    public YugiohCardType mockType(int number) {
        YugiohCardType cardType = new YugiohCardType();
        cardType.setId(Long.valueOf(number));
        cardType.setDescription("Monster type " + number);
        return cardType;
    }

    public YugiohCardDTO mockDTO(int number){
        return new YugiohCardDTO(
            Long.valueOf(number), 
            "Name " + number, 
            Long.valueOf(number), 
            Long.valueOf(number), 
            YugiohCardAttribute.values()[(number%7)].name(), 
            number, 
            "Effect lore text " + number, 
            number, 
            Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "WN"), 
            number, 
            number);
    }
}
