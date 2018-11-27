package nl.appsource.json2influx;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
public class StockMeasurement {

    private Instant timestamp;
    private String name;
    private Double price;
    private Double volume24Hr;
    private Double marketCap;
    private Double open;
    private Double low;
    private Double high;
    private Double close;

    @SuppressWarnings("unchecked")
    @JsonProperty("data")
    private void unpackNested(final Map<String, Object> data) {
        
        final Map<String, Object> currency = (Map<String, Object>) data.get("currency");
        final Map<String, Object> BTC = (Map<String, Object>) currency.get("BTC");
        
        name = "" + BTC.get("name");
        
        final Map<String, Object> quotes = (Map<String, Object>) BTC.get("quotes");
        final Map<String, Object> USD = (Map<String, Object>) quotes.get("USD");
        
        price = Double.parseDouble("" + USD.get("price"));
        volume24Hr = Double.parseDouble("" + USD.get("volume24Hr"));
        marketCap = Double.parseDouble("" + USD.get("marketCap"));
        open = Double.parseDouble("" + USD.get("open"));
        low = Double.parseDouble("" + USD.get("low"));
        high = Double.parseDouble("" + USD.get("high"));
        close = Double.parseDouble("" + USD.get("close"));

    }

}
