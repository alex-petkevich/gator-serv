package by.homesite.gator.messaging;

import org.springframework.stereotype.Component;

import by.homesite.gator.messaging.dto.Item;

@Component
public class MessageConsumer
{
    public void receiveMessage(Item message) {
        System.out.println("Received <" + message.getId() + ">");
    }

}
