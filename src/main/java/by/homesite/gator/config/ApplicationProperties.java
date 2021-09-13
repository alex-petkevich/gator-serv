package by.homesite.gator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Gator.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final ApplicationProperties.General general = new ApplicationProperties.General();

    public ApplicationProperties() {}

    public General getGeneral() {
        return general;
    }

    public static class General {

        private String crawlerSchedulers = "crawler-schedulers";

        private String rateSchedulers = "rate-schedulers";

        public General() {}

        public String getCrawlerSchedulers() {
            return crawlerSchedulers;
        }

        public void setCrawlerSchedulers(String crawlerSchedulers) {
            this.crawlerSchedulers = crawlerSchedulers;
        }

        public String getRateSchedulers() {
            return rateSchedulers;
        }

        public void setRateSchedulers(String rateSchedulers) {
            this.rateSchedulers = rateSchedulers;
        }
    }
}
