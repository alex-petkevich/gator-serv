package by.homesite.gator.scheduler;

import by.homesite.gator.config.Constants;
import by.homesite.gator.service.RateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RateScheduled {
    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduled.class);
    private final RateService rateService;

    public RateScheduled(RateService rateService) {
        this.rateService = rateService;
    }

    @Scheduled(fixedRate = Constants.PURGE_ITEMS_PERIOD)
    public void fetchRates() {
        rateService.fetchRates();
    }

}
