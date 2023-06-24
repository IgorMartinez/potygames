package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductDTOMapper;
import br.com.igormartinez.potygames.mocks.MockProduct;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.ProductService;


@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    private ProductService service;
    private MockProduct productMocker;
    private MockProductType productTypeMocker;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        productMocker = new MockProduct();
        productTypeMocker = new MockProductType();

        service = new ProductService(
            productRepository, 
            productTypeRepository, 
            new ProductDTOMapper(), 
            securityContextManager);
    }

    @Test
    void testPrepareEntityWithProductDTONull() {
        Exception output = assertThrows(IllegalArgumentException.class, () -> {
            service.prepareEntity(null, null);
        });
        String expectedMessage = "The productDTO argument must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithProductTypeIdNull() {
        ProductDTO productDTO = new ProductDTO(
            null, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product type ID must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithProductTypeNotFound() {
        ProductDTO productDTO = new ProductDTO(
            null, 0L, null, 
            null, null, null);

        when(productTypeRepository.findById(0L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithNameNull() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, null, 
            null, null, null);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product name must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithNameBlank() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, "  ", 
            null, null, null);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product name must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithPriceNull() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            null, null, null);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product price must be greater than or equal to zero.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithPriceNegative() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            null, new BigDecimal("-1.97"), null);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The product price must be greater than or equal to zero.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithQuantityNull() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            null, new BigDecimal("1.97"), null);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The quantity of the product must not be null or negative.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithQuantityNegative() {
        ProductType type = productTypeMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            null, new BigDecimal("1.97"), -100);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(null, productDTO);
        });
        String expectedMessage = "The quantity of the product must not be null or negative.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithProductNull() {
        ProductType type = productTypeMocker.mockEntity(1);
        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            null, new BigDecimal("1.99"), 1);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        Product output = service.prepareEntity(null, productDTO);
        assertNotNull(output);
        assertNull(output.getId());
        assertEquals(1L, output.getType().getId());
        assertEquals("Description 1", output.getType().getDescription());
        assertEquals("Product name 1", output.getName());
        assertNull(output.getAltName());
        assertEquals(new BigDecimal("1.99"), output.getPrice());
        assertEquals(1, output.getQuantity());
    }

    @Test
    void testPrepareEntityWithProductNotNull() {
        ProductType type = productTypeMocker.mockEntity(2);
        Product product = productMocker.mockEntity(1);

        ProductDTO productDTO = new ProductDTO(
            888L, 2L, "Product name update", 
            "Alt name updated", new BigDecimal("6.57"), 47);

        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.of(type));

        Product output = service.prepareEntity(product, productDTO);
        assertNotNull(output);
        assertEquals(1L, output.getId());
        assertEquals(2L, output.getType().getId());
        assertEquals("Description 2", output.getType().getDescription());
        assertEquals("Product name update", output.getName());
        assertEquals("Alt name updated", output.getAltName());
        assertEquals(new BigDecimal("6.57"), output.getPrice());
        assertEquals(47, output.getQuantity());
    }

    @Test
    void testFindAllWithProductsPage0() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "id"));
        Page<Product> page = productMocker.mockProductPage(94, pageable);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDTO> outputPage = service.findAll(pageable);
        assertEquals(0, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<ProductDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals(1L, outputPosition0.idProductType());
        assertEquals("Product name 1", outputPosition0.name());
        assertEquals("Product alt name 1", outputPosition0.altName());
        assertEquals(new BigDecimal("1.99"), outputPosition0.price());
        assertEquals(1, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(5L, outputPosition4.idProductType());
        assertEquals("Product name 5", outputPosition4.name());
        assertEquals("Product alt name 5", outputPosition4.altName());
        assertEquals(new BigDecimal("5.99"), outputPosition4.price());
        assertEquals(5, outputPosition4.quantity());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals(10L, outputPosition9.idProductType());
        assertEquals("Product name 10", outputPosition9.name());
        assertEquals("Product alt name 10", outputPosition9.altName());
        assertEquals(new BigDecimal("10.99"), outputPosition9.price());
        assertEquals(10, outputPosition9.quantity());
    }

    @Test
    void testFindAllWithProductsPage4() {
        Pageable pageable = PageRequest.of(4, 10, Sort.by(Direction.ASC, "name"));
        Page<Product> page = productMocker.mockProductPage(94, pageable);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDTO> outputPage = service.findAll(pageable);
        assertEquals(4, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<ProductDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(10, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(41L, outputPosition0.id());
        assertEquals(41L, outputPosition0.idProductType());
        assertEquals("Product name 41", outputPosition0.name());
        assertEquals("Product alt name 41", outputPosition0.altName());
        assertEquals(new BigDecimal("41.99"), outputPosition0.price());
        assertEquals(41, outputPosition0.quantity());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(45L, outputPosition4.id());
        assertEquals(45L, outputPosition4.idProductType());
        assertEquals("Product name 45", outputPosition4.name());
        assertEquals("Product alt name 45", outputPosition4.altName());
        assertEquals(new BigDecimal("45.99"), outputPosition4.price());
        assertEquals(45, outputPosition4.quantity());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(50L, outputPosition9.id());
        assertEquals(50L, outputPosition9.idProductType());
        assertEquals("Product name 50", outputPosition9.name());
        assertEquals("Product alt name 50", outputPosition9.altName());
        assertEquals(new BigDecimal("50.99"), outputPosition9.price());
        assertEquals(50, outputPosition9.quantity());
    }

    @Test
    void testFindAllWithProductsPage9() {
        Pageable pageable = PageRequest.of(9, 10, Sort.by(Direction.ASC, "name"));
        Page<Product> page = productMocker.mockProductPage(94, pageable);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDTO> outputPage = service.findAll(pageable);
        assertEquals(9, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(10, outputPage.getTotalPages());
        assertEquals(94, outputPage.getTotalElements());

        List<ProductDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(4, output.size());

        ProductDTO outputPosition0 = output.get(0);
        assertEquals(91L, outputPosition0.id());
        assertEquals(91L, outputPosition0.idProductType());
        assertEquals("Product name 91", outputPosition0.name());
        assertEquals("Product alt name 91", outputPosition0.altName());
        assertEquals(new BigDecimal("91.99"), outputPosition0.price());
        assertEquals(91, outputPosition0.quantity());

        ProductDTO outputPosition3 = output.get(3);
        assertEquals(94L, outputPosition3.id());
        assertEquals(94L, outputPosition3.idProductType());
        assertEquals("Product name 94", outputPosition3.name());
        assertEquals("Product alt name 94", outputPosition3.altName());
        assertEquals(new BigDecimal("94.99"), outputPosition3.price());
        assertEquals(94, outputPosition3.quantity());
    }

    @Test
    void testFindAllWithoutProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "name"));
        Page<Product> page = productMocker.mockProductPage(0, pageable);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDTO> outputPage = service.findAll(pageable);
        assertEquals(0, outputPage.getNumber());
        assertEquals(10, outputPage.getSize());
        assertEquals(0, outputPage.getTotalPages());
        assertEquals(0, outputPage.getTotalElements());

        List<ProductDTO> output = outputPage.getContent();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testFindByIdWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(-10L);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithProductFound() {
        Product product = productMocker.mockEntity(1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(1L, output.idProductType());
        assertEquals("Product name 1", output.name());
        assertEquals("Product alt name 1", output.altName());
        assertEquals(new BigDecimal("1.99"), output.price());
        assertEquals(1, output.quantity());
    }

    @Test
    void testFindByIdWithProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The product was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTONull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "The request body must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        ProductDTO productDTO = new ProductDTO(
            null, 1L, "Product name 1", 
            "Product alt name 1", new BigDecimal("1.99"), 1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(productDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        ProductService spyService = Mockito.spy(service);

        ProductDTO productDTO = productMocker.mockDTO(1);
        Product product = productMocker.mockEntity(productDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        doReturn(product).when(spyService).
            prepareEntity(ArgumentMatchers.isNull(Product.class), ArgumentMatchers.any(ProductDTO.class));
        when(productRepository.save(product)).thenReturn(product);

        ProductDTO output = spyService.create(productDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(1L, output.idProductType());
        assertEquals("Product name 1", output.name());
        assertEquals("Product alt name 1", output.altName());
        assertEquals(new BigDecimal("1.99"), output.price());
        assertEquals(1, output.quantity());
    }

    @Test
    void testUpdateWithParamIdNull() {
        ProductDTO productDTO = productMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        ProductDTO productDTO = productMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        ProductDTO productDTO = productMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-10L, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTONull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, null);
        });
        String expectedMessage = "The request body must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdNull() {
        ProductDTO productDTO = new ProductDTO(
            null, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdMismatchParamId() {
        ProductDTO productDTO = new ProductDTO(
            2L, null, null, 
            null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        ProductDTO productDTO = productMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        ProductService spyService = Mockito.spy(service);
        
        ProductDTO productDTO = new ProductDTO(
            1L, 2L, "Product name updated 1", 
            "Alt name updated", new BigDecimal("99.11"), 2);
        Product product = productMocker.mockEntity(1);
        Product productUpdated = productMocker.mockEntity(productDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(productDTO.id())).thenReturn(Optional.of(product));
        doReturn(productUpdated).when(spyService).
            prepareEntity(ArgumentMatchers.any(Product.class), ArgumentMatchers.any(ProductDTO.class));
        when(productRepository.save(productUpdated)).thenReturn(productUpdated);

        ProductDTO output = spyService.update(1L, productDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Product name updated 1", output.name());
        assertEquals("Alt name updated", output.altName());
        assertEquals(new BigDecimal("99.11"), output.price());
        assertEquals(2, output.quantity());
    }

    @Test
    void testpdateWithProductNotFound() {
        ProductDTO productDTO = productMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(productDTO.id())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The product was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(-10L);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithoutPermission() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithPermission() {
        Product product = productMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);
    }

    @Test
    void testDeleteWithProductTypeNotFound() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The product was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
