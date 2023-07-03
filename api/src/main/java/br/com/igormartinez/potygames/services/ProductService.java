package br.com.igormartinez.potygames.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
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

    public Product prepareEntity(Product product, ProductDTO productDTO) {
        if (productDTO == null)
            throw new IllegalArgumentException("The productDTO argument must not be null.");

        if (product == null)
            product = new Product();

        if (productDTO.idProductType() == null) 
            throw new RequestValidationException("The product type ID must not be null.");
        ProductType type = productTypeRepository.findById(productDTO.idProductType())
            .orElseThrow(() -> new ResourceNotFoundException("The product type was not found with the given ID."));
        product.setType(type);

        if (productDTO.name() == null || productDTO.name().isBlank()) 
            throw new RequestValidationException("The product name must not be blank.");
        product.setName(productDTO.name());

        product.setDescription(
            (productDTO.description() == null || productDTO.description().isBlank())
            ? null : productDTO.description()
        );

        return product;
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

    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO == null)
           throw new RequestValidationException("The request body must not be null.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        Product product = prepareEntity(null, productDTO);

        return productDTOMapper.apply(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductDTO productDTO) {
        if (id == null || id <= 0)
            throw new RequestValidationException("The product-id must be a positive integer value.");

        if (productDTO == null)
            throw new RequestValidationException("The request body must not be null.");

        if (productDTO.id() == null || productDTO.id().compareTo(id) != 0)
            throw new RequestValidationException("The ID in the request body must match the value of the product-id parameter.");

        if(!securityContextManager.checkAdmin())
            throw new UserUnauthorizedException();

        Product product = productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("The product was not found with the given ID."));

        product = prepareEntity(product, productDTO);

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
        
        productRepository.delete(product);
    }
}
