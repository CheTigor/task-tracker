package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic: epics.values()) {
            epic.cleanSubtasksId();
            checkEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public int createTask(Task task) {
        final int id = nextId++;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        final int id = nextId++;
        subtask.setId(id);
        epics.get(subtask.getEpicId()).getSubtasksId().add(subtask.getId());
        subtasks.put(id, subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        final int id = nextId++;
        epic.setId(id);
        epics.put(id, epic);
        checkEpicStatus(epic);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        final int epicId = subtask.getEpicId();
        epics.get(epicId).getSubtasksId().remove(subtask.getId());
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksId()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void checkEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        if (epic.getSubtasksId().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtasksId : epic.getSubtasksId()) {
            epicSubtasks.add(subtasks.get(subtasksId));
        }
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus().equals(TaskStatus.DONE)) {
                countDone++;
            } else if (subtask.getStatus().equals(TaskStatus.NEW)) {
                countNew++;
            }
        }
        if (countNew == epic.getSubtasksId().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone == epic.getSubtasksId().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}

