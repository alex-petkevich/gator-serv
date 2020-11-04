package by.homesite.gator.parser;

import static by.homesite.gator.parser.ParserUtil.getAbsLink;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import by.homesite.gator.service.ItemService;
import by.homesite.gator.service.dto.CategoryDTO;
import by.homesite.gator.service.dto.ItemDTO;
import io.searchbox.strings.StringUtils;

@Component("onliner")
public class OnlinerParser implements Parser
{
    private static final Logger log = LoggerFactory.getLogger(OnlinerParser.class);
    private final ItemService itemService;

    private WebClient webClient;

    public OnlinerParser(WebClient webClient, ItemService itemService)
    {
        this.webClient = webClient;
        this.itemService = itemService;
    }

    @Override
    public void parseItems(CategoryDTO categoryDTO)
    {
        log.info("Starting Onliner parser for {}", categoryDTO.getLink());

        String response = webClient.get(categoryDTO.getLink());

        if (!webClient.getStatus().is2xxSuccessful())
            return;

        Document html = Jsoup.parse(response);

        Elements trs = html.body().select("table.ba-tbl-list__table > tbody > tr");
        trs.forEach(el ->{
            if (!el.hasClass("m-imp") && !el.hasClass("sorting__1")) {
                String title = el.getElementsByTag("h2").text();
                String link = getAbsLink(el.getElementsByTag("h2").get(0).getElementsByTag("a").get(0).attr("href"),
                    categoryDTO.getLink());
                String nativeId = link.substring(link.lastIndexOf("=")+1);
                int alreadyExistsElements = 0;
                if (StringUtils.isNotBlank(nativeId)) {
                    alreadyExistsElements = itemService.search("nativeId:"+nativeId, Pageable.unpaged()).getNumberOfElements();
                }

                if (StringUtils.isNotBlank(title) && alreadyExistsElements == 0)
                {
                    ItemDTO item = new ItemDTO();
                    item.setTitle(title);
                    item.setDescription(el.getElementsByClass("ba-description").text());
                    item.setNativeId(Long.parseLong(nativeId));
                    item.setLink(link);
                    item.setImage(el.getElementsByClass("img-va").get(0).getElementsByTag("img").attr("src"));
                    item.setOwnerName(el.getElementsByClass("gray").text());
                    item.setOwnerLink(el.getElementsByClass("gray").attr("href"));
                    String price = el.getElementsByClass("price-primary").text();
                    if (StringUtils.isNotBlank(price)) {
                        String cleanPrice = price.replaceAll("[^\\d,]","").replaceAll(",",".");
                        item.setPrice(Float.parseFloat(cleanPrice));
                    }
                    item.setCategoryId(categoryDTO.getId());
                    item.setActive(true);
                    itemService.save(item);

                    log.info("ad title: {}", title);
                }
            }
        });


    }
}
