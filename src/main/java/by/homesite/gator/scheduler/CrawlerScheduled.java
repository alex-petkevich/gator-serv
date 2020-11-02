package by.homesite.gator.scheduler;

import java.util.MissingResourceException;

import by.homesite.gator.parser.ParseFactory;
import by.homesite.gator.parser.Parser;
import by.homesite.gator.service.CategoryService;
import by.homesite.gator.service.SiteService;
import by.homesite.gator.service.dto.CategoryDTO;
import by.homesite.gator.service.dto.SiteDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlerScheduled {

    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduled.class);

    private final CategoryService categoryService;

    private final SiteService siteService;

    public CrawlerScheduled(CategoryService categoryService, SiteService siteService) {
        this.categoryService = categoryService;
        this.siteService = siteService;
    }

    @Scheduled(fixedRate = 10000)
    public void parseSites() {

        categoryService.search("active:true").forEach(this::processCategoryLink);

    }

    private void processCategoryLink(CategoryDTO category)
    {
        SiteDTO site = siteService.findOne(category.getSiteId()).get();

        Parser parser = ParseFactory.getParser(site.getName());

        if (parser != null) {
            parser.parseItems(category);
        } else {
            log.error("Parser not found: {}", site.getTitle());
        }
    }
}
