package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    private Task task;
    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    void init() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        task = new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60);
        epic = new Epic("TestEpic", "EpicDescription");
        subtask = new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,1,0,0),60, 1);
        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createTask(task);
        Task task2 = new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,1,0,0), 60);
        taskManager.createTask(task2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(2, actual.size(), "Не верное количество задач");
        assertEquals(List.of(task, task2), actual, "Задачи не совпадают");
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0),60, 1);
        taskManager.createSubtask(subtask2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> actual = gson.fromJson(response.body(), subtaskType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(2, actual.size(), "Не верное количество задач");
        assertEquals(List.of(subtask, subtask2), actual, "Задачи не совпадают");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Epic epic2 = new Epic("TestEpic", "EpicDescription");
        taskManager.createEpic(epic2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<ArrayList<Epic>>() {}.getType();
        List<Epic> actual = gson.fromJson(response.body(), epicType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(2, actual.size(), "Не верное количество задач");
        assertEquals(List.of(epic, epic2), actual, "Задачи не совпадают");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createTask(task);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {}.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task, actual, "Задачи не совпадают");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Subtask>() {}.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(subtask, actual, "Задачи не совпадают");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {}.getType();
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(epic, actual, "Задачи не совпадают");
    }

    @Test
    void addOrUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        byte[] data = gson.toJson(task).getBytes();
        InputStream is = new ByteArrayInputStream(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        task.setId(1);
        Task task1 = taskManager.getTaskById(1);

        assertEquals(task, task1, "Задачи не совпадают");

        byte[] data2 = gson.toJson(task).getBytes();
        InputStream is2 = new ByteArrayInputStream(data2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        assertEquals(1, taskManager.getTasks().size(), "Задача не обновляется, а добавляется в базу");
    }

    @Test
    void addOrUpdateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        taskManager.createEpic(epic);
        byte[] data = gson.toJson(subtask).getBytes();
        InputStream is = new ByteArrayInputStream(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        subtask.setId(2);
        Subtask subtask1 = taskManager.getSubtaskById(2);

        assertEquals(subtask, subtask1, "Задачи не совпадают");

        byte[] data2 = gson.toJson(subtask).getBytes();
        InputStream is2 = new ByteArrayInputStream(data2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        assertEquals(1, taskManager.getSubtasks().size(), "Задача не обновляется, а добавляется в базу");
    }

    @Test
    void addOrUpdateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        byte[] data = gson.toJson(epic).getBytes();
        InputStream is = new ByteArrayInputStream(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        epic.setId(1);
        Epic epic1 = taskManager.getEpicById(1);

        assertEquals(epic, epic1, "Задачи не совпадают");

        byte[] data2 = gson.toJson(epic).getBytes();
        InputStream is2 = new ByteArrayInputStream(data2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        assertEquals(1, taskManager.getEpics().size(), "Задача не обновляется, а добавляется в базу");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createTask(task);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {}.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления задачи не сработал");
    }

    @Test
    void deleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createEpic(epic);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {}.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления подзадачи не сработал");
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {}.getType();
        Epic actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления подзадачи не сработал");
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createTask(task);
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0), 60));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления всех задач не сработал");
    }

    @Test
    void deleteAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0), 60, 1));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления всех подзадач не сработал");
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0), 60, 1));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {}.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNull(actual, "Метод DELETE для удаления всех эпиков не сработал");

        URI uri2 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        Type taskType2 = new TypeToken<ArrayList<Subtask>>() {}.getType();
        Task actual2 = gson.fromJson(response.body(), taskType2);

        assertNull(actual2, "При удалении эпиков subtasks not null");
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0),60, 1);
        taskManager.createSubtask(subtask2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "При удалении эпиков subtasks not null");
        assertEquals(List.of(subtask, subtask2), actual, "Задачи не совпадают");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(3);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type historyType = new TypeToken<ArrayList<Integer>>() {}.getType();
        List<Integer> actual = gson.fromJson(response.body(), historyType);

        assertNotNull(actual, "При удалении эпиков subtasks not null");
        assertEquals(List.of(2, 3), actual, "Задачи не совпадают");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,2,0,0),60, 1);
        taskManager.createSubtask(subtask2);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {}.getType();
        List<Subtask> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "При удалении эпиков subtasks not null");
        assertEquals(List.of(subtask, subtask2), actual, "Задачи не совпадают");
        assertNotEquals(List.of(subtask2, subtask), actual, "Задачи не совпадают");
    }

    @Test
    void saveAndLoad() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        byte[] data = gson.toJson(task).getBytes();
        InputStream is = new ByteArrayInputStream(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofInputStream (() -> is))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpTaskManager httpTaskManager2 = HttpTaskManager.load(KVServer.KVSERVER_URI, "httptaskmanager");

        assertEquals(httpTaskManager2.getTasks(), taskManager.getTasks(), "При загрузке и сохранении задачи не совпадают");
    }

    @Test
    void load() {

    }
}