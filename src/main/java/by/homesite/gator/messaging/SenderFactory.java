package by.homesite.gator.messaging;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SenderFactory implements ApplicationContextAware
{
    private static ApplicationContext ac;

    static public Sender getSender(String siteName) {
        if (siteName == null || !ac.containsBean(siteName))
            return null;

        return (Sender) ac.getBean(siteName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        ac = applicationContext;
    }
}
