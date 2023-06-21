package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.mappers.ProductDTOMapper;
import br.com.igormartinez.potygames.mappers.ProductTypeDTOMapper;
import br.com.igormartinez.potygames.mocks.MockProduct;
import br.com.igormartinez.potygames.mocks.MockProductType;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;

public class ProductMappersTest {

    private MockProduct productMocker;
    private ProductDTOMapper productDTOMapper;

    private MockProductType productTypeMocker;
    private ProductTypeDTOMapper productTypeDTOMapper;

    @BeforeEach
    public void setup() {
        productMocker = new MockProduct();
        productDTOMapper = new ProductDTOMapper();

        productTypeMocker = new MockProductType();
        productTypeDTOMapper = new ProductTypeDTOMapper();
    }

    @Test
    public void testProductDTOMapper() {
        Product product = productMocker.mockEntity(1);
        
        ProductDTO productDTO = productDTOMapper.apply(product);
        
        assertNotNull(productDTO);
        assertEquals(1L, productDTO.id());
        assertEquals(1L, productDTO.idProductType());
        assertEquals("Product name 1", productDTO.name());
        assertEquals("Product alt name 1", productDTO.altName());
        assertEquals(new BigDecimal("1.99"), productDTO.price());
        assertEquals(1, productDTO.quantity());
    }

    @Test
    public void testProductTypeDTOMapper() {
        ProductType productType = productTypeMocker.mockEntity(1);

        ProductTypeDTO productTypeDTO = productTypeDTOMapper.apply(productType);

        assertNotNull(productTypeDTO);
        assertEquals(1L, productTypeDTO.id());
        assertEquals("Description 1", productTypeDTO.description());
    }
}
