package br.com.igormartinez.potygames.services;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
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

    public List<ProductTypeDTO> findAll() {
        return productTypeRepository.findAll()
            .stream()
            .map(productTypeDTOMapper)
            .toList();
    }

    public ProductTypeDTO findById(Long id) {
        if (id == null || id <= 0)
            throw new RequestObjectIsNullException();

        return productTypeRepository.findById(id)
            .map(productTypeDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException());
    }

    public ProductTypeDTO create(ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null 
            || productTypeDTO.description() == null || productTypeDTO.description().isBlank())
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        ProductType productType = new ProductType();
        productType.setDescription(productTypeDTO.description());

        return productTypeDTOMapper.apply(productTypeRepository.save(productType));
    }

    public ProductTypeDTO update(Long id, ProductTypeDTO productTypeDTO) {
        if (id == null || id <= 0
            ||productTypeDTO == null
            || productTypeDTO.id() == null || productTypeDTO.id().compareTo(id) != 0
            || productTypeDTO.description() == null || productTypeDTO.description().isBlank())
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        ProductType productType = productTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException());

        productType.setDescription(productTypeDTO.description());

        return productTypeDTOMapper.apply(productTypeRepository.save(productType));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestObjectIsNullException();

        if (!securityContextManager.checkAdmin()) 
            throw new UserUnauthorizedException();

        if (productRepository.countProductsByIdProductType(id) > 0)
            throw new DeleteAssociationConflictException();

        ProductType productType = productTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException());

        productTypeRepository.delete(productType);
    }
} 
