package by.homesite.gator.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import by.homesite.gator.config.Constants;
import by.homesite.gator.messaging.dto.Item;

@Component
public class MessageProducer
{
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(Item message) {
        rabbitTemplate.convertAndSend(Constants.topicExchangeName, "ads." + message.getSiteName(), message);

    }
}
