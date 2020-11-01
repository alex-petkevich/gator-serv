package by.homesite.gator.scheduler;

import by.homesite.gator.service.CategoryService;
import by.homesite.gator.service.dto.CategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrawlerScheduled {

    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduled.class);

    private final CategoryService categoryService;

    public CrawlerScheduled(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Scheduled(fixedRate = 10000)
    public void parseSites() {

        List<CategoryDTO> categories = categoryService.search("active:true");

        for(CategoryDTO category: categories) {
            log.info("Start parsing {}", category.getLink());

        }


    }
}
