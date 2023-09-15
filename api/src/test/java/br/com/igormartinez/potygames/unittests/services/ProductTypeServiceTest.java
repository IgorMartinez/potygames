package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import br.com.igormartinez.potygames.data.request.ProductTypeCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductTypeUpdateDTO;
import br.com.igormartinez.potygames.data.response.ProductTypeDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
import br.com.igormartinez.potygames.exceptions.ResourceNotFoundException;
import br.com.igormartinez.potygames.exceptions.UserUnauthorizedException;
import br.com.igormartinez.potygames.mappers.ProductTypeToProductTypeDTOMapper;
import br.com.igormartinez.potygames.mocks.ProductTypeMocker;
import br.com.igormartinez.potygames.models.ProductType;
import br.com.igormartinez.potygames.repositories.ProductRepository;
import br.com.igormartinez.potygames.repositories.ProductTypeRepository;
import br.com.igormartinez.potygames.security.SecurityContextManager;
import br.com.igormartinez.potygames.services.ProductTypeService;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ProductTypeServiceTest {

    private ProductTypeService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;
    
    @Mock
    private SecurityContextManager securityContextManager;

    @BeforeEach
    void setup() {
        service = new ProductTypeService(
            productTypeRepository, 
            productRepository, 
            new ProductTypeToProductTypeDTOMapper(), 
            securityContextManager);
    }

    @Test
    void testFindAllWithProductTypes() {
        List<ProductType> list = ProductTypeMocker.mockEntityList(10);

        when(productTypeRepository.findAll()).thenReturn(list);

        List<ProductTypeDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(10, output.size());

        ProductTypeDTO outputPosition0 = output.get(0);
        assertEquals(1L, outputPosition0.id());
        assertEquals("keyword-1", outputPosition0.keyword());
        assertEquals("Description 1", outputPosition0.description());

        ProductTypeDTO outputPosition4 = output.get(4);
        assertEquals(5L, outputPosition4.id());
        assertEquals("keyword-5", outputPosition4.keyword());
        assertEquals("Description 5", outputPosition4.description());

        ProductTypeDTO outputPosition9 = output.get(9);
        assertEquals(10L, outputPosition9.id());
        assertEquals("keyword-10", outputPosition9.keyword());
        assertEquals("Description 10", outputPosition9.description());
    }

    @Test
    void testFindAllWithoutProductTypes() {
        List<ProductType> list = ProductTypeMocker.mockEntityList(0);

        when(productTypeRepository.findAll()).thenReturn(list);

        List<ProductTypeDTO> output = service.findAll();
        assertNotNull(output);
        assertEquals(0, output.size());
    }

    @Test
    void testFindByIdWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(null);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(0L);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.findById(-10L);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testFindByIdWithProductFound() {
        ProductType productType = ProductTypeMocker.mockEntity(1);

        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        ProductTypeDTO output = service.findById(1L);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("keyword-1", output.keyword());
        assertEquals("Description 1", output.description());
    }

    @Test
    void testFindByIdWithProductNotFound() {
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("keyword","Some description");

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(productTypeDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        ProductTypeCreateDTO productTypeDTO 
            = new ProductTypeCreateDTO("keyword","Some description");
        ProductType productType = ProductTypeMocker.mockEntity(1, productTypeDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);
        
        // Check the code after the save
        ProductTypeDTO output = service.create(productTypeDTO);
        assertEquals(1L, output.id());
        assertEquals("keyword", output.keyword());
        assertEquals("Some description", output.description());

        // Check the arguments before save
        ArgumentCaptor<ProductType> argumentCaptor = ArgumentCaptor.forClass(ProductType.class);
        verify(productTypeRepository).save(argumentCaptor.capture());
        ProductType capturedObject = argumentCaptor.getValue();
        assertNull(capturedObject.getId());
        assertEquals("keyword", capturedObject.getKeyword());
        assertEquals("Some description", capturedObject.getDescription());
    }

    @Test
    void testUpdateWithParamIdNull() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, typeDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, typeDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-13L, typeDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithMismatchParamIdAndDTOId() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(55L, typeDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-type-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithoutPermission() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.update(1L, typeDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithPermission() {
        ProductTypeUpdateDTO typeDTO 
            = new ProductTypeUpdateDTO(1L, "keyword-updated", "Some description updated");
        ProductType productType = ProductTypeMocker.mockEntity(1);
        ProductType preparedProductType = ProductTypeMocker.mockEntity(typeDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(productTypeRepository.save(productType)).thenReturn(preparedProductType);
        
        // Check code after save
        ProductTypeDTO output = service.update(1L, typeDTO);
        assertEquals(1L, output.id());
        assertEquals("keyword-updated", output.keyword());
        assertEquals("Some description updated", output.description());

        // Check arguments before save
        ArgumentCaptor<ProductType> argumentCaptor = ArgumentCaptor.forClass(ProductType.class);
        verify(productTypeRepository).save(argumentCaptor.capture());
        ProductType capturedObject = argumentCaptor.getValue();
        assertEquals(1L, capturedObject.getId());
        assertEquals("keyword-updated", capturedObject.getKeyword());
        assertEquals("Some description updated", capturedObject.getDescription());
    }

    @Test
    void testUpdateWithProductTypeNotFound() {
        ProductTypeUpdateDTO typeDTO = ProductTypeMocker.mockUpdateDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, typeDTO);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(null);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdZero() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(0L);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithParamIdNegative() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.delete(-11L);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
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
    void testDeleteWithPermission() {
        ProductType product = ProductTypeMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.countProductsByIdProductType(1L)).thenReturn(0);

        service.delete(1L);
    }

    @Test
    void testDeleteWithProductTypeNotFound() {
        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The product type was not found with the given ID.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithAssociatedProducts() {
        ProductType productType = ProductTypeMocker.mockEntity(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(productRepository.countProductsByIdProductType(1L)).thenReturn(11);

        Exception output = assertThrows(DeleteAssociationConflictException.class, () -> {
            service.delete(1L);
        });
        String expectedMessage = "The product type cannot be removed because it is associated with products.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }
}
