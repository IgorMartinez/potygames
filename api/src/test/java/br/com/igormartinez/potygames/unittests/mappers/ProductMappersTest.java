package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.response.ProductDTO;
import br.com.igormartinez.potygames.data.response.ProductTypeDTO;
import br.com.igormartinez.potygames.mappers.ProductToProductDTOMapper;
import br.com.igormartinez.potygames.mappers.ProductTypeToProductTypeDTOMapper;
import br.com.igormartinez.potygames.mocks.ProductMocker;
import br.com.igormartinez.potygames.mocks.ProductTypeMocker;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;

public class ProductMappersTest {

    private ProductToProductDTOMapper productDTOMapper;
    private ProductTypeToProductTypeDTOMapper productTypeDTOMapper;

    @BeforeEach
    public void setup() {
        productTypeDTOMapper = new ProductTypeToProductTypeDTOMapper();
        productDTOMapper = new ProductToProductDTOMapper();
    }

    @Test
    public void testProductDTOMapper() {
        Product product = ProductMocker.mockEntity(1);
        
        ProductDTO productDTO = productDTOMapper.apply(product);
        
        assertNotNull(productDTO);
        assertEquals(1L, productDTO.id());
        assertEquals(1L, productDTO.idProductType());
        assertEquals("Product name 1", productDTO.name());
        assertEquals("Product description 1", productDTO.description());
    }

    @Test
    public void testProductTypeDTOMapper() {
        ProductType productType = ProductTypeMocker.mockEntity(1);

        ProductTypeDTO productTypeDTO = productTypeDTOMapper.apply(productType);

        assertNotNull(productTypeDTO);
        assertEquals(1L, productTypeDTO.id());
        assertEquals("keyword-1", productTypeDTO.keyword());
        assertEquals("Description 1", productTypeDTO.description());
    }
}
