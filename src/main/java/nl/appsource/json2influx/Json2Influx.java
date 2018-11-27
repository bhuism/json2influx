package nl.appsource.json2influx;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.extern.slf4j.Slf4j;
import nl.appsource.json2influx.service.ClientService;

@Slf4j
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
    private ClientService clientService;

    @Override
    public void run(String... args) throws Exception {
        log.debug("Starting, url=" + url);
        while (true) {
            clientService.getData2().log().subscribe(new InfluxClient(url, username, password));
            Thread.sleep(INTERVAL.toMillis());
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Json2Influx.class).bannerMode(Mode.OFF).web(WebApplicationType.NONE).run(args);
    }

}
