package oris.travelcommunity.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oris.travelcommunity.dto.request.ChatMessageRequest;
import oris.travelcommunity.exceptions.NotFoundException;
import oris.travelcommunity.models.ChatMessage;
import oris.travelcommunity.models.TripProposal;
import oris.travelcommunity.models.User;
import oris.travelcommunity.repositories.ChatMessageRepository;
import oris.travelcommunity.repositories.TripProposalRepository;
import oris.travelcommunity.repositories.UserRepository;
import oris.travelcommunity.services.ChatMessageService;
import oris.travelcommunity.services.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final TripProposalRepository tripProposalRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public ChatMessage sendMessage(ChatMessageRequest messageRequest, Long senderId) {
        TripProposal proposal = tripProposalRepository.findById(messageRequest.getProposalId())
                .orElseThrow(() -> new NotFoundException("Поездка не найдена"));
        User sender = userService.getById(senderId);
        User receiver = userService.getById(messageRequest.getReceiverId());

        ChatMessage message = ChatMessage.builder()
                .proposal(proposal)
                .sender(sender)
                .receiver(receiver)
                .messageText(messageRequest.getMessageText())
                .isRead(false)
                .build();

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long proposalId, Long userId1, Long userId2) {
        return chatMessageRepository.findChatHistory(proposalId, userId1, userId2);
    }

}
