package servers;

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
        apiToken = getToken();
    }

    public String getToken() {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uriRegister)
                .GET()
                .build();
        String value = "";
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                value = response.body();
            }
        } catch (IOException | InterruptedException | NullPointerException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                "Проверьте адрес и повторите попытку");
        }
        return value;
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
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Менеджер успешно сохранен");
            } else {
                System.out.println("Менеджер не удалось сохранить, код ответа: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("При сохранении менеджера возникла ошибка");
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
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("При сохранении менеджера возникла ошибка");
            return "";
        }
    }
}
