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
        card.setCategory(mockCategoryEntity(number));
        card.setType(mockTypeEntity(number));
        card.setAttribute(YugiohCardAttribute.values()[(number%7)]);
        card.setLevelRankLink(number);
        card.setEffectLoreText("Effect lore text " + number);
        card.setPendulumScale(number);
        card.setLinkArrows(Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "WN"));
        card.setAtk(number);
        card.setDef(number);

        return card;
    }

    public YugiohCard mockPrepareEntity(YugiohCardDTO cardDTO) {
        YugiohCard card = new YugiohCard();
        card.setId(null);
        card.setIdYgoprodeck(null);
        card.setName(cardDTO.name());
        card.setCategory(mockCategoryEntity(cardDTO.category().intValue()));
        card.setType(mockTypeEntity(cardDTO.type().intValue()));
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

    public YugiohCardCategory mockCategoryEntity(int number) {
        YugiohCardCategory cardCategory = new YugiohCardCategory();
        cardCategory.setId(Long.valueOf(number));
        cardCategory.setCategory("Category " + number);
        cardCategory.setSubCategory("Subcategory " + number);
        cardCategory.setMainDeck((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        return cardCategory;
    }

    public YugiohCardCategory mockCategoryEntity(int number, String category) {
        YugiohCardCategory cardCategory = new YugiohCardCategory();
        cardCategory.setId(Long.valueOf(number));
        cardCategory.setCategory(category);
        cardCategory.setSubCategory("Subcategory " + number);
        cardCategory.setMainDeck((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        return cardCategory;
    }

    public YugiohCardCategory mockPendulumMonsterCategory(int number) {
        YugiohCardCategory cardCategory = new YugiohCardCategory();
        cardCategory.setId(Long.valueOf(number));
        cardCategory.setCategory("Monster");
        cardCategory.setSubCategory("Pendulum");
        cardCategory.setMainDeck(Boolean.TRUE);
        return cardCategory;
    }

    public YugiohCardCategory mockLinkMonsterCategory(int number) {
        YugiohCardCategory cardCategory = new YugiohCardCategory();
        cardCategory.setId(Long.valueOf(number));
        cardCategory.setCategory("Monster");
        cardCategory.setSubCategory("Link");
        cardCategory.setMainDeck(Boolean.FALSE);
        return cardCategory;
    }

    public YugiohCardType mockTypeEntity(int number) {
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
