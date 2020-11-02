package by.homesite.gator.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import by.homesite.gator.service.dto.CategoryDTO;

@Component("onliner")
public class OnlinerParser implements Parser
{
    private static final Logger log = LoggerFactory.getLogger(OnlinerParser.class);

    @Override
    public void parseItems(CategoryDTO categoryDTO)
    {
        log.info("Starting Onliner parser for {}", categoryDTO.getLink());
    }
}
