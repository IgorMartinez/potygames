package br.com.igormartinez.potygames.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductDTOMapper;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductDTOMapper productDTOMapper;
    private final SecurityContextManager securityContextManager;

    public ProductService(ProductRepository productRepository, ProductTypeRepository productTypeRepository,
            ProductDTOMapper productDTOMapper, SecurityContextManager securityContextManager) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
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
            throw new RequestObjectIsNullException();

        return productRepository.findById(id)
            .map(productDTOMapper)
            .orElseThrow(() -> new ResourceNotFoundException());
    }

    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO == null 
            || productDTO.idProductType() == null || productDTO.idProductType() <= 0
            || productDTO.name() == null || productDTO.name().isBlank()
            || productDTO.price() == null || productDTO.price().signum() == -1
            || productDTO.quantity() == null || productDTO.quantity() < 0)
           throw new RequestObjectIsNullException();

        if(!securityContextManager.checkAdmin()) {
            throw new UserUnauthorizedException();
        }

        ProductType type = productTypeRepository
            .findById(productDTO.idProductType())
            .orElseThrow(() -> new ResourceNotFoundException());

        Product product = new Product();
        product.setType(type);
        product.setName(productDTO.name());
        product.setAltName(productDTO.altName());
        product.setPrice(productDTO.price());
        product.setQuantity(productDTO.quantity());

        return productDTOMapper.apply(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductDTO productDTO) {
        if (id == null || id <= 0
            || productDTO == null
            || productDTO.id() == null || productDTO.id().compareTo(id) != 0
            || productDTO.idProductType() == null || productDTO.idProductType() <= 0
            || productDTO.name() == null || productDTO.name().isBlank()
            || productDTO.price() == null || productDTO.price().signum() == -1
            || productDTO.quantity() == null || productDTO.quantity() < 0)
        throw new RequestObjectIsNullException();

        if(!securityContextManager.checkAdmin()) {
            throw new UserUnauthorizedException();
        }

        ProductType type = productTypeRepository
            .findById(productDTO.idProductType())
            .orElseThrow(() -> new ResourceNotFoundException());

        Product product = productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException());
        
        product.setType(type);
        product.setName(productDTO.name());
        product.setAltName(productDTO.altName());
        product.setPrice(productDTO.price());
        product.setQuantity(productDTO.quantity());

        return productDTOMapper.apply(productRepository.save(product));
    }

    public void delete(Long id) {
        if (id == null || id <= 0)
            throw new RequestObjectIsNullException();

        if(!securityContextManager.checkAdmin()) {
            throw new UserUnauthorizedException();
        }

        Product product = productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException());
        
        productRepository.delete(product);
    }
}
