package br.com.igormartinez.potygames.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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

import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.exceptions.DeleteAssociationConflictException;
import br.com.igormartinez.potygames.exceptions.RequestValidationException;
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
    void testPrepareEntityWithProductTypeDTONull() {
        Exception output = assertThrows(IllegalArgumentException.class, () -> {
            service.prepareEntity(null);
        });
        String expectedMessage = "The ProductTypeDTO argument must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithKeywordNull() {
        ProductTypeDTO typeDTO = new ProductTypeDTO(null, null, null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(typeDTO);
        });
        String expectedMessage = "The keyword of product type must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithKeywordBlank() {
        ProductTypeDTO typeDTO = new ProductTypeDTO(null, " ", null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(typeDTO);
        });
        String expectedMessage = "The keyword of product type must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithDescriptionNull() {
        ProductTypeDTO typeDTO = new ProductTypeDTO(null, "loremipsum", null);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(typeDTO);
        });
        String expectedMessage = "The description of product type must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testPrepareEntityWithDescriptionBlank() {
        ProductTypeDTO typeDTO = new ProductTypeDTO(null, "loremipsum", "");

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.prepareEntity(typeDTO);
        });
        String expectedMessage = "The description of product type must not be blank.";
        assertTrue(output.getMessage().contains(expectedMessage));
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
        List<ProductType> list = productTypeMocker.mockEntityList(0);

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
        ProductType productType = productTypeMocker.mockEntity(1);

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
    void testCreateWithParamDTONull() {
        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "The request body must not be null.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithoutPermission() {
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null,"keyword","Some description");

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.FALSE);

        Exception output = assertThrows(UserUnauthorizedException.class, () -> {
            service.create(productTypeDTO);
        });
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreateWithPermission() {
        ProductTypeService spyService = Mockito.spy(service);

        ProductType productType = productTypeMocker.mockEntity(1);
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(null,"keyword-1","Description 1");
        ProductType preparedProductType = productTypeMocker.mockPreparedEntity(productTypeDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        doReturn(preparedProductType).when(spyService).prepareEntity(ArgumentMatchers.any(ProductTypeDTO.class));
        when(productTypeRepository.save(preparedProductType)).thenReturn(productType);
        
        ProductTypeDTO output = spyService.create(productTypeDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("keyword-1", output.keyword());
        assertEquals("Description 1", output.description());
    }

    @Test
    void testUpdateWithParamIdNull() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(null, productDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdZero() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(0L, productDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamIdNegative() {
        ProductTypeDTO productDTO = productTypeMocker.mockDTO(1);

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(-13L, productDTO);
        });
        String expectedMessage = "The product-type-id must be a positive integer value.";
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
        ProductTypeDTO productDTO = new ProductTypeDTO(null,"keyword","Some description");

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-type-id parameter.";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithParamDTOIdMismatchParamId() {
        ProductTypeDTO productDTO = new ProductTypeDTO(2L,"keyword","Some description");

        Exception output = assertThrows(RequestValidationException.class, () -> {
            service.update(1L, productDTO);
        });
        String expectedMessage = "The ID in the request body must match the value of the product-type-id parameter.";
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
        ProductTypeService spyService = Mockito.spy(service);

        ProductType productType = productTypeMocker.mockEntity(1);
        ProductTypeDTO productTypeDTO = new ProductTypeDTO(1L,"keyword-updated","Some description updated");
        ProductType preparedProductType = productTypeMocker.mockPreparedEntity(productTypeDTO);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));
        doReturn(preparedProductType)
            .when(spyService).prepareEntity(ArgumentMatchers.any(ProductTypeDTO.class));
        when(productTypeRepository.save(preparedProductType)).thenReturn(preparedProductType);
        
        ProductTypeDTO output = spyService.update(1L, productTypeDTO);
        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("keyword-updated", output.keyword());
        assertEquals("Some description updated", output.description());
    }

    @Test
    void testUpdateWithProductTypeNotFound() {
        ProductTypeDTO productTypeDTO = productTypeMocker.mockDTO(1);

        when(securityContextManager.checkAdmin()).thenReturn(Boolean.TRUE);
        when(productTypeRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        Exception output = assertThrows(ResourceNotFoundException.class, () -> {
            service.update(1L, productTypeDTO);
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
        String expectedMessage = "The user is not authorized to access this resource";
        assertTrue(output.getMessage().contains(expectedMessage));
    }

    @Test
    void testDeleteWithPermission() {
        ProductType product = productTypeMocker.mockEntity(1);

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
        ProductType productType = productTypeMocker.mockEntity(1);

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
