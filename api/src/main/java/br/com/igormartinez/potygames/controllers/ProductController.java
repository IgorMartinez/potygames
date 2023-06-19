package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.dto.v1.ProductDTO;
import br.com.igormartinez.potygames.services.ProductService;

@RestController
@RequestMapping("api/product/v1")
public class ProductController {

    @Autowired
    private ProductService service;
    
    @GetMapping
    public List<ProductDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO product) {
        return service.create(product);
    }

    @PutMapping("/{id}")
    public ProductDTO update(
        @PathVariable(value = "id") Long id,
        @RequestBody ProductDTO product) {
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
