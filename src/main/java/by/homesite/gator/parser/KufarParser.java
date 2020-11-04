package by.homesite.gator.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import by.homesite.gator.service.dto.CategoryDTO;

@Component("kufar")
public class KufarParser implements Parser
{
    private static final Logger log = LoggerFactory.getLogger(KufarParser.class);

    @Override
    public void parseItems(CategoryDTO categoryDTO)
    {
        log.info("Starting Kufar parser for {}", categoryDTO.getLink());
    }
}
