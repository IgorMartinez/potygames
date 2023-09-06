package br.com.igormartinez.potygames.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.InventoryItemCreateDTO;
import br.com.igormartinez.potygames.data.request.InventoryItemUpdateDTO;
import br.com.igormartinez.potygames.data.response.InventoryItemDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.InventoryItemToInventoryItemDTOMapper;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository repository;
    private final ProductRepository productRepository;
    private final InventoryItemToInventoryItemDTOMapper mapper;
    private final SecurityContextManager securityContextManager;

    public InventoryItemService(InventoryItemRepository repository, ProductRepository productRepository,
        InventoryItemToInventoryItemDTOMapper mapper, SecurityContextManager securityContextManager) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.securityContextManager = securityContextManager;
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

    public InventoryItemDTO create(InventoryItemCreateDTO itemDTO) {
        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        Product product = productRepository.findById(itemDTO.product())
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));

        InventoryItem item = new InventoryItem();
        item.setProduct(product);
        item.setVersion(itemDTO.version());
        item.setCondition(itemDTO.condition().isBlank() ? null : itemDTO.condition());
        item.setPrice(itemDTO.price());
        item.setQuantity(itemDTO.quantity());

        return mapper.apply(repository.save(item));
    }

    public InventoryItemDTO update(Long id, InventoryItemUpdateDTO itemDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The inventory-item-id must be a positive integer value.");

        if (itemDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the inventory-item-id parameter.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        InventoryItem item = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
        
        Product product = productRepository.findById(itemDTO.product())
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));

        item.setProduct(product);
        item.setVersion(itemDTO.version());
        item.setCondition(itemDTO.condition().isBlank() ? null : itemDTO.condition());
        item.setPrice(itemDTO.price());
        item.setQuantity(itemDTO.quantity());

        return mapper.apply(repository.save(item));
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
