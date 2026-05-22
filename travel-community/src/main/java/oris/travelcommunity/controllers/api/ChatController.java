package oris.travelcommunity.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import oris.travelcommunity.dto.request.ChatMessageRequest;
import oris.travelcommunity.models.ChatMessage;
import oris.travelcommunity.services.ChatMessageService;
import oris.travelcommunity.services.UserService;
import oris.travelcommunity.models.User;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageRequest request,
                                         @AuthenticationPrincipal UserDetails currentUser) {
        User sender = userService.getByEmail(currentUser.getUsername());

        ChatMessage message = chatMessageService.sendMessage(request, sender.getId());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam("proposalId") Long proposalId,
                                                        @RequestParam("receiverId") Long receiverId,
                                                        @AuthenticationPrincipal UserDetails currentUser) {
        User sender = userService.getByEmail(currentUser.getUsername());

        List<ChatMessage> history = chatMessageService.getChatHistory(proposalId, sender.getId(), receiverId);
        return ResponseEntity.ok(history);
    }
}