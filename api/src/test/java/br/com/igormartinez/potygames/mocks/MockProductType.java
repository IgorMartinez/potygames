package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.dto.v1.ProductTypeDTO;
import br.com.igormartinez.potygames.models.ProductType;

public class MockProductType {
    public ProductType mockEntity(int number) {
        ProductType type = new ProductType();
        type.setId(Long.valueOf(number));
        type.setDescription("Description " + number);
        type.setKeyword("keyword-" + number);
        return type;
    }

    public ProductType mockPreparedEntity(int number) {
        ProductType type = new ProductType();
        type.setDescription("Description " + number);
        type.setKeyword("keyword-" + number);
        return type;
    }

    public ProductType mockPreparedEntity(ProductTypeDTO typeDTO) {
        ProductType type = new ProductType();
        type.setDescription(typeDTO.description());
        type.setKeyword(typeDTO.keyword());
        return type;
    }

    public List<ProductType> mockEntityList(int number) {
        List<ProductType> list = new ArrayList<>();
        for (int i=1; i<=number; i++)
            list.add(mockEntity(i));
        return list;
    }

    public ProductTypeDTO mockDTO(int number) {
        return new ProductTypeDTO(Long.valueOf(number), "keyword-" + number, "Description " + 1);
    }
}
