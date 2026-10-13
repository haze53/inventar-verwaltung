package de.hss.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class ProduktSuche {

    private static final String API_URL = "https://world.openfoodfacts.org/api/v2/product/%s.json";

    private final HttpClient client;

    public ProduktSuche() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public Optional<ProduktInfo> nachBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            return Optional.empty();
        }
        String sauber = barcode.trim();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL.formatted(sauber)))
                .header("User-Agent", "inventar-verwaltung-schulprojekt/1.0")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Optional.empty();
            }
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!root.has("product") || !root.get("product").isJsonObject()) {
                return Optional.empty();
            }
            JsonObject product = root.getAsJsonObject("product");
            String name = text(product, "product_name");
            if (name.isBlank()) {
                name = text(product, "generic_name");
            }
            String kategorie = text(product, "categories");
            if (kategorie.contains(",")) {
                kategorie = kategorie.split(",")[0].trim();
            }
            if (name.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(new ProduktInfo(name, kategorie));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String text(JsonObject object, String feld) {
        if (!object.has(feld) || object.get(feld).isJsonNull()) {
            return "";
        }
        return object.get(feld).getAsString().trim();
    }

    public record ProduktInfo(String name, String kategorie) {
    }
}
