package br.com.igormartinez.potygames.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.InventoryItemDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.InventoryItemDTOMapper;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.YugiohCardRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository repository;
    private final ProductRepository productRepository;
    private final YugiohCardRepository yugiohCardRepository;
    private final InventoryItemDTOMapper mapper;
    private final SecurityContextManager securityContextManager;

    public InventoryItemService(InventoryItemRepository repository, ProductRepository productRepository,
            YugiohCardRepository yugiohCardRepository, InventoryItemDTOMapper mapper,
            SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.yugiohCardRepository = yugiohCardRepository;
        this.mapper = mapper;
        this.securityContextManager = securityContextManager;
    }

    public InventoryItem prepareEntity(InventoryItemDTO itemDTO) {
        if (itemDTO == null)
            throw new IllegalArgumentException("The itemDTO argument must not be null.");

        InventoryItem item = new InventoryItem();

        if (itemDTO.product() == null && itemDTO.yugiohCard() == null)
            throw new RequestValidationException("A product or yugioh card must be provided.");

        if (itemDTO.product() != null && itemDTO.yugiohCard() != null)
            throw new RequestValidationException("Only product or yugioh card must be provided, not both.");

        if (itemDTO.product() != null) {
            item.setProduct(
                productRepository.findById(itemDTO.product())
                    .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."))
            );
        }
        
        if (itemDTO.yugiohCard() != null) {
            item.setYugiohCard(
                yugiohCardRepository.findById(itemDTO.yugiohCard())
                    .orElseThrow(() -> new ResourceNotFoundException("The yugioh card was not found with the given ID."))
            );
        }

        item.setVersion(itemDTO.version().isBlank() ? null : itemDTO.version());
        item.setCondition(itemDTO.condition().isBlank() ? null : itemDTO.condition());
        
        if (itemDTO.price() != null && itemDTO.price().signum() == -1)
            throw new RequestValidationException("The price must be null, zero or positive.");
        item.setPrice(itemDTO.price());

        if (itemDTO.quantity() != null && itemDTO.quantity() < 0) 
            throw new RequestValidationException("The quantity must be null, zero or positive.");
        item.setQuantity(itemDTO.quantity());
        
        return item;
    }

    public Page<InventoryItemDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper);
    }

    public InventoryItemDTO findById(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The inventory-item-id must be a positive integer value.");

        return repository.findById(id)
            .map(mapper)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
    }

    public InventoryItemDTO create(InventoryItemDTO itemDTO) {
        if (itemDTO == null)
            throw new RequestValidationException("The request body must not be null.");
        
        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        InventoryItem item = prepareEntity(itemDTO);

        return mapper.apply(repository.save(item));
    }

    public InventoryItemDTO update(Long id, InventoryItemDTO itemDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The inventory-item-id must be a positive integer value.");

        if (itemDTO == null)
            throw new RequestValidationException("The request body must not be null.");

        if (itemDTO.id() == null || itemDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the inventory-item-id parameter.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        InventoryItem item = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
        
        InventoryItem preparedItem = prepareEntity(itemDTO);
        preparedItem.setId(item.getId());

        return mapper.apply(repository.save(preparedItem));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The inventory-item-id must be a positive integer value.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        InventoryItem item = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
        
        repository.delete(item);
    }
}
