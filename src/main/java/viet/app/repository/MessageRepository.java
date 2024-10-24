package viet.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viet.app.model.Conversation;
import viet.app.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByConversationId(Conversation conversation);

    void deleteAllByConversationId(Conversation conversation);
}
