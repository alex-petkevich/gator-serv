package by.homesite.gator.parser;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ParseFactory implements ApplicationContextAware
{
    private static ApplicationContext ac;

    static public Parser getParser(String siteName) {
        if (siteName == null || !ac.containsBean(siteName))
            return null;

        return (Parser) ac.getBean(siteName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        ac = applicationContext;
    }
}
