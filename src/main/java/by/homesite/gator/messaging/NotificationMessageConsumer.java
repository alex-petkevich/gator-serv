package by.homesite.gator.messaging;

import by.homesite.gator.messaging.dto.Item;
import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.dto.NotificationDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageConsumer {

    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(NotificationMessageConsumer.class);

    public NotificationMessageConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void receiveMessage(Item message) {
        log.debug("Received <" + message.getId() + ">");
        List<NotificationDTO> notifications = notificationService.findAll();
        notifications.forEach(
            notificationDTO -> {
                Sender sender = SenderFactory.getSender(notificationDTO.getName());
                if (sender != null) {
                    sender.sendNotifications(message.getId());
                }
            }
        );
    }
}
