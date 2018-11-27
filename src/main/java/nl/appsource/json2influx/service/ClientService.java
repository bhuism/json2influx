package nl.appsource.json2influx.service;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.appsource.json2influx.StockMeasurement;
import reactor.core.publisher.Flux;

@Service
public class ClientService {

    @Autowired
    private WebClient.Builder builder;

    private WebClient client;

    @PostConstruct
    public void postConstruct() {
        client = builder.baseUrl("https://production.api.coindesk.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void getData(final Consumer<StockMeasurement> stockMeasureMentConsumer) {
        client
                .get()
                .uri("/v1/currency/BTC/ticker")
                .retrieve()
                .bodyToMono(StockMeasurement.class)
//                .doOnSuccess(new InfluxConsumer())
                .block();
    }

    public Flux<StockMeasurement> getData2() {
        return client
                .get()
                .uri("/v1/currency/BTC/ticker")
                .retrieve()
                //.exchange()
                //.flatMap(cr -> cr.bodyToFlux(StockMeasurement.class))
                .bodyToFlux(StockMeasurement.class)
        ;
    }

}
