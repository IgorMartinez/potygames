package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.models.Product;

public class MockProduct {

    private MockProductType mockProductType;

    public MockProduct(MockProductType mockProductType) {
        this.mockProductType = mockProductType;
    }
    
    public Product mockEntity(int number) {
        Product product = new Product();
        product.setId(Long.valueOf(number));
        product.setType(mockProductType.mockEntity(number));
        product.setName("Product name " + number);
        product.setDescription("Product description " + number);

        return product;
    }

    public Product mockEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.id());
        product.setType(mockProductType.mockEntity(productDTO.idProductType().intValue()));
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());

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
            "Product description " + number);
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
