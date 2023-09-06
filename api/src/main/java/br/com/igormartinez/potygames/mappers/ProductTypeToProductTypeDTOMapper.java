package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.response.ProductTypeDTO;
import br.com.igormartinez.potygames.models.ProductType;

@Service
public class ProductTypeToProductTypeDTOMapper implements Function<ProductType, ProductTypeDTO> {

    @Override
    public ProductTypeDTO apply(ProductType type) {
        return new ProductTypeDTO(
            type.getId(), 
            type.getKeyword(),
            type.getDescription());
    }
    
}
