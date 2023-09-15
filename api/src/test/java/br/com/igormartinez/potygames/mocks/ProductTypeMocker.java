package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.request.ProductTypeCreateDTO;
import br.com.igormartinez.potygames.data.request.ProductTypeUpdateDTO;
import br.com.igormartinez.potygames.data.response.ProductTypeDTO;
import br.com.igormartinez.potygames.models.ProductType;

public class ProductTypeMocker {

    public static ProductType mockEntity(int number) {
        ProductType type = new ProductType();
        type.setId(Long.valueOf(number));
        type.setDescription("Description " + number);
        type.setKeyword("keyword-" + number);
        return type;
    }

    public static ProductType mockEntity(int number, ProductTypeCreateDTO typeDTO) {
        ProductType type = new ProductType();
        type.setId(Long.valueOf(number));
        type.setDescription(typeDTO.description());
        type.setKeyword(typeDTO.keyword());
        return type;
    }

    public static ProductType mockEntity(ProductTypeUpdateDTO typeDTO) {
        ProductType type = new ProductType();
        type.setId(typeDTO.id());
        type.setDescription(typeDTO.description());
        type.setKeyword(typeDTO.keyword());
        return type;
    }

    public static ProductType mockPreparedEntity(int number) {
        ProductType type = new ProductType();
        type.setDescription("Description " + number);
        type.setKeyword("keyword-" + number);
        return type;
    }

    public static ProductType mockPreparedEntity(ProductTypeDTO typeDTO) {
        ProductType type = new ProductType();
        type.setDescription(typeDTO.description());
        type.setKeyword(typeDTO.keyword());
        return type;
    }


    public static List<ProductType> mockEntityList(int number) {
        List<ProductType> list = new ArrayList<>();
        for (int i=1; i<=number; i++)
            list.add(mockEntity(i));
        return list;
    }

    public static ProductTypeDTO mockDTO(int number) {
        return new ProductTypeDTO(Long.valueOf(number), "keyword-" + number, "Description " + number);
    }

    public static ProductTypeUpdateDTO mockUpdateDTO(int number) {
        return new ProductTypeUpdateDTO(Long.valueOf(number), "keyword-" + number, "Description " + number);
    }
}
