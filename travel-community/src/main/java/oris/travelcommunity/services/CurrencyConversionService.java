package oris.travelcommunity.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class CurrencyConversionService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public BigDecimal convertRubToUsd(BigDecimal priceInRub) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://open.er-api.com/v6/latest/RUB"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = OBJECT_MAPPER.readTree(response.body());
                double rate = root.path("rates").path("USD").asDouble();
                return priceInRub.multiply(BigDecimal.valueOf(rate))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.error("Ошибка при запросе к API курсов валют", e);
        }
        return priceInRub.multiply(BigDecimal.valueOf(0.011)).setScale(2, RoundingMode.HALF_UP);
    }
}
