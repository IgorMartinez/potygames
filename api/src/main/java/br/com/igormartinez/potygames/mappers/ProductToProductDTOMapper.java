package br.com.igormartinez.potygames.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import br.com.igormartinez.potygames.data.response.ProductDTO;
import br.com.igormartinez.potygames.models.Product;

@Service
public class ProductToProductDTOMapper implements Function<Product, ProductDTO> {

    @Override
    public ProductDTO apply(Product product) {
        return new ProductDTO(
            product.getId(), 
            product.getType().getId(), 
            product.getName(), 
            product.getDescription());
    }
    
}
