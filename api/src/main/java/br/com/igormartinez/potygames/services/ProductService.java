package br.com.igormartinez.potygames.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.request.ProductCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductUpdateDTO;
import br.com.igormartinez.potygames.data.response.ProductDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductToProductDTOMapper;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductToProductDTOMapper productDTOMapper;
    private final SecurityContextManager securityContextManager;

    public ProductService(ProductRepository productRepository, ProductTypeRepository productTypeRepository,
            InventoryItemRepository inventoryItemRepository, ProductToProductDTOMapper productDTOMapper,
            SecurityContextManager securityContextManager) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.productDTOMapper = productDTOMapper;
        this.securityContextManager = securityContextManager;
    }

    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository
            .findAll(pageable)
            .map(productDTOMapper);
    }

    public ProductDTO findById(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-id must be a positive integer value.");

        return productRepository.findById(id)
            .map(productDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));
    }

    public ProductDTO create(ProductCreateDTO productDTO) {
        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        ProductType type = productTypeRepository.findById(productDTO.idProductType())
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));

        Product product = new Product();
        product.setType(type);
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());

        return productDTOMapper.apply(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductUpdateDTO productDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-id must be a positive integer value.");

        if (productDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the product-id parameter.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        ProductType type = productTypeRepository.findById(productDTO.idProductType())
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));

        product.setType(type);
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());

        return productDTOMapper.apply(productRepository.save(product));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-id must be a positive integer value.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        Product product = productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));
        
        if (inventoryItemRepository.countByIdProduct(id) > 0)
            throw new DeleteAssociationConflictException("The product cannot be removed because it is associated with inventory items.");

        productRepository.delete(product);
    }
}
