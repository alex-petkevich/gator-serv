package by.homesite.gator.scheduler;

import java.util.List;
import java.util.concurrent.ExecutionException;

import by.homesite.gator.config.ApplicationProperties;
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

    private final ApplicationProperties applicationProperties;

    public CrawlerScheduled(CategoryService categoryService, SiteService siteService, ItemService itemService, ApplicationProperties applicationProperties) {
        this.categoryService = categoryService;
        this.siteService = siteService;
        this.itemService = itemService;
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(fixedRate = Constants.PARSE_ITEMS_PERIOD)
    public void parseSites() throws ExecutionException, InterruptedException
    {
        if (Constants.DISABLED.equals(applicationProperties.getGeneral().getCrawlerSchedulers()))
            return;

        List<CategoryDTO> categories = categoryService.search("active:true");
        for (CategoryDTO category : categories)
        {
            processCategoryLink(category);
        }

    }

    @Scheduled(fixedRate = Constants.PURGE_ITEMS_PERIOD)
    public void purgeOldItems() {
        if (Constants.DISABLED.equals(applicationProperties.getGeneral().getCrawlerSchedulers()))
            return;

        itemService.deleteOldItems(Constants.TTL_ITEMS_DAYS);
    }

    private void processCategoryLink(CategoryDTO category) throws ExecutionException, InterruptedException
    {
        SiteDTO site = siteService.findOne(category.getSiteId()).get();

        Parser parser = ParseFactory.getParser(site.getName());

        if (parser != null) {
            int parsed = parser.parseItems(category).get();
            log.debug("Parsed {} items in {}", parsed, category.getName());
        } else {
            log.error("Parser not found: {}", site.getTitle());
        }
    }
}
