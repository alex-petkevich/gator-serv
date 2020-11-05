package by.homesite.gator.parser;

import java.util.concurrent.CompletableFuture;

import by.homesite.gator.service.dto.CategoryDTO;

public interface Parser
{
    CompletableFuture<Integer> parseItems(CategoryDTO categoryDTO);
}
