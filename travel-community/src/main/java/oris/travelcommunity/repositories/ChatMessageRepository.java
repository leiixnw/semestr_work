package oris.travelcommunity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import oris.travelcommunity.models.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            select cm from ChatMessage cm where cm.proposal.id = :proposalId
            and ((cm.sender.id = :u1 and cm.receiver.id = :u2) or (cm.sender.id = :u2 and cm.receiver.id = :u1))
            order by cm.sentAt asc
    """)
    List<ChatMessage> findChatHistory(@Param("proposalId") Long proposalId,
                                      @Param("u1") Long userId1,
                                      @Param("u2") Long userId2);
}
