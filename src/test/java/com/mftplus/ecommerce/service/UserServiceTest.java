package com.mftplus.ecommerce.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.mftplus.ecommerce.api.dto.LoginBody;
import com.mftplus.ecommerce.api.dto.RegistrationBody;
import com.mftplus.ecommerce.exception.EmailFailureException;
import com.mftplus.ecommerce.exception.UserAlreadyExistsException;
import com.mftplus.ecommerce.exception.UserNotVerifiedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("lastName");
        body.setPassword("MySecretPassword123");

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.save(body), " username should already be in use.");

        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.save(body), " email should already be in use.");

        body.setEmail("UserServiceTest$testRegisterUser@junit.com");

        Assertions.assertDoesNotThrow(
                () -> userService.save(body), " user should register successfully");

        Assertions.assertEquals(body.getEmail(),
                greenMailExtension.getReceivedMessages()[0]
                        .getRecipients(Message.RecipientType.TO)[0].toString());
    }


    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();

        body.setUsername("UserA-NotExists");
        body.setPassword("PasswordA123");

        Assertions.assertNull(userService.loginUser(body), " The user should not exist.");

        body.setUsername("UserA");
        body.setPassword("BadPassword123");

        Assertions.assertNull(userService.loginUser(body), " The password should be incorrect.");

        body.setPassword("PasswordA123");

        Assertions.assertNotNull(userService.loginUser(body), " The user should login successfully.");

        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
           userService.loginUser(body);
           Assertions.assertTrue(false, " User should not have email verified.");

        }catch (UserNotVerifiedException exception){
            Assertions.assertTrue(exception.isNewEmailSent(), " Email verification should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }

        try {
            userService.loginUser(body);
            Assertions.assertTrue(false, " User should not have email verified.");

        }catch (UserNotVerifiedException exception){
            Assertions.assertFalse(exception.isNewEmailSent(), " Email verification should be resent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }
}
