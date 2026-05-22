package oris.travelcommunity.services;

import org.springframework.data.repository.query.Param;
import oris.travelcommunity.dto.request.ChatMessageRequest;
import oris.travelcommunity.models.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage sendMessage(ChatMessageRequest messageRequest, Long senderId);
    List<ChatMessage> getChatHistory(Long proposalId, Long userId1, Long userId2);
}
