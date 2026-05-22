package oris.travelcommunity.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long proposalId;
    private Long receiverId;
    private String messageText;
}