package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    public ArrayList<Task> getTasks();

    public ArrayList<Subtask> getSubtasks();

    public ArrayList<Epic> getEpics();

    public void removeAllTasks();

    public void removeAllSubtasks();

    public void removeAllEpics();

    public Task getTaskById(int id);

    public Subtask getSubtaskById(int id);

    public Epic getEpicById(int id);

    public int createTask(Task task);

    public Integer createSubtask(Subtask subtask);

    public int createEpic(Epic epic);

    public void updateTask(Task task);

    public void updateSubtask(Subtask subtask);

    public void updateEpic(Epic epic);

    public void removeTask(int id);

    public void removeSubtask(int id);

    public void removeEpic(int id);

    public ArrayList<Subtask> getEpicSubtasks(int epicId);
}
