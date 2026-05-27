package oris.travelcommunity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Тесты валидации и безопасности")
public class SecurityValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Пустой email при регистрации — возвращает форму с ошибкой")
    public void testEmptyEmailValidation() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("fullName", "Test User")
                        .param("role", "ROLE_TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    @DisplayName("Короткий пароль при регистрации — возвращает форму с ошибкой")
    public void testShortPasswordValidation() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "123")
                        .param("fullName", "Test User")
                        .param("role", "ROLE_TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    @DisplayName("Доступ к защищённому ресурсу без авторизации — редирект")
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("TRAVELER не может создавать туры — 403")
    @WithMockUser(username = "traveler@test.com", roles = "TRAVELER")
    public void testRoleBasedAccess() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isForbidden());
    }
}
