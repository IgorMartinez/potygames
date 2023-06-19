package br.com.igormartinez.potygames.mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;

public class MockProduct {
    
    public Product mockEntity(Integer number) {
        ProductType type = new ProductType();
        type.setId(number.longValue());
        type.setDescription("Type of product " + number);

        Product product = new Product();
        product.setId(number.longValue());
        product.setType(type);
        product.setName("Product name " + number);
        product.setAltName("Product alt name " + number);
        product.setPrice(new BigDecimal(number + 0.99));
        product.setQuantity(number);

        return product;
    }

    public List<Product> mockEntityList(Integer number) {
        List<Product> list = new ArrayList<>();
        for (int i=1; i<=number; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }

    public ProductDTO mockDTO(Integer number) {
        return new ProductDTO(
            number.longValue(), 
            number.longValue(), 
            "Product name " + number, 
            "Product alt name " + number, 
            new BigDecimal(number + 0.99), 
            number);
    }

    public List<ProductDTO> mockDTOList(Integer number) {
        List<ProductDTO> list = new ArrayList<>();
        for (int i=1; i<=number; i++) {
            list.add(mockDTO(i));
        }
        return list;
    }

}
