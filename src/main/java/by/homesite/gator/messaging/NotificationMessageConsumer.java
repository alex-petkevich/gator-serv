package by.homesite.gator.messaging;

import java.util.List;

import org.springframework.stereotype.Component;

import by.homesite.gator.messaging.dto.Item;
import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.dto.NotificationDTO;

@Component
public class NotificationMessageConsumer
{
    private NotificationService notificationService;

    public NotificationMessageConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void receiveMessage(Item message) {
        System.out.println("Received <" + message.getId() + ">");
        List<NotificationDTO> notifications = notificationService.findAll();
        notifications.forEach(notificationDTO -> {
            Sender sender = SenderFactory.getSender(notificationDTO.getName());
            if (sender != null)
            {
                sender.sendNotifications(message.getId());
            }
        });
    }

}
