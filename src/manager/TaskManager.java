package manager;

import java.util.ArrayList;
import java.util.HashMap;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap();
    private HashMap<Integer, Subtask> subtasks = new HashMap();
    private HashMap<Integer, Epic> epics = new HashMap();


    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic: epics.values()) {
            epic.cleanSubtasksId();
            checkEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public int createTask(Task task) {
        final int id = ++nextId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public Integer createSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        final int id = ++nextId;
        subtask.setId(id);
        epics.get(subtask.getEpicId()).getSubtasksId().add(subtask.getId());
        subtasks.put(id, subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        return id;
    }

    public int createEpic(Epic epic) {
        final int id = ++nextId;
        epic.setId(id);
        epics.put(id, epic);
        checkEpicStatus(epic);
        return id;
    }

    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        final int epicId = subtask.getEpicId();
        epics.get(epicId).getSubtasksId().remove(subtask.getId());
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
    }

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

