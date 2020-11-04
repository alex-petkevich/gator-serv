package by.homesite.gator.scheduler;

import java.time.Duration;
import java.util.MissingResourceException;

import by.homesite.gator.config.Constants;
import by.homesite.gator.parser.ParseFactory;
import by.homesite.gator.parser.Parser;
import by.homesite.gator.service.CategoryService;
import by.homesite.gator.service.ItemService;
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

    private final ItemService itemService;

    public CrawlerScheduled(CategoryService categoryService, SiteService siteService, ItemService itemService) {
        this.categoryService = categoryService;
        this.siteService = siteService;
        this.itemService = itemService;
    }

    @Scheduled(fixedRate = Constants.PARSE_ITEMS_PERIOD)
    public void parseSites() {

        categoryService.search("active:true").forEach(this::processCategoryLink);

    }

    @Scheduled(fixedRate = Constants.PURGE_ITEMS_PERIOD)
    public void purgeOldItems() {
        itemService.deleteOldItems(Constants.TTL_ITEMS_DAYS);
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
