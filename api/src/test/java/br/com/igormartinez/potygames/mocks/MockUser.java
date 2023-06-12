package br.com.igormartinez.potygames.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserPersonalInformationDTO;
import br.com.igormartinez.potygames.data.dto.v1.UserRegistrationDTO;
import br.com.igormartinez.potygames.enums.PermissionType;
import br.com.igormartinez.potygames.models.Permission;
import br.com.igormartinez.potygames.models.User;

public class MockUser {
    
    public User mockUser(Integer number) {
        User user = new User();
        user.setId(number.longValue());
        user.setEmail("user_mail" + number + "@test.com");
        user.setName("User name " + number);
        user.setPassword("password" + number);
        user.setBirthDate(LocalDate.of((number%100)+1950, (number%12)+1, (number%28)+1));
        user.setDocumentNumber("000.000.000-"+String.format("%02d", number%100));
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
        user.setEmail("user_mail" + number + "@test.com");
        user.setName("User name " + number);
        user.setPassword("password" + number);
        user.setBirthDate(LocalDate.of((number%100)+1950, (number%12)+1, (number%28)+1));
        user.setDocumentNumber("000.000.000-"+String.format("%02d", number%100));
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

        return new UserDTO(
            number.longValue(),
            "user_mail" + number + "@test.com",
            "User name " + number,
            LocalDate.of((number%100)+1950, (number%12)+1, (number%28)+1),
            "000.000.000-"+String.format("%02d", number%100),
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // accountNonExpired
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // accountNonLocked
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // credentialsNonExpired
            (number%2==0) ? Boolean.TRUE : Boolean.FALSE, // enabled
            permissionList
        );
    }

    public List<UserDTO> mockUserDTOList(Integer number) {
        List<UserDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserDTO(i+1));
        }
        return userList;
    }

    public UserRegistrationDTO mockUserRegistrationDTO(Integer number) {
        return new UserRegistrationDTO(
            "user_mail" + number + "@test.com",
            "User name " + number,
            "password" + number,
            LocalDate.of((number%100)+1950, (number%12)+1, (number%28)+1),
            "000.000.000-"+String.format("%02d", number%100)
        );
    }

    public List<UserRegistrationDTO> mockUserRegistrationDTOList(Integer number) {
        List<UserRegistrationDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserRegistrationDTO(i+1));
        }
        return userList;
    }

    public UserPersonalInformationDTO mockUserPersonalInformationDTO(Integer number) {
        return new UserPersonalInformationDTO(
            number.longValue(), 
            "User name " + number,
            LocalDate.of((number%100)+1950, (number%12)+1, (number%28)+1),
            "000.000.000-"+String.format("%02d", number%100)
        );
    }

    public List<UserPersonalInformationDTO> mockUserPersonalInformationDTOList(Integer number) {
        List<UserPersonalInformationDTO> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            userList.add(mockUserPersonalInformationDTO(i+1));
        }
        return userList;
    }
}
