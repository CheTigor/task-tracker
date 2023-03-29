package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void createTask(String name, String description, TaskStatus status);

    void createSubtask(String name, String description, TaskStatus status, int epicId);

    void createEpic(String name, String description);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
