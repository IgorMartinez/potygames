package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductTypeDTOMapper;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class ProductTypeService {
    
    private final ProductTypeRepository productTypeRepository;
    private final ProductRepository productRepository;
    private final ProductTypeDTOMapper productTypeDTOMapper;
    private final SecurityContextManager securityContextManager;

    public ProductTypeService(ProductTypeRepository productTypeRepository, ProductRepository productRepository,
            ProductTypeDTOMapper productTypeDTOMapper, SecurityContextManager securityContextManager) {
        this.productTypeRepository = productTypeRepository;
        this.productRepository = productRepository;
        this.productTypeDTOMapper = productTypeDTOMapper;
        this.securityContextManager = securityContextManager;
    }

    public ProductType prepareEntity(ProductTypeDTO typeDTO) {
        if (typeDTO == null)
            throw new IllegalArgumentException("The ProductTypeDTO argument must not be null.");

        ProductType type = new ProductType();

        if (typeDTO.keyword() == null || typeDTO.keyword().isBlank())
            throw new RequestValidationException("The keyword of product type must not be blank.");
        type.setKeyword(typeDTO.keyword());

        if (typeDTO.description() == null || typeDTO.description().isBlank())
            throw new RequestValidationException("The description of product type must not be blank.");
        type.setDescription(typeDTO.description());

        return type;
    }

    public List<ProductTypeDTO> findAll() {
        return productTypeRepository.findAll()
            .stream()
            .map(productTypeDTOMapper)
            .toList();
    }

    public ProductTypeDTO findById(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-type-id must be a positive integer value.");

        return productTypeRepository.findById(id)
            .map(productTypeDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));
    }

    public ProductTypeDTO create(ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null)
            throw new RequestValidationException("The request body must not be null.");

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        ProductType productType = prepareEntity(productTypeDTO);

        return productTypeDTOMapper.apply(productTypeRepository.save(productType));
    }

    public ProductTypeDTO update(Long id, ProductTypeDTO productTypeDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-type-id must be a positive integer value.");

        if (productTypeDTO == null)
            throw new RequestValidationException("The request body must not be null.");

        if (productTypeDTO.id() == null || productTypeDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the product-type-id parameter.");

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        ProductType productType = productTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));

        ProductType preparedProductType = prepareEntity(productTypeDTO);
        preparedProductType.setId(productType.getId());

        return productTypeDTOMapper.apply(productTypeRepository.save(preparedProductType));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-type-id must be a positive integer value.");

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        ProductType productType = productTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));

        if (productRepository.countProductsByIdProductType(id) > 0)
            throw new DeleteAssociationConflictException("The product type cannot be removed because it is associated with products.");

        productTypeRepository.delete(productType);
    }
} 
