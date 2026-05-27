package oris.travelcommunity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Тесты AuthController")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /login возвращает страницу входа")
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /signup возвращает страницу регистрации")
    public void testGetSignupPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @Test
    @DisplayName("POST /signup с валидными данными — редирект на логин")
    public void testSignupSuccess() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "newuser@test.com")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("fullName", "New User")
                        .param("role", "ROLE_TRAVELER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @DisplayName("POST /signup с дублирующимся email — возвращает форму с ошибкой")
    public void testSignupDuplicateEmail() throws Exception {
        mockMvc.perform(post("/signup").with(csrf())
                .param("email", "dup@test.com").param("username", "user1")
                .param("password", "password123").param("fullName", "User One")
                .param("role", "ROLE_TRAVELER"));

        mockMvc.perform(post("/signup").with(csrf())
                        .param("email", "dup@test.com").param("username", "user2")
                        .param("password", "password456").param("fullName", "User Two")
                        .param("role", "ROLE_TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("POST /signup без CSRF токена — 403 Forbidden")
    public void testSignupWithoutCsrf() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("fullName", "Test User")
                        .param("role", "ROLE_TRAVELER"))
                .andExpect(status().isForbidden());
    }
}
