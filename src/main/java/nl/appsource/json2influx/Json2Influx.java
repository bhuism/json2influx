package nl.appsource.json2influx;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Json2Influx implements CommandLineRunner {

    @Value("${url}")
    private String url;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    private final Duration INTERVAL = Duration.ofMinutes(1);

    @Autowired
    private WebClient.Builder builder;

    private WebClient webClient;
    
    @PostConstruct
    public void postConstruct() {
        webClient = builder.baseUrl("https://production.api.coindesk.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public void run(String... args) throws Exception {
        
        while (true) {
            webClient
                .get()
                .uri("/v1/currency/BTC/ticker")
                .retrieve()
                .bodyToFlux(StockMeasurement.class)
                .log()
                .subscribe(new InfluxClient(url, username, password));
//                .subscribe(System.out::println)
                ;
            Thread.sleep(INTERVAL.toMillis());
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Json2Influx.class)
            .bannerMode(Mode.OFF)
            .web(WebApplicationType.NONE)
            .run(args);
    }

}
