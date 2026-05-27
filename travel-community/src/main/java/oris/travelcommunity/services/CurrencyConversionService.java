package oris.travelcommunity.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public BigDecimal convertRubToUsd(BigDecimal priceInRub) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://open.er-api.com/v6/latest/RUB"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                double rubToUsdRate = root.path("rates").path("USD").asDouble();

                return priceInRub.multiply(BigDecimal.valueOf(rubToUsdRate))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.error("Ошибка при запросе к API курсов валют", e);
        }
        return priceInRub.multiply(BigDecimal.valueOf(0.011)).setScale(2, RoundingMode.HALF_UP);
    }
}