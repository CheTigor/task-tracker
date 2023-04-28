package servers;

import manager.exceptions.ManagerSaveException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URI uriRegister;
    private final URI uriLoad;
    private final URI uriSave;
    private final String apiToken;

    public KVTaskClient(URI uri) {
        client = HttpClient.newHttpClient();
        uriRegister = URI.create(uri + "/register/");
        uriLoad = URI.create(uri + "/load/");
        uriSave = URI.create(uri + "/save/");
        apiToken = register();
    }

    public String register() {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRegister)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do register request, status code: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException | NullPointerException e) {
            throw new ManagerSaveException("Error during registration");
        }
    }

    public void put(String key, String json) {
        final String uri = uriSave.toString() + key + "?API_TOKEN=" + apiToken;
        byte[] data = json.getBytes();
        InputStream is = new ByteArrayInputStream(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is))
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode());
            }
            System.out.println("Менеджер успешно сохранен!");
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request", e);
        }
    }

    public String load(String key) {
        final String uri = uriLoad.toString() + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do load request, status code: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do load request", e);
        }
    }
}
