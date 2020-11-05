package by.homesite.gator.parser;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import by.homesite.gator.service.dto.CategoryDTO;

@Component("kufar")
public class KufarParser implements Parser
{
    private static final Logger log = LoggerFactory.getLogger(KufarParser.class);

    @Override
    @Async
    public CompletableFuture<Integer> parseItems(CategoryDTO categoryDTO)
    {
        log.info("Starting Kufar parser for {}", categoryDTO.getLink());
        return CompletableFuture.completedFuture(0);
    }
}
