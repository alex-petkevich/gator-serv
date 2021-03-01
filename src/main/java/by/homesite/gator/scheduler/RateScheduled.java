package by.homesite.gator.scheduler;

import by.homesite.gator.config.ApplicationProperties;
import by.homesite.gator.config.Constants;
import by.homesite.gator.service.RateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateScheduled {
    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduled.class);
    private final RateService rateService;
    private final ApplicationProperties applicationProperties;

    public RateScheduled(RateService rateService, ApplicationProperties applicationProperties) {
        this.rateService = rateService;
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(fixedRate = Constants.RATES_FETCH_PERIOD)
    public void fetchRates() {
        if (Constants.DISABLED.equals(applicationProperties.getGeneral().getRateSchedulers()))
            return;

        rateService.fetchRates();
    }

}
