package br.com.igormartinez.potygames.unittests.services;

import static org.junit.Assert.assertNull;
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

import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestObjectIsNullException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductTypeDTOMapper;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.ProductTypeService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProductTypeServiceTest {

    private ProductTypeService service;
    private MockProductType productTypeMocker;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        productTypeMocker = new MockProductType();

        service = new ProductTypeService(
            productTypeRepository, 
            productRepository, 
            new ProductTypeDTOMapper(), 
            securityContextManager);
    }

    @Test
    void testFindAllWithProductTypes() {
        List<ProductType> list = productTypeMocker.mockEntityList(10);

        when(productTypeRepository.findAll()).thenReturn(list);

        List<ProductTypeDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(10, output.size());

        ProductTypeDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("Description 1", outputPosition0.description());

        ProductTypeDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals("Description 5", outputPosition4.description());

        ProductTypeDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("Description 10", outputPosition9.description());
    }

    @Test
    void testFindAllWithoutProductTypes() {
        List<ProductType> list = productTypeMocker.mockEntityList(0);

        when(productTypeRepository.findAll()).thenReturn(list);

        List<ProductTypeDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testFindByIdWithParamIdNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.findById(-10L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithProductFound() {
        ProductType productType = productTypeMocker.mockEntity(1);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        ProductTypeDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("Description 1", output.description());
    }

    @Test
    void testFindByIdWithProductNotFound() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTONull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTODescriptionNull() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(productTypeDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithParamDTODescriptionBlank() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, " ");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.create(productTypeDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, "Some description");

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(productTypeDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        ProductType productType = productTypeMocker.mockEntity(1);
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null, "Description 1");

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);
        
        ProductTypeDTO output = service.create(productTypeDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("Description 1", output.description());
        
        ArgumentCaptor<ProductType> productTypeCaptor = ArgumentCaptor.forClass(ProductType.class);
        verify(productTypeRepository).save(productTypeCaptor.capture());
        ProductType capturedProductType = productTypeCaptor.getValue();
        assertNotNull(capturedProductType);
        assertNull(capturedProductType.getId());
        assertEquals("Description 1", capturedProductType.getDescription());
    }

    @Test
    void testUpdateWithParamIdNull() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(null, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(0L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(-10L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTONull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdNull() {
        ProductTypeDTO productDTO = new ProductTypeDTO(null, "Some description");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdMismatchParamId() {
        ProductTypeDTO productDTO = new ProductTypeDTO(2L, "Some description");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTODescriptionNull() {
        ProductTypeDTO productDTO = new ProductTypeDTO(1L, null);

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTODescriptionBlank() {
        ProductTypeDTO productDTO = new ProductTypeDTO(1L, " ");

        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        ProductType productType = productTypeMocker.mockEntity(1);
        ProductTypeDTO updateProductTypeDTO = new ProductTypeDTO(1L, "Updated description");

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(productTypeRepository.save(productType)).thenReturn(productType);

        ProductTypeDTO output = service.update(1L, updateProductTypeDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("Updated description", output.description());
    }

    @Test
    void testUpdateWithProductTypeNotFound() {
        ProductTypeDTO productTypeDTO = productTypeMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, productTypeDTO);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNull() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdZero() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "Request object cannot be null";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNegative() {
        Exception output = assertThrows(RequestObjectIsNullException.class, () -> {
            service.delete(-10L);
        });
        String expectedMessage = "Request object cannot be null";
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
        ProductType product = productTypeMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.countProductsByIdProductType(1L)).thenReturn(0);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);
    }

    @Test
    void testDeleteWithAssociatedProducts() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.countProductsByIdProductType(1L)).thenReturn(11);

        Exception output = assertThrows(DeleteAssociationConflictException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "Resource cannot be removed due to being associated with other resources";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithProductTypeNotFound() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productRepository.countProductsByIdProductType(1L)).thenReturn(0);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The resource was not found";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

}
