package br.com.igormartinez.potygames.unittests.mappers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.igormartinez.potygames.data.dto.v1.UserDTO;
import br.com.igormartinez.potygames.mappers.UserDTOMapper;
import br.com.igormartinez.potygames.mocks.MockUser;
import br.com.igormartinez.potygames.models.User;

public class FunctionMapperTest {
    
    MockUser mockUser;

    UserDTOMapper userDTOMapper;

    @BeforeEach
    public void setup() {
        mockUser = new MockUser();
        userDTOMapper = new UserDTOMapper();
    }

    @Test
    public void testUserDTOMapper() {
        User user = mockUser.mockUser(1);

        UserDTO userDTO = userDTOMapper.apply(user);
        assertTrue(mockUser.isEquals(user, userDTO));
    }
}
