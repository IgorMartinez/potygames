package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.models.YugiohCard;

@Service
public class YugiohCardDTOMapper implements Function<YugiohCard, YugiohCardDTO> {

    @Override
    public YugiohCardDTO apply(YugiohCard card) {
        return new YugiohCardDTO(
            card.getId(), 
            card.getName(),
            card.getCategory() == null ? null : card.getCategory().getId(), 
            card.getType() == null ? null : card.getType().getId(), 
            card.getAttribute() == null ? null : card.getAttribute().name(),
            card.getLevelRankLink(), 
            card.getEffectLoreText(), 
            card.getPendulumScale(), 
            card.getLinkArrows(), 
            card.getAtk(), 
            card.getDef());
    }
    
}
