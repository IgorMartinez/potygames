package br.com.igormartinez.potygames.mocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.models.Permission;
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

        List<Permission> permissionList = new ArrayList<>();
        Permission permission = new Permission();
        permission.setDescription(
                (number%2==0) 
                ? PermissionType.ADMIN.getValue() 
                : PermissionType.CUSTOMER.getValue());
        permissionList.add(permission);
        user.setPermissions(permissionList);
        return user;
    }

    public User mockUserSignup(Integer number) {
        User user = new User();
        user.setId(number.longValue());
        user.setName("User name " + number);
        user.setEmail("user_mail" + number + "@test.com");
        user.setPassword("password" + number);
        user.setAccountNonExpired(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setCredentialsNonExpired(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);

        List<Permission> permissionList = new ArrayList<>();
        Permission permission = new Permission();
        permission.setDescription(PermissionType.CUSTOMER.getValue());
        permissionList.add(permission);
        user.setPermissions(permissionList);
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
        List<String> permissionList = new ArrayList<>();
        permissionList.add((number%2==0) 
                ? PermissionType.ADMIN.getValue() 
                : PermissionType.CUSTOMER.getValue());

        UserDTO userDTO = new UserDTO(
            number.longValue(),
            "User name " + number,
            "user_mail" + number + "@test.com",
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // accountNonExpired
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // accountNonLocked
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // credentialsNonExpired
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // enabled
            permissionList
        );
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
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
            "User name " + number,
            "user_mail" + number + "@test.com",
            "password" + number
        );
        return userRegistrationDTO;
    }

    public List<UserRegistrationDTO> mockUserRegistrationDTOList(Integer number) {
        List<UserRegistrationDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserRegistrationDTO(i+1));
        }
        return userList;
    }

    public boolean isEquals(User user, UserDTO userDTO) {
        List<String> listUser = user.getPermissions()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return user.getId() == userDTO.id()
            && user.getName().equals(userDTO.name())
            && user.getEmail().equals(userDTO.email())
            && user.getAccountNonExpired() == userDTO.accountNonExpired()
            && user.getAccountNonLocked() == userDTO.accountNonLocked()
            && user.getCredentialsNonExpired() == userDTO.credentialsNonExpired()
            && user.getEnabled() == userDTO.enabled()
            && listUser.size() == userDTO.permissions().size()
            && listUser.containsAll(userDTO.permissions());
    }
}
