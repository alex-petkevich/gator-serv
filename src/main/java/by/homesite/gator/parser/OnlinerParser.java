package by.homesite.gator.parser;

import static by.homesite.gator.config.Constants.*;
import static by.homesite.gator.parser.util.ParserUtil.getAbsLink;

import by.homesite.gator.messaging.MessageProducer;
import by.homesite.gator.messaging.dto.Item;
import by.homesite.gator.parser.util.WebClient;
import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.dto.CategoryDTO;
import by.homesite.gator.service.dto.ItemDTO;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("onliner")
public class OnlinerParser implements Parser {

    private static final Logger log = LoggerFactory.getLogger(OnlinerParser.class);
    private final ItemService itemService;
    private final WebClient webClient;
    private final MessageProducer messageProducer;

    public OnlinerParser(WebClient webClient, ItemService itemService, MessageProducer messageProducer) {
        this.webClient = webClient;
        this.itemService = itemService;
        this.messageProducer = messageProducer;
    }

    @Override
    @Async
    public CompletableFuture<Integer> parseItems(CategoryDTO categoryDTO) {
        log.debug("Starting Onliner parser for {}", categoryDTO.getLink());
        final int[] result = { 0 };

        String response = webClient.get(categoryDTO.getLink());

        if (!webClient.getStatus().is2xxSuccessful()) {
            log.debug("...failed {}", webClient.getStatus().getReasonPhrase());
            return CompletableFuture.completedFuture(result[0]);
        }

        log.debug("received content, start parser, {}", categoryDTO.getLink());
        Document html = Jsoup.parse(response);
        log.debug("...parsed");

        Elements trs = html.body().select("table.ba-tbl-list__table > tbody > tr");
        trs.forEach(
            el -> {
                try {
                    if (!el.hasClass("m-imp") && !el.hasClass("sorting__1")) {
                        String title = el.getElementsByTag("h2").text();
                        String link = getLink(categoryDTO.getLink(), el);
                        if (link.equals("")) return;
                        String nativeId = link.substring(link.lastIndexOf("=") + 1);
                        int alreadyExistsElements = 0;
                        if (StringUtils.isNotBlank(nativeId)) {
                            alreadyExistsElements =
                                itemService.search("nativeId:" + nativeId, "", "", Pageable.unpaged()).getNumberOfElements();
                        }

                        if (StringUtils.isNotBlank(title) && alreadyExistsElements == 0) {
                            ItemDTO item = new ItemDTO();
                            item.setTitle(title);
                            item.setDescription(el.getElementsByClass("ba-description").text());
                            item.setNativeId(Long.parseLong(nativeId));
                            item.setLink(link);
                            Elements elimgs = el.getElementsByClass("img-va");
                            if (elimgs.size() > 0) item.setImage(elimgs.get(0).getElementsByTag("img").attr("src"));
                            item.setOwnerName(el.getElementsByClass("gray").text());
                            item.setOwnerLink(el.getElementsByClass("gray").attr("href"));
                            String price = el.getElementsByClass("price-primary").text();
                            item.setPrice(getPrice(price));
                            item.setCategoryId(categoryDTO.getId());
                            item.setActive(true);
                            item.setType(extractType(el));

                            item = itemService.save(item);

                            produceMessage(categoryDTO, item);

                            result[0]++;
                            log.debug("ad title: {}", title);
                        }
                    }
                } catch (Exception e) {
                    log.error("parsing error: {}", e.getMessage());
                }
            }
        );
        log.debug("...completed");

        return CompletableFuture.completedFuture(result[0]);
    }

    private Float getPrice(String price) {
        Float fPrice = null;
        if (StringUtils.isNotBlank(price)) {
            String cleanPrice = price.replaceAll("[^\\d,]", "").replaceAll(",", ".");
            fPrice = Float.parseFloat(cleanPrice);
        }
        return fPrice;
    }

    private String getLink(String parentLink, Element el) {
        String link = "";
        Elements elslink = el.getElementsByTag("h2");
        if (elslink.size() > 0) {
            Elements elsa = elslink.get(0).getElementsByTag("a");
            if (elsa.size() > 0) {
                link = getAbsLink(elsa.get(0).attr("href"), parentLink);
            }
        }
        return link;
    }

    private int extractType(Element el) {
        int type = 0;
        Elements eltypes = el.getElementsByClass("ba-label");
        for (Element eltype : eltypes) {
            if (eltype.hasClass("ba-label-1")) type = ITEM_TYPE_IMPORTANT;
            if (eltype.hasClass("ba-label-2")) type = ITEM_TYPE_SELL;
            if (eltype.hasClass("ba-label-3")) type = ITEM_TYPE_BUY;
            if (eltype.hasClass("ba-label-4")) type = ITEM_TYPE_CHANGE;
            if (eltype.hasClass("ba-label-6")) type = ITEM_TYPE_RENT;
            if (eltype.hasClass("ba-label-5")) type = ITEM_TYPE_SERVICE;
            if (eltype.hasClass("ba-label-7")) type = ITEM_TYPE_CLOSED;
        }
        return type;
    }

    private void produceMessage(CategoryDTO categoryDTO, ItemDTO item) {
        Item messageItem = new Item();
        messageItem.setId(item.getId());
        messageItem.setSiteName(categoryDTO.getSiteTitle());
        messageProducer.sendNotification(messageItem);
    }
}
