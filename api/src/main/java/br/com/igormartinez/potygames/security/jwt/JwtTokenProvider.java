package br.com.igormartinez.potygames.security.jwt;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.igormartinez.potygames.data.security.v1.Token;
import br.com.igormartinez.potygames.exceptions.ErrorCreationTokenException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validInMilliseconds = 3600000L; // 1h

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    /**
     * Receive a username and roles and return a valid token. 
     * The user has already been authenticated.
     * @param username
     * @param roles
     * @throws ErrorCreationTokenException
     * @return Token
     */
    public Token createAccessToken(String username, List<String> roles) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validity = ZonedDateTime.now().plus(validInMilliseconds, ChronoUnit.MILLIS);

        try {
            String accessToken = getAccessToken(username, roles, now, validity);
            String refreshToken = getRefreshToken(username, roles, now);
            
            return new Token(username, true, now, validity, accessToken, refreshToken);
        
        } catch (Exception ex) {
            throw new ErrorCreationTokenException();
        }
    }

    public Token refreshToken(String refreshToken) {
        if (refreshToken.contains("Bearer ")) refreshToken = refreshToken.substring("Bearer ".length());

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        return createAccessToken(username, roles);
    }

    private String getAccessToken(String username, List<String> roles, ZonedDateTime now, ZonedDateTime validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(Date.from(now.toInstant()))
            .withExpiresAt(Date.from(validity.toInstant()))
            .withSubject(username)
            .withIssuer(issuerUrl)
            .sign(algorithm)
            .strip();
    }

    private String getRefreshToken(String username, List<String> roles, ZonedDateTime now) {
        ZonedDateTime validityRefreshToken = now.plus(validInMilliseconds * 3, ChronoUnit.MILLIS);
        
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(Date.from(now.toInstant()))
            .withExpiresAt(Date.from(validityRefreshToken.toInstant()))
            .withSubject(username)
            .sign(algorithm)
            .strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()); 
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        
        return decodedJWT;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = decodedToken(token);
            if (decodedJWT.getExpiresAt().before(new Date())) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
