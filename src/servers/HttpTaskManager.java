package servers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.file.FileBackedTasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final URI uriKVServer;
    private final KVTaskClient client;
    private final Gson gson;
    private final String key;

    public HttpTaskManager(URI uri) {
        uriKVServer = uri;
        client = new KVTaskClient(uriKVServer);
        this.gson = Managers.getGson();
        key = "httptaskmanager";
    }

    @Override
    protected void save() {
        StringBuilder value = new StringBuilder();
        for (Task task : tasks.values()) {
            value.append(gson.toJson(task)).append("\n");
        }
        for (Task subtask : subtasks.values()) {
            value.append(gson.toJson(subtask)).append("\n");
        }
        for (Task epic : epics.values()) {
            value.append(gson.toJson(epic)).append("\n");
        }
        List<Task> history = historyManager.getListOfHistory();
        if (!history.isEmpty()) {
            int[] ids = new int[history.size()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = history.get(i).getId();
            }
            value.append(gson.toJson(ids));
        }
        client.put(key, value.toString());
    }

    public static HttpTaskManager load(URI uri, String key) {
        final HttpTaskManager taskManager = new HttpTaskManager(uri);
        final String value = taskManager.client.load(key);
        final String[] lines = value.split(System.lineSeparator());
        int generatorId = 0;
        List<Integer> history = Collections.emptyList();
        for (String line : lines) {
            if (line.contains("history")) {
                Type historyType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                history = taskManager.gson.fromJson(line, historyType);
                break;
            }
            final Task task = taskManager.gson.fromJson(line, Task.class);
            final int id = task.getId();
            if (id > generatorId) {
                generatorId = id;
            }
            taskManager.addAnyTask(task);
        }
        for (Map.Entry<Integer, Subtask> e : taskManager.subtasks.entrySet()) {
            final Subtask subtask = e.getValue();
            final Epic epic = taskManager.epics.get(subtask.getEpicId());
            epic.getSubtasksId().add(subtask.getId());
        }
        for (Integer taskId : history) {
            taskManager.historyManager.add(taskManager.findTask(taskId));
        }
        taskManager.nextId = generatorId + 1;
        return taskManager;
    }
}
