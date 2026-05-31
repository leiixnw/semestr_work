package oris.travelcommunity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import oris.travelcommunity.models.User;
import oris.travelcommunity.services.UserService;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final UserService userService;

    @GetMapping("/room")
    public String chatRoom(@RequestParam("proposalId") Long proposalId,
                           @RequestParam("receiverId") Long receiverId,
                           @AuthenticationPrincipal UserDetails currentUser,
                           Model model) {
        User user = userService.getByEmail(currentUser.getUsername());
        model.addAttribute("proposalId", proposalId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("currentUserId", user.getId());
        return "chat/room";
    }
}
