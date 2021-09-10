package by.homesite.gator.messaging;

import org.springframework.stereotype.Component;

import by.homesite.gator.domain.Notification;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.MailService;
import by.homesite.gator.service.NotificationService;
import by.homesite.gator.service.UserNotificationsService;
import by.homesite.gator.service.dto.ItemDTO;

@Component("email")
public class NotificationEmailSender implements Sender
{
    public static final String EMAIL = "email";
    private final NotificationService notificationService;
    private final UserNotificationsService userNotificationsService;
    private final ItemService itemService;
    private final MailService mailService;

    public NotificationEmailSender(NotificationService notificationService, ItemService itemService, MailService mailService, UserNotificationsService userNotificationsService)
    {
        this.notificationService = notificationService;
        this.itemService = itemService;
        this.mailService = mailService;
        this.userNotificationsService = userNotificationsService;
    }

    @Override
    public Integer sendNotifications(Long itemId)
    {
        ItemDTO item = itemService.findOne(itemId).orElse(null);
        Notification notification = notificationService.findByName(EMAIL);
        if (notification != null && item != null) {
            userNotificationsService.findUsersForNotifications(notification.getId(), item).forEach(userNotifications -> {
                mailService.sendNotification(userNotifications.getUser(), item);
                userNotificationsService.calculate(userNotifications);
            });
        }
        return null;
    }
}
