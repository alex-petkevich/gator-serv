package by.homesite.gator.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import by.homesite.gator.service.dto.CategoryDTO;

@Component("kosht")
public class KoshtParser implements Parser
{
    private static final Logger log = LoggerFactory.getLogger(KoshtParser.class);

    @Override
    public void parseItems(CategoryDTO categoryDTO)
    {
        log.info("Starting Kosht parser for {}", categoryDTO.getLink());
    }
}
