package by.homesite.gator.parser;

import by.homesite.gator.parser.util.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class CurrencyParser {
    private static final String HBRB_URL_RATE = "https://www.nbrb.by/api/exrates/rates/{code}?parammode=2";
    public static final String CUR_OFFICIAL_RATE = "Cur_OfficialRate";
    public static final String CUR_SCALE = "Cur_Scale";

    private WebClient webClient;


    public CurrencyParser(WebClient webClient) {
        this.webClient = webClient;
    }

    public Map<String, String> getCurrency(Set<String> currencies) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (currencies == null || currencies.size() == 0)
            return result;

        ObjectMapper mapper = new ObjectMapper();
        for(String curr: currencies) {
            String url = HBRB_URL_RATE.replace("{code}", curr);
            String rateInfo = webClient.get(url);
            Map<String, Object> rateMap = mapper.readValue(rateInfo, Map.class);
            Double rate = (Double) rateMap.get(CUR_OFFICIAL_RATE);
            Integer scale = (Integer) rateMap.get(CUR_SCALE);
            result.put(curr, Double.toString(rate / scale));
        }

        return result;
    }
}
