package oris.travelcommunity;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Тесты TripProposalController")
@RequiredArgsConstructor
public class TripProposalControlerTest {

    private final MockMvc mockMvc;

    @Test
    @DisplayName("GET /proposals/create без авторизации — редирект на логин")
    public void testCreatePageWithoutAuth() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /proposals/create с ролью ORGANIZER — возвращает форму")
    @WithMockUser(username = "organizer@test.com", roles = "ORGANIZER")
    public void testCreatePageWithOrganizerRole() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("proposals/create"))
                .andExpect(model().attributeExists("proposalRequest", "categories"));
    }

    @Test
    @DisplayName("GET /proposals/create с ролью TRAVELER — 403 Forbidden")
    @WithMockUser(username = "traveler@test.com", roles = "TRAVELER")
    public void testCreatePageWithTravelerRole() throws Exception {
        mockMvc.perform(get("/proposals/create"))
                .andExpect(status().isForbidden());
    }
}
