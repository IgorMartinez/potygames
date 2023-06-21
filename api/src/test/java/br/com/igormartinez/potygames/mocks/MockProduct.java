package br.com.igormartinez.potygames.mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.models.Product;
import br.com.igormartinez.potygames.models.ProductType;

public class MockProduct {
    
    public Product mockEntity(int number) {
        ProductType type = new ProductType();
        type.setId(Long.valueOf(number));
        type.setDescription("Type of product " + number);

        Product product = new Product();
        product.setId(Long.valueOf(number));
        product.setType(type);
        product.setName("Product name " + number);
        product.setAltName("Product alt name " + number);
        product.setPrice(new BigDecimal(number + ".99"));
        product.setQuantity(number);

        return product;
    }

    public List<Product> mockEntityList(int number) {
        List<Product> list = new ArrayList<>();
        for (int i=1; i<=number; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }

    public List<Product> mockEntityList(int startNumber, int endNumber) {
        List<Product> list = new ArrayList<>();
        for (int i=startNumber; i<=endNumber; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }

    public ProductDTO mockDTO(int number) {
        return new ProductDTO(
            Long.valueOf(number), 
            Long.valueOf(number), 
            "Product name " + number, 
            "Product alt name " + number, 
            new BigDecimal(number + ".99"), 
            number);
    }

    public List<ProductDTO> mockDTOList(int number) {
        List<ProductDTO> list = new ArrayList<>();
        for (int i=1; i<=number; i++) {
            list.add(mockDTO(i));
        }
        return list;
    }

    public Page<Product> mockProductPage(int totalElements, Pageable pageable) {
        int sizePage = pageable.getPageSize();
        int numberPage = pageable.getPageNumber();

        int startNumber = 1 + (sizePage * numberPage);
        int endNumber = (numberPage + 1) * sizePage;
        List<Product> mockList = mockEntityList(startNumber, Math.min(totalElements, endNumber));
        
        Page<Product> page = new PageImpl<>(mockList, pageable, totalElements);
        return page;
    }
}
