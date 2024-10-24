package viet.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viet.app.dto.response.ConversationResponse;
import viet.app.model.Conversation;
import viet.app.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Conversation findConversationByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query(
            nativeQuery = true,
            value = """
        select C.id as conversationId, U.id as otherUserId, U.name as otherUserName, M.message as lastMessage, M.timestamp as lastMessageTimestamp, U.avatar as userAvatar 
        from tbl_conversation as C
        inner join tbl_user as U
        on (C.user1_id = U.id or C.user2_id = U.id) and U.id != ?1
        left join (
            select
                conversation_id,
                (select content from tbl_message m2 where m2.conversation_id = m.conversation_id order by m2.timestamp desc limit 1) as message,
                max(timestamp) as timestamp
            from tbl_message m group by conversation_id
        ) as M
        on C.id = M.conversation_id
        where C.user1_id = ?1 or C.user2_id = ?1
        order by M.timestamp desc;
    """
    )
    List<ConversationResponse> findConversationsByUserId(long userId);

}
