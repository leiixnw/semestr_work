package oris.travelcommunity.dto;

import lombok.Data;
import oris.travelcommunity.models.enums.UserRole;

@Data
public class SignUpForm {
    private String email;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;
}