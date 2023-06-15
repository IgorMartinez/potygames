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

import br.com.igormartinez.potygames.data.dto.v1.UserAddressDTO;
import br.com.igormartinez.potygames.services.UserAddressService;

@RestController
@RequestMapping("api/user/v1/{user-id}/address")
public class UserAddressController {

    @Autowired
    UserAddressService service;
    
    @GetMapping
    public List<UserAddressDTO> findAllAddressesByIdUser(@PathVariable(value = "user-id") Long idUser){
        return service.findAllByIdUser(idUser);
    }

    @GetMapping("/{address-id}")
    public UserAddressDTO findAddressById(
        @PathVariable(value = "user-id") Long idUser,  
        @PathVariable(value = "address-id") Long idAddress){
        return service.findById(idUser, idAddress);
    }

    @PostMapping
    public UserAddressDTO createAddress(
        @PathVariable(value = "user-id") Long idUser,
        @RequestBody UserAddressDTO userAddressDTO) {
        return service.create(idUser, userAddressDTO);
    }

    @PutMapping("/{address-id}")
    public UserAddressDTO updateAddress(
        @PathVariable(value = "user-id") Long idUser,
        @PathVariable(value = "address-id") Long idAddress,
        @RequestBody UserAddressDTO userAddressDTO) {
        return service.update(idUser, idAddress, userAddressDTO);
    }

    @DeleteMapping("/{address-id}")
    public ResponseEntity<?> deleteAddress(
        @PathVariable(value = "user-id") Long idUser,  
        @PathVariable(value = "address-id") Long idAddress) {
        service.delete(idUser, idAddress);
        return ResponseEntity.noContent().build();
    }
}
