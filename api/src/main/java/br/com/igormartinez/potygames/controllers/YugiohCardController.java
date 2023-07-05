package br.com.igormartinez.potygames.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.potygames.data.dto.v1.YugiohCardDTO;
import br.com.igormartinez.potygames.enums.YugiohCardAttribute;
import br.com.igormartinez.potygames.models.YugiohCardCategory;
import br.com.igormartinez.potygames.models.YugiohCardType;
import br.com.igormartinez.potygames.services.YugiohCardService;

@RestController
@RequestMapping("/api/v1/product/yugioh-card")
public class YugiohCardController {

    @Autowired
    YugiohCardService service;
    
    @GetMapping
    public Page<YugiohCardDTO> findAll(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));
        return service.findAll(pageable);
    }

    @GetMapping("/{yugioh-card-id}")
    public YugiohCardDTO findById(@PathVariable("yugioh-card-id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public YugiohCardDTO create(@RequestBody YugiohCardDTO cardDTO) {
        return service.create(cardDTO);
    }

    @PutMapping("/{yugioh-card-id}")
    public YugiohCardDTO update(
        @PathVariable("yugioh-card-id") Long id,
        @RequestBody YugiohCardDTO cardDTO) {
        return service.update(id, cardDTO);
    }

    @DeleteMapping("/{yugioh-card-id}")
    public ResponseEntity<?> delete(@PathVariable("yugioh-card-id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/attribute")
    public List<YugiohCardAttribute> findAllAttributes() {
        return service.findAllAttributes();
    }

    @GetMapping("/category")
    public List<YugiohCardCategory> findAllCategories() {
        return service.findAllCategories();
    }

    @GetMapping("/type")
    public List<YugiohCardType> findAllTypes() {
        return service.findAllTypes();
    }
}
