package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import br.com.igormartinez.potygames.data.request.ProductCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductUpdateDTO;
import br.com.igormartinez.potygames.data.response.ProductDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductToProductDTOMapper;
import br.com.igormartinez.potygames.mocks.MockProduct;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.repositories.InventoryItemRepository;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.ProductService;


@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService service;
    private MockProduct productMocker;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;
    
    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        productMocker = new MockProduct(new MockProductType());

        service = new ProductService(
            productRepository, 
            productTypeRepository, 
            inventoryItemRepository,
            new ProductToProductDTOMapper(), 
            securityContextManager);
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
        assertEquals("Product description 1", outputPosition0.description());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals(5L, outputPosition4.idProductType());
        assertEquals("Product name 5", outputPosition4.name());
        assertEquals("Product description 5", outputPosition4.description());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals(10L, outputPosition9.idProductType());
        assertEquals("Product name 10", outputPosition9.name());
        assertEquals("Product description 10", outputPosition9.description());
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
        assertEquals("Product description 41", outputPosition0.description());

        ProductDTO outputPosition4 = output.get(4);
        assertEquals(45L, outputPosition4.id());
        assertEquals(45L, outputPosition4.idProductType());
        assertEquals("Product name 45", outputPosition4.name());
        assertEquals("Product description 45", outputPosition4.description());

        ProductDTO outputPosition9 = output.get(9);
        assertEquals(50L, outputPosition9.id());
        assertEquals(50L, outputPosition9.idProductType());
        assertEquals("Product name 50", outputPosition9.name());
        assertEquals("Product description 50", outputPosition9.description());
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
        assertEquals("Product description 91", outputPosition0.description());

        ProductDTO outputPosition3 = output.get(3);
        assertEquals(94L, outputPosition3.id());
        assertEquals(94L, outputPosition3.idProductType());
        assertEquals("Product name 94", outputPosition3.name());
        assertEquals("Product description 94", outputPosition3.description());
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
        assertEquals("Product description 1", output.description());
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
    void testCreateWithoutPermission() {
        ProductCreateDTO productDTO = productMocker.mockCreateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(productDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        ProductCreateDTO productDTO 
            = new ProductCreateDTO(1L,"Product name 1","Product description 1");
        Product product = productMocker.mockEntity(1, productDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.of(product.getType()));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Check the code after the save
        ProductDTO output = service.create(productDTO);
        assertEquals(1L, output.id());
        assertEquals(1L, output.idProductType());
        assertEquals("Product name 1", output.name());
        assertEquals("Product description 1", output.description());

        // Check the arguments before save
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());
        Product capturedObject = argumentCaptor.getValue();
        assertNull(capturedObject.getId());
        assertEquals(1L, capturedObject.getType().getId());
        assertEquals("Product name 1", capturedObject.getName());
        assertEquals("Product description 1", capturedObject.getDescription());
    }

    @Test
    void testCreateWithProductTypeNotFound() {
        ProductCreateDTO productDTO = productMocker.mockCreateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.create(productDTO);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNull() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-10L, productDTO);
        });
        String expectedMessage = "The product-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithMismatchParamIdAndParamDTOId() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(2L, productDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        ProductUpdateDTO productDTO = new ProductUpdateDTO(
            1L, 2L, "Product name updated 1", 
            "Product description updated 1");
        Product product = productMocker.mockEntity(1);
        Product productUpdated = productMocker.mockEntity(productDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.of(productUpdated.getType()));
        when(productRepository.findById(productDTO.id())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(productUpdated);

        // Check code after save
        ProductDTO output = service.update(1L, productDTO);
        assertEquals(1L, output.id());
        assertEquals(2L, output.idProductType());
        assertEquals("Product name updated 1", output.name());
        assertEquals("Product description updated 1", output.description());

        // Check arguments before save
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());
        Product capturedObject = argumentCaptor.getValue();
        assertEquals(1L, capturedObject.getId());
        assertEquals(2L, capturedObject.getType().getId());
        assertEquals("Product name updated 1", capturedObject.getName());
        assertEquals("Product description updated 1", capturedObject.getDescription());
    }

    @Test
    void testUpdateWithProductTypeNotFound() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithProductNotFound() {
        ProductUpdateDTO productDTO = productMocker.mockUpdateDTO(1);
        Product product = productMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(productDTO.idProductType())).thenReturn(Optional.of(product.getType()));
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
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
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

    @Test
    void testDeleteWithAssociatedInventoryItems() {
        Product product = productMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryItemRepository.countByIdProduct(1L)).thenReturn(11);

        Exception output = assertThrows(DeleteAssociationConflictException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The product cannot be removed because it is associated with inventory items.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDelete() {
        Product product = productMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryItemRepository.countByIdProduct(1L)).thenReturn(0);

        service.delete(1L);
    }
}
