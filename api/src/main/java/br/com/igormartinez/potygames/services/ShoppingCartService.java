package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.ShoppingCartItemRequestDTO;
import br.com.igormartinez.potygames.data.response.ShoppingCartItemResponseDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceAlreadyExistsException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ShoppingCartItemEntityToDTOMapper;
import br.com.igormartinez.potygames.models.InventoryItem;
import br.com.igormartinez.potygames.models.ShoppingCartItem;
import br.com.igormartinez.potygames.models.User;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ShoppingCartItemRepository;
import br.com.igormartinez.potygames.repositories.UserRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class ShoppingCartService {
    private final ShoppingCartItemRepository repository;
    private final InventoryItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SecurityContextManager securityContextManager;
    private final ShoppingCartItemEntityToDTOMapper mapper;

    public ShoppingCartService(ShoppingCartItemRepository repository, InventoryItemRepository itemRepository,
            UserRepository userRepository, SecurityContextManager securityContextManager,
            ShoppingCartItemEntityToDTOMapper mapper) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.securityContextManager = securityContextManager;
        this.mapper = mapper;
    }

    /**
     * Get all items in a user's shopping cart.
     * @param idUser must be not null and greater than zero.
     * @return
     */
    public List<ShoppingCartItemResponseDTO> findAllByUser(Long idUser) {
        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        return repository.findAllByUserId(idUser)
            .stream()
            .map(mapper)
            .toList();
    }

    /**
     * Add a item in user's shopping cart.
     * @param idUser must be not null and greater than zero.
     * @param itemDTO must be already validated.
     * @return
     */
    public ShoppingCartItemResponseDTO addItemToCart(Long idUser, ShoppingCartItemRequestDTO itemDTO) {
        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new ResourceNotFoundException("The user was not found with the given ID."));

        InventoryItem item = itemRepository.findById(itemDTO.idInventoryItem())
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not found with the given ID."));
            
        if (repository.existsByUserIdAndItemId(idUser, itemDTO.idInventoryItem()))
            throw new ResourceAlreadyExistsException("The inventory item was already add to the cart.");
        
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setUser(user);
        shoppingCartItem.setItem(item);
        shoppingCartItem.setQuantity(itemDTO.quantity());

        return mapper.apply(repository.save(shoppingCartItem));
    }

    /**
     * Update a item in user's shopping cart.
     * @param idUser must be not null and greater than zero.
     * @param idInventoryItem must be not null and greater than zero.
     * @param itemDTO must be already validated.
     * @return
     */
    public ShoppingCartItemResponseDTO updateItemInCart(Long idUser, Long idInventoryItem, 
        ShoppingCartItemRequestDTO itemDTO) {
        
        if (idInventoryItem.compareTo(itemDTO.idInventoryItem()) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the inventory-item-id parameter.");

        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        ShoppingCartItem shoppingCartItem = repository.findByUserIdAndItemId(idUser, idInventoryItem)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not added to the cart."));

        shoppingCartItem.setQuantity(itemDTO.quantity());

        return mapper.apply(repository.save(shoppingCartItem));
    }

    /**
     * Remove a item from user's shopping cart.
     * @param idUser must be not null and greater than zero.
     * @param idInventoryItem must be not null and greater than zero.
     */
    public void removeItemFromCart(Long idUser, Long idInventoryItem) {
        if (!securityContextManager.checkSameUserOrAdmin(idUser))
            throw new UserUnauthorizedException();

        ShoppingCartItem shoppingCartItem = repository.findByUserIdAndItemId(idUser, idInventoryItem)
            .orElseThrow(() -> new ResourceNotFoundException("The inventory item was not added to the cart."));

        repository.delete(shoppingCartItem);
    }
}
