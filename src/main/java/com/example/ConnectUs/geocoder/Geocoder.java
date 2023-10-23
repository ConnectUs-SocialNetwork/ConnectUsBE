package com.example.ConnectUs.geocoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
@Service
public class Geocoder {

    private static final String GEOCODING_RESOURCE = "https://geocode.search.hereapi.com/v1/geocode";
    private static final String API_KEY = "zxvvvasMUEMByVew8lECk-vROeVvgUVtcrqI2AHRT_4";

    public String GeocodeSync(String query) throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();

        String encodedQuery = URLEncoder.encode(query,"UTF-8");
        String requestUri = GEOCODING_RESOURCE + "?apiKey=" + API_KEY + "&q=" + encodedQuery;

        HttpRequest geocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
                .timeout(Duration.ofMillis(2000)).build();

        HttpResponse geocodingResponse = httpClient.send(geocodingRequest,
                HttpResponse.BodyHandlers.ofString());

        return geocodingResponse.body().toString();
    }

    public JsonNode getLocationInformationFromAddress(String addressString){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String response = GeocodeSync(addressString);
            JsonNode responseJsonNode = mapper.readTree(response);

            JsonNode items = responseJsonNode.get("items");

            if (items != null && items.isArray() && items.size() > 0) {
                JsonNode firstItem = items.get(0);

                return firstItem;
            }

            return items;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }






}