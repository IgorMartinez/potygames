package br.com.igormartinez.potygames.mocks;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import br.com.igormartinez.potygames.data.security.v1.Token;

public class MockToken {

    public Token mockToken(String username) {
        return new Token(
            username, 
            Boolean.TRUE, 
            ZonedDateTime.of(
                2023, 
                06, 
                13, 
                13, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault()), 
            ZonedDateTime.of(
                2023, 
                06, 
                13, 
                14, 
                27, 
                0, 
                0, 
                ZoneId.systemDefault()),
            "mockedAccessToken", 
            "mockedRefreshToken");
    }
}
