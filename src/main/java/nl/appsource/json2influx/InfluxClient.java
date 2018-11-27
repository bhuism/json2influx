package nl.appsource.json2influx;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class InfluxClient implements Consumer<StockMeasurement> {

    final String url;
    final String username;
    final String password;

    @Override
    public void accept(StockMeasurement stockMeasurement) {

        InfluxDB influxDB = null;

        try {

            influxDB = InfluxDBFactory.connect(url, username, password);

            final String dbName = "stock";

            influxDB.setDatabase(dbName);

            influxDB.enableBatch(BatchOptions.DEFAULTS.exceptionHandler((failedPoints, e) -> {
                log.error("", e);
            }));

            BatchPoints batchPoints = BatchPoints.database(dbName).consistency(ConsistencyLevel.ALL).build();

            batchPoints.point(Point.measurement("tick").time(stockMeasurement.getTimestamp().getEpochSecond(), TimeUnit.SECONDS).tag("name", stockMeasurement.getName()).addField("close", stockMeasurement.getClose())
                    .addField("high", stockMeasurement.getHigh()).addField("low", stockMeasurement.getLow()).addField("marketcap", stockMeasurement.getMarketCap()).addField("open", stockMeasurement.getOpen())
                    .addField("price", stockMeasurement.getPrice()).addField("volume24hr", stockMeasurement.getVolume24Hr()).build());

            influxDB.write(batchPoints);

            influxDB.flush();

        } finally {
            if (influxDB != null) {
                try {
                    influxDB.close();
                } catch (Exception e) {
                    log.error("influxDB.close() failed", e);
                } finally {
                    influxDB = null;
                }
            }
        }
    }

}
