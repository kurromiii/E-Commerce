package com.mftplus.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.mftplus.ecommerce.model.entity.User;
import com.mftplus.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Test
    public void testVerificationTokenNotUsableForLogin(){
        User user = userRepository.findByUsernameIgnoreCaseAndDeletedFalse("UserA").get();
        String token = jwtService.generateVerificationJwt(user);

        Assertions.assertNull(jwtService.getUsername(token),
                " verification token should not contain token.");

    }

    @Test
    public void testAuthTokenReturnUsername(){
        User user = userRepository.findByUsernameIgnoreCaseAndDeletedFalse("UserA").get();
        String token = jwtService.generateJWT(user);

        Assertions.assertEquals(user.getUsername(),
                jwtService.getUsername(token),
                        " token for auth should contain users username.");

    }

    @Test
    public void testLoginJwtNotGeneratedByUs(){
        String token =
        JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
                "NotTheRealSecret"));

        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getUsername(token));

    }

    @Test
    public void testLoginJWTCorrectlySignedNoIssuer(){
        String token =
                JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
                        algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getUsername(token));
    }

    @Test
    public void testResetPasswordJwtNotGeneratedByUs(){
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
                        "NotTheRealSecret"));

        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getResetPasswordEmail(token));

    }

    @Test
    public void testResetPasswordJWTCorrectlySignedNoIssuer(){
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
                        algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    public void testPasswordResetToken() {
        User user = userRepository.findByUsernameIgnoreCaseAndDeletedFalse("UserA").get();
        String token = jwtService.generatePasswordResetJwt(user);
        Assertions.assertEquals(user.getEmail()
                , jwtService.getResetPasswordEmail(token)
                ,"Email should match inside jwt." );
    }
}
