package oris.travelcommunity.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import oris.travelcommunity.dto.SignUpForm;
import oris.travelcommunity.models.User;

public interface UserService extends UserDetailsService {
    User register(SignUpForm form);
    User getById(Long id);
    User getByEmail(String email);
}