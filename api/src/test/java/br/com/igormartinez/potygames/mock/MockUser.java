package br.com.igormartinez.potygames.mock;

import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.models.User;

public class MockUser {
    
    public User mockUser(Integer number) {
        User user = new User();
        user.setId(number.longValue());
        user.setName("User name " + number);
        user.setEmail("user_mail" + number + "@test.com");
        user.setPassword("password" + number);
        user.setAccountNonExpired((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        user.setAccountNonLocked((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        user.setCredentialsNonExpired((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        user.setEnabled((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        user.setPermissions(new ArrayList<>());
        return user;
    }

    public List<User> mockUserList(Integer number) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUser(i+1));
        }
        return userList;
    }

    public UserDTO mockUserDTO(Integer number) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(number.longValue());
        userDTO.setName("User name " + number);
        userDTO.setEmail("user_mail" + number + "@test.com");
        userDTO.setPassword("password" + number);
        userDTO.setAccountNonExpired((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        userDTO.setAccountNonLocked((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        userDTO.setCredentialsNonExpired((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        userDTO.setEnabled((number%2==0) ? Boolean.TRUE : Boolean.FALSE);
        return userDTO;
    }

    public List<UserDTO> mockUserDTOList(Integer number) {
        List<UserDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserDTO(i+1));
        }
        return userList;
    }

    public UserRegistrationDTO mockUserRegistrationDTO(Integer number) {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setName("User name " + number);
        userRegistrationDTO.setEmail("user_mail" + number + "@test.com");
        userRegistrationDTO.setPassword("password" + number);
        return userRegistrationDTO;
    }

    public List<UserRegistrationDTO> mockUserRegistrationDTOList(Integer number) {
        List<UserRegistrationDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserRegistrationDTO(i+1));
        }
        return userList;
    }
}
