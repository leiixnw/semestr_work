package oris.travelcommunity.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты AuthController")
@RequiredArgsConstructor
public class AuthControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

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
    @DisplayName("POST /signup с валидными данными успешна")
    public void testSignupSuccess() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "newuser@test.com")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("fullName", "New User")
                        .param("role", "TRAVELER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @DisplayName("POST /signup с дублирующимся email возвращает ошибку")
    public void testSignupDuplicateEmail() throws Exception {
        // Первая регистрация
        mockMvc.perform(post("/signup")
                .with(csrf())
                .param("email", "duplicate@test.com")
                .param("username", "user1")
                .param("password", "password123")
                .param("fullName", "User One")
                .param("role", "TRAVELER"));

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "duplicate@test.com")
                        .param("username", "user2")
                        .param("password", "password456")
                        .param("fullName", "User Two")
                        .param("role", "TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("POST /signup без CSRF токена выбрасывает 403 Forbidden")
    public void testSignupWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("fullName", "Test User")
                        .param("role", "TRAVELER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /login с правильными данными")
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/signup")
                .with(csrf())
                .param("email", "login@test.com")
                .param("username", "loginuser")
                .param("password", "testpass123")
                .param("fullName", "Login User")
                .param("role", "TRAVELER"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "login@test.com")
                        .param("password", "testpass123"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /login с неправильным паролем возвращает ошибку")
    public void testLoginWithWrongPassword() throws Exception {
        // Регистрируем пользователя
        mockMvc.perform(post("/signup")
                .with(csrf())
                .param("email", "wrongpass@test.com")
                .param("username", "wrongpassuser")
                .param("password", "correctpass")
                .param("fullName", "Wrong Pass User")
                .param("role", "TRAVELER"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("email", "wrongpass@test.com")
                        .param("password", "wrongpass"))
                .andExpect(status().is3xxRedirection());
    }
}

/**
 * Тесты для TripProposalController
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты TripProposalController")
public class TripProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /proposals/:id возвращает страницу с деталями тура")
    @WithMockUser(username = "user@test.com", roles = "TRAVELER")
    public void testGetProposalDetails() throws Exception {
        mockMvc.perform(get("/proposals/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("proposals/details"));
    }

    @Test
    @DisplayName("GET /proposals/:id несуществующего тура возвращает 404")
    @WithMockUser(username = "user@test.com", roles = "TRAVELER")
    public void testGetNonexistentProposal() throws Exception {
        mockMvc.perform(get("/proposals/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /proposals/create без авторизации редирект на логин")
    public void testCreateProposalPageWithoutAuth() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("GET /proposals/create с авторизацией возвращает форму")
    @WithMockUser(username = "organizer@test.com", roles = "ORGANIZER")
    public void testCreateProposalPageWithAuth() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("proposals/create"))
                .andExpect(model().attributeExists("proposalRequest", "categories"));
    }

    @Test
    @DisplayName("GET /proposals/create без ORGANIZER роли возвращает 403")
    @WithMockUser(username = "traveler@test.com", roles = "TRAVELER")
    public void testCreateProposalPageWithoutOrganizerRole() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isForbidden());
    }
}

/**
 * Тесты для REST API контроллеров
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты REST API контроллеров")
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/proposals/hot возвращает JSON список туров")
    public void testGetHotProposals() throws Exception {
        mockMvc.perform(get("/api/proposals/hot")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("POST /api/applications/apply требует авторизации")
    public void testApplyForTripWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/applications/apply")
                        .contentType("application/json")
                        .content("{\"proposalId\": 1, \"comment\": \"test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/applications/apply с авторизацией создает заявку")
    @WithMockUser(username = "traveler@test.com", roles = "TRAVELER")
    public void testApplyForTripWithAuth() throws Exception {
        mockMvc.perform(post("/api/applications/apply")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"proposalId\": 1, \"comment\": \"test comment\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/chat/history возвращает массив сообщений")
    @WithMockUser(username = "user@test.com", roles = "TRAVELER")
    public void testGetChatHistory() throws Exception {
        mockMvc.perform(get("/api/chat/history")
                        .param("proposalId", "1")
                        .param("receiverId", "2")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @DisplayName("POST /api/chat/send отправляет сообщение")
    @WithMockUser(username = "user@test.com", roles = "TRAVELER")
    public void testSendChatMessage() throws Exception {
        mockMvc.perform(post("/api/chat/send")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"proposalId\": 1, \"receiverId\": 2, \"messageText\": \"Hello!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /v3/api-docs возвращает OpenAPI спецификацию")
    public void testGetOpenAPISpec() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.openapi", equalTo("3.0.1")))
                .andExpect(jsonPath("$.info.title", containsString("Travel Community")));
    }

    @Test
    @DisplayName("GET /swagger-ui.html возвращает Swagger интерфейс")
    public void testGetSwaggerUI() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }
}

/**
 * Тесты для обработки исключений
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты обработки исключений")
public class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /nonexistent возвращает 404")
    public void testNotFoundException() throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST с неправильным JSON возвращает 400")
    public void testBadRequestException() throws Exception {
        mockMvc.perform(post("/api/applications/apply")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Без CSRF токена POST возвращает 403")
    @WithMockUser(username = "user@test.com")
    public void testCsrfTokenMissing() throws Exception {
        mockMvc.perform(post("/api/applications/apply")
                        .contentType("application/json")
                        .content("{\"proposalId\": 1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /error/404 показывает пользовательскую 404 страницу")
    public void testCustom404Page() throws Exception {
        // Запрашиваем несуществующий ресурс
        MvcResult result = mockMvc.perform(get("/nonexistent-page"))
                .andExpect(status().isNotFound())
                .andReturn();

        // Проверяем что возвращается HTML страница
        String content = result.getResponse().getContentAsString();
        // Проверяем что это не дефолтная Spring ошибка
        assert(!content.contains("Whitelabel Error Page"));
    }
}

/**
 * Тесты для валидации и безопасности
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Тесты валидации и безопасности")
public class SecurityValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Пустой email при регистрации возвращает ошибку")
    public void testEmptyEmailValidation() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("fullName", "Test User")
                        .param("role", "TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    @DisplayName("Короткий пароль при регистрации возвращает ошибку")
    public void testShortPasswordValidation() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "123")  // Очень короткий
                        .param("fullName", "Test User")
                        .param("role", "TRAVELER"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    @DisplayName("Доступ к защищенному ресурсу без авторизации")
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Низкоуровневый пользователь не может создавать туры")
    @WithMockUser(username = "traveler@test.com", roles = "TRAVELER")
    public void testRoleBasedAccessControl() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Пользователь может получить доступ к своим данным")
    @WithMockUser(username = "user@test.com", roles = "TRAVELER")
    public void testAuthorizedAccess() throws Exception {
        mockMvc.perform(get("/proposals/1"))
                .andExpect(status().isOk());
    }
}