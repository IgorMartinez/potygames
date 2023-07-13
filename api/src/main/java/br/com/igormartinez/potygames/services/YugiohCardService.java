package br.com.igormartinez.potygames.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.enums.YugiohCardAttribute;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.YugiohCardDTOMapper;
import br.com.igormartinez.potygames.models.YugiohCard;
import br.com.igormartinez.potygames.models.YugiohCardCategory;
import br.com.igormartinez.potygames.models.YugiohCardType;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardCategoryRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class YugiohCardService {

    private final List<String> LINK_ARROWS_VALID = Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "NW");
    
    private final YugiohCardRepository repository;
    private final YugiohCardCategoryRepository categoryRepository;
    private final YugiohCardTypeRepository typeRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final YugiohCardDTOMapper mapper;
    private final SecurityContextManager securityContextManager;

    public YugiohCardService(YugiohCardRepository repository, YugiohCardCategoryRepository categoryRepository,
            YugiohCardTypeRepository typeRepository, InventoryItemRepository inventoryItemRepository,
            YugiohCardDTOMapper mapper, SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.typeRepository = typeRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.mapper = mapper;
        this.securityContextManager = securityContextManager;
    }

    public YugiohCard prepareEntity(YugiohCardDTO cardDTO) {
        if (cardDTO == null)
            throw new IllegalArgumentException("The YugiohCardDTO argument must not be null.");

        YugiohCard card = new YugiohCard();

        if(cardDTO.name() == null || cardDTO.name().isBlank())
            throw new RequestValidationException("The card name must not be blank.");
        card.setName(cardDTO.name());

        if (cardDTO.category() == null)
            throw new RequestValidationException("The card category must not be null.");
        YugiohCardCategory category = categoryRepository
            .findById(cardDTO.category())
            .orElseThrow(() -> new ResourceNotFoundException("The card category was not found with the given ID."));
        card.setCategory(category);
        
        card.setEffectLoreText((cardDTO.effectLoreText() == null || cardDTO.effectLoreText().isBlank())
            ? null : cardDTO.effectLoreText());

        if (category.isMonster()) {
            if (cardDTO.type() == null) 
                throw new RequestValidationException("The card type must not be null.");

            YugiohCardType type = typeRepository.findById(cardDTO.type())
                .orElseThrow(() -> new ResourceNotFoundException("The card type was not found with the given ID."));
            card.setType(type);

            if (!YugiohCardAttribute.isInEnum(cardDTO.attribute().toUpperCase()))
                throw new ResourceNotFoundException("The attribute of monster card must be DARK, DIVINE, EARTH, FIRE, LIGHT, WATER or WIND.");
            card.setAttribute(YugiohCardAttribute.valueOf(cardDTO.attribute().toUpperCase()));

            card.setLevelRankLink(cardDTO.levelRankLink());

            if (cardDTO.atk() != null && cardDTO.atk() < 0)
                throw new RequestValidationException("The attack of monster card must be null, zero or positive.");
            card.setAtk(cardDTO.atk());

            // The def of link monster is null
            if (!category.isLinkMonster()) {
                if (cardDTO.def() != null && cardDTO.def() < 0)
                    throw new RequestValidationException("The defense of monster card must be null, zero or positive.");
                card.setDef(cardDTO.def());
            }
        }

        if (category.isPendulumMonster()) {
            if (cardDTO.pendulumScale() == null || cardDTO.pendulumScale() < 0)
                throw new RequestValidationException("The pendulum scale of a pendulum monster must be zero or positive.");
            card.setPendulumScale(cardDTO.pendulumScale());
        }

        if (category.isLinkMonster()) {
            if (cardDTO.linkArrows() == null) 
                throw new RequestValidationException("The link arrows array of a link monster must be not null.");

            List<String> linkArrows = cardDTO.linkArrows()
                .stream()
                .map(String::toUpperCase)
                .filter(link -> LINK_ARROWS_VALID.contains(link))
                .distinct()
                .collect(Collectors.toList());

            if (linkArrows.size() == 0) 
                throw new RequestValidationException("The link arrows array of a link monster must contain valid coordinates.");
            card.setLinkArrows(linkArrows);
        }

        return card;
    }

    public Page<YugiohCardDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper);
    }

    public YugiohCardDTO findById(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The yugioh-card-id must be a positive integer value.");

        return repository.findById(id)
            .map(mapper)
            .orElseThrow(() -> new ResourceNotFoundException("The card was not found with the given ID."));
    }

    public YugiohCardDTO create(YugiohCardDTO cardDTO) {
        if (cardDTO == null)
            throw new RequestValidationException("The request body must not be null.");
        
        if (!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();
        
        YugiohCard card = prepareEntity(cardDTO);
        return mapper.apply(repository.save(card));
    }

    public YugiohCardDTO update(Long id, YugiohCardDTO cardDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The yugioh-card-id must be a positive integer value.");

        if (cardDTO == null)
            throw new RequestValidationException("The request body must not be null.");

        if (cardDTO.id() == null || cardDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the yugioh-card-id parameter.");

        if (!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        YugiohCard card = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The card was not found with the given ID."));
        
        YugiohCard preparedCard = prepareEntity(cardDTO);
        preparedCard.setId(card.getId());
        preparedCard.setIdYgoprodeck(card.getIdYgoprodeck());
        
        return mapper.apply(repository.save(preparedCard));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The yugioh-card-id must be a positive integer value.");
        
        if (!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        YugiohCard card = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The card was not found with the given ID."));

        if (inventoryItemRepository.countByIdYugiohCard(id) > 0)
            throw new DeleteAssociationConflictException("The card cannot be removed because it is associated with inventory items.");

        repository.delete(card);
    }

    public List<YugiohCardAttribute> findAllAttributes() {
        return Arrays.asList(YugiohCardAttribute.values());
    }

    public List<YugiohCardCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<YugiohCardType> findAllTypes() {
        return typeRepository.findAll();
    }
}
