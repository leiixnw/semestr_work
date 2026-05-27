package oris.travelcommunity.test;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import oris.travelcommunity.dto.SignUpForm;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.User;
import oris.travelcommunity.models.enums.UserRole;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.services.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@AllArgsConstructor
@DisplayName("Тесты UserService")
public class UserServiceTest {

    private UserService userService;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void testRegisterUserSuccess() {
        SignUpForm form = new SignUpForm();
        form.setEmail("test@example.com");
        form.setUsername("testuser");
        form.setPassword("password123");
        form.setFullName("Test User");
        form.setRole(UserRole.ROLE_TRAVELER);

        User registeredUser = userService.register(form);

        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("Test User", registeredUser.getFullName());
        assertEquals(UserRole.ROLE_TRAVELER, registeredUser.getRole());
        assertTrue(passwordEncoder.matches("password123", registeredUser.getPassword()));
    }

    @Test
    @DisplayName("Регистрация с существующим email выбрасывает исключение")
    public void testRegisterUserWithExistingEmail() {
        SignUpForm form1 = new SignUpForm();
        form1.setEmail("duplicate@example.com");
        form1.setUsername("user1");
        form1.setPassword("password123");
        form1.setFullName("User One");
        form1.setRole(UserRole.ROLE_TRAVELER);

        SignUpForm form2 = new SignUpForm();
        form2.setEmail("duplicate@example.com");
        form2.setUsername("user2");
        form2.setPassword("password456");
        form2.setFullName("User Two");
        form2.setRole(UserRole.ROLE_TRAVELER);

        userService.register(form1);
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(form2);
        });
    }

    @Test
    @DisplayName("Регистрация с существующим username выбрасывает исключение")
    public void testRegisterUserWithExistingUsername() {
        SignUpForm form1 = new SignUpForm();
        form1.setEmail("user1@example.com");
        form1.setUsername("duplicate");
        form1.setPassword("password123");
        form1.setFullName("User One");
        form1.setRole(UserRole.ROLE_TRAVELER);

        SignUpForm form2 = new SignUpForm();
        form2.setEmail("user2@example.com");
        form2.setUsername("duplicate");
        form2.setPassword("password456");
        form2.setFullName("User Two");
        form2.setRole(UserRole.ROLE_TRAVELER);

        userService.register(form1);
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(form2);
        });
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    public void testGetUserById() {
        SignUpForm form = new SignUpForm();
        form.setEmail("getbyid@example.com");
        form.setUsername("getbyiduser");
        form.setPassword("password123");
        form.setFullName("Get By ID User");
        form.setRole(UserRole.ROLE_TRAVELER);
        User registeredUser = userService.register(form);

        User foundUser = userService.getById(registeredUser.getId());

        assertNotNull(foundUser);
        assertEquals(registeredUser.getId(), foundUser.getId());
        assertEquals("getbyid@example.com", foundUser.getEmail());
    }

    @Test
    @DisplayName("Получение несуществующего пользователя по ID выбрасывает исключение")
    public void testGetUserByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            userService.getById(99999L);
        });
    }

    @Test
    @DisplayName("Получение пользователя по email")
    public void testGetUserByEmail() {
        SignUpForm form = new SignUpForm();
        form.setEmail("byemail@example.com");
        form.setUsername("byemailuser");
        form.setPassword("password123");
        form.setFullName("By Email User");
        form.setRole(UserRole.ROLE_ORGANIZER);
        userService.register(form);

        User foundUser = userService.getByEmail("byemail@example.com");

        assertNotNull(foundUser);
        assertEquals("byemail@example.com", foundUser.getEmail());
        assertEquals(UserRole.ROLE_ORGANIZER, foundUser.getRole());
    }

    @Test
    @DisplayName("Получение несуществующего пользователя по email выбрасывает исключение")
    public void testGetUserByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> {
            userService.getByEmail("nonexistent@example.com");
        });
    }

    @Test
    @DisplayName("Пароль корректно хешируется при регистрации")
    public void testPasswordEncryption() {
        SignUpForm form = new SignUpForm();
        form.setEmail("encrypt@example.com");
        form.setUsername("encryptuser");
        form.setPassword("mySecurePassword");
        form.setFullName("Encrypt Test User");
        form.setRole(UserRole.ROLE_TRAVELER);

        User registeredUser = userService.register(form);

        assertNotEquals("mySecurePassword", registeredUser.getPassword());
        assertTrue(passwordEncoder.matches("mySecurePassword", registeredUser.getPassword()));
    }
}