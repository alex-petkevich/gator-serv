package by.homesite.gator.parser.util;

import static by.homesite.gator.config.Constants.CONNECT_TIMEOUT;

import java.time.Duration;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebClient
{
    private final HttpHeaders headers;
    private final RestTemplate restTemplate;
    private HttpStatus status;

    public WebClient(RestTemplateBuilder builder)
    {
        HttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD).build())
            .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        clientHttpRequestFactory.setConnectionRequestTimeout(CONNECT_TIMEOUT);
        clientHttpRequestFactory.setReadTimeout(CONNECT_TIMEOUT);
        clientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);

        //this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT)).build();
        this.restTemplate = new RestTemplate(clientHttpRequestFactory);
        this.headers = new HttpHeaders();
        headers.add("Accept", "*/*");
    }

    public String get(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    public void put(String uri, String json) {
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    public void delete(String uri) {
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, String.class);
        this.setStatus(responseEntity.getStatusCode());
    }

    private void setStatus(HttpStatus statusCode)
    {
        this.status = statusCode;
    }

    public HttpStatus getStatus()
    {
        return status;
    }
}
