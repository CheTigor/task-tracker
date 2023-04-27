package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handleTasks);
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    private void handleTasks(HttpExchange h) {
        try {
            String path = h.getRequestURI().toString();
            System.out.println(path);
            String requestMethod = h.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/task$", path)) {
                        String response = gson.toJson(taskManager.getTasks());
                        sendText(h, response);
                        //return;
                    } else if (Pattern.matches("^/tasks/subtask$", path)) {
                        String response = gson.toJson(taskManager.getSubtasks());
                        sendText(h, response);
                        //return;
                    } else if (Pattern.matches("^/tasks/epic$", path)) {
                        String response = gson.toJson(taskManager.getEpics());
                        sendText(h, response);
                        //return;
                    } else if (Pattern.matches("^/tasks/task\\?id=\\d+$", path)) {
                        getTaskById(h);
                    } else if (Pattern.matches("^/tasks/subtask\\?id=\\d+$", path)) {
                        getSubtaskById(h);
                    } else if (Pattern.matches("^/tasks/epic\\?id=\\d+$", path)) {
                        getEpicById(h);
                    } else if (Pattern.matches("^/tasks$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(h, response);
                    } else if (Pattern.matches("^/tasks/history$", path)) {
                        List<Task> history = taskManager.getHistory();
                        List<Integer> historyIds = new ArrayList<>();
                        for (Task task : history) {
                            historyIds.add(task.getId());
                        }
                        String response = gson.toJson(historyIds);
                        sendText(h, response);
                    } else if (Pattern.matches("^/tasks/subtask/epic\\?id=\\d+$", path)) {
                        getEpicSubtasks(h);
                    } else {
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/task$", path)) {
                        addOrUpdateTask(h);
                    } else if (Pattern.matches("^/tasks/subtask$", path)) {
                        addOrUpdateSubtask(h);
                    } else if (Pattern.matches("^/tasks/epic$", path)) {
                        addOrUpdateEpic(h);
                    } else {
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/task\\?id=\\d+$", path)) {
                        deleteTask(h);
                    } else if (Pattern.matches("^/tasks/subtask\\?id=\\d+$", path)) {
                        deleteSubtask(h);
                    } else if (Pattern.matches("^/tasks/epic\\?id=\\d+$", path)) {
                        deleteEpic(h);
                    } else if (Pattern.matches("^/tasks/task$", path)) {
                        if (taskManager.getTasks().equals(List.of())) {
                            System.out.println("Нет задач для удаления");
                            h.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeAllTasks();
                            System.out.println("Все самостоятельные задачи успешно удалены");
                            h.sendResponseHeaders(200,0);
                        }
                    } else if (Pattern.matches("^/tasks/subtask$", path)) {
                        if (taskManager.getSubtasks().equals(List.of())) {
                            System.out.println("Нет задач для удаления");
                            h.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeAllSubtasks();
                            System.out.println("Все подзадачи успешно удалены");
                            h.sendResponseHeaders(200,0);
                        }
                    } else if (Pattern.matches("^/tasks/epic$", path)) {
                        if (taskManager.getEpics().equals(List.of())) {
                            System.out.println("Нет задач для удаления");
                            h.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeAllEpics();
                            System.out.println("Все эпики успешно удалены");
                            h.sendResponseHeaders(200,0);
                        }
                    } else if (Pattern.matches("^/tasks$", path)) {
                        if (taskManager.getPrioritizedTasks().equals(List.of())) {
                            System.out.println("Нет задач для удаления");
                            h.sendResponseHeaders(404, 0);
                        } else {
                            taskManager.removeAllTasks();
                            taskManager.removeAllEpics();
                            System.out.println("Все задачи успешно удалены");
                            h.sendResponseHeaders(200,0);
                        }
                    } else {
                        h.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Данный метод не поддерживается: " + requestMethod);
                    h.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Сервер запущен");
        System.out.println("http://localhost:" + PORT + "/tasks/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    private String readText(HttpExchange h) throws  IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void getTaskById(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/task\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getTaskById(id) != null) {
                String response = gson.toJson(taskManager.getTaskById(id));
                sendText(h, response);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void getSubtaskById(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/subtask\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getSubtaskById(id) != null) {
                String response = gson.toJson(taskManager.getSubtaskById(id));
                sendText(h, response);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void getEpicById(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/epic\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getEpicById(id) != null) {
                String response = gson.toJson(taskManager.getEpicById(id));
                sendText(h, response);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void addOrUpdateTask(HttpExchange h) throws IOException {
        try {
            String value = readText(h);
            Type taskType = new TypeToken<Task>() {}.getType();
            Task task = gson.fromJson(value, taskType);
            if (taskManager.getTaskById(task.getId()) != null) {
                taskManager.updateTask(task);
            } else {
                taskManager.createTask(task);
            }
            System.out.println("Задача успешно создана");
            h.sendResponseHeaders(200, 0);
        } catch (NullPointerException e) {
            System.out.println("Ошибка при создании задачи");
            h.sendResponseHeaders(405, 0);
        }
    }

    public void addOrUpdateSubtask(HttpExchange h) throws IOException {
        try {
            String value = readText(h);
            Type taskType = new TypeToken<Subtask>() {}.getType();
            Subtask subtask = gson.fromJson(value, taskType);
            if (taskManager.getSubtaskById(subtask.getId()) != null) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.createSubtask(subtask);
            }
            System.out.println("Подзадача успешно создана");
            h.sendResponseHeaders(200, 0);
        } catch (NullPointerException e) {
            System.out.println("Ошибка при создании подзадачи");
            h.sendResponseHeaders(405, 0);
        }
    }

    public void addOrUpdateEpic(HttpExchange h) throws IOException {
        try {
            String value = readText(h);
            Type taskType = new TypeToken<Epic>() {}.getType();
            Epic epic = gson.fromJson(value, taskType);
            if (taskManager.getEpicById(epic.getId()) != null) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.createEpic(epic);
            }
            System.out.println("Эпик успешно создан");
            h.sendResponseHeaders(200, 0);
        } catch (NullPointerException e) {
            System.out.println("Ошибка при создании эпика");
            h.sendResponseHeaders(405, 0);
        }
    }

    public void deleteTask(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/task\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getTaskById(id) != null) {
                taskManager.removeTask(id);
                System.out.println("Задача с id = '" + id + "' успешно удалена");
                h.sendResponseHeaders(200,0);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void deleteSubtask(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/subtask\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getSubtaskById(id) != null) {
                taskManager.removeSubtask(id);
                System.out.println("Задача с id = '" + id + "' успешно удалена");
                h.sendResponseHeaders(200,0);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void deleteEpic(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/epic\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getEpicById(id) != null) {
                taskManager.removeEpic(id);
                System.out.println("Задача с id = '" + id + "' успешно удалена");
                h.sendResponseHeaders(200,0);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    public void getEpicSubtasks(HttpExchange h) throws IOException {
        String pathId = h.getRequestURI().toString().replaceFirst("/tasks/subtask/epic\\?id=","");
        int id = parsePathId(pathId);
        if (id != -1) {
            if (taskManager.getEpicById(id) != null) {
                String response = gson.toJson(taskManager.getEpicSubtasks(id));
                sendText(h, response);
            } else {
                System.out.println("Задачи по данному идентификатору не существует: " + id);
                h.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("Получен некорректный идентификатор задачи: " + id);
            h.sendResponseHeaders(405, 0);
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exc) {
            return -1;
        }
    }
}
