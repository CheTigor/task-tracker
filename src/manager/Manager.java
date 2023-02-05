package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Manager {
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
        this.tasks.clear();
    }

    /*public void removeAllSubtasks() {
        for (Epic epic: epics.values()) {
            epic.cleanSubtasksId();
            checkEpicStatus(epic);
        }
        subtasks.clear();
    }*/

    public void removeAllSubtasks() {
        for (Epic epic: epics.values()) {
            epic.cleanSubtasks();
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
        task.setId(nextId);
        tasks.put(nextId, task);
        return nextId++;
    }

    /*public Integer createSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        subtask.setId(nextId);
        (epics.get(subtask.getEpicId())).getSubtasksId().add(subtask.getId());
        subtasks.put(nextId, subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        return nextId++;
    }*/

    public Integer createSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        subtask.setId(nextId);
        (epics.get(subtask.getEpicId())).getSubtasks().add(subtask);
        subtasks.put(nextId, subtask);
        checkEpicStatus(epics.get(subtask.getEpicId()));
        return nextId++;
    }

    public int createEpic(Epic epic) {
        epic.setId(nextId);
        epics.put(nextId, epic);
        checkEpicStatus(epic);
        return nextId++;
    }

    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    /*public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            int epicIdOld = subtasks.get(subtask.getId()).getEpicId();
            int epicIdNew = subtask.getEpicId();
            int subId = subtask.getId();
            //int indexOfSubtasks = epics.get(epicId).getSubtasks().indexOf(subtasks.get(subtask.getId()));
            epics.get(epicIdOld).getSubtasksId().remove(subId);
            checkEpicStatus(epics.get(epicIdOld));
            subtasks.remove(subId);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicIdNew).getSubtasksId().add(subId);
            checkEpicStatus(epics.get(epicIdNew));
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }
    }*/

    public void updateSubtask(Subtask subtask) {
        final int epicIdOld = subtasks.get(subtask.getId()).getEpicId();
        final int epicIdNew = subtask.getEpicId();
        final int subId = subtask.getId();
        final Subtask savedSubtask = subtasks.get(subId);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicIdNew);
        if (epic == null) {
            return;
        }
        int indexOfSubtasks = epics.get(epicIdOld).getSubtasks().indexOf(subtasks.get(subtask.getId()));
        epics.get(epicIdOld).getSubtasks().remove(indexOfSubtasks);
        checkEpicStatus(epics.get(epicIdOld));
        subtasks.put(subId, subtask);
        epics.get(epicIdNew).getSubtasks().add(subtask);
        checkEpicStatus(epics.get(epicIdNew));
    }

    /*public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            for (Integer subId : epics.get(epic.getId()).getSubtasksId()) {
                subtasks.remove(subId);
            }
            epics.put(epic.getId(), epic);
            for (Integer subId : epics.get(epic.getId()).getSubtasksId()) {
                subtasks.put(subId, epics.get(epic.getSubtasksId()));
            }
            checkEpicStatus(epic);
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }
    }*/

    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        for (Subtask subtask : epics.get(epic.getId()).getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epics.put(epic.getId(), epic);
        for (Subtask subtask : epics.get(epic.getId()).getSubtasks()) {
            subtasks.put(subtask.getId(), subtask);
        }
        checkEpicStatus(epic);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        int epicId = subtask.getEpicId();
        int indexOfSubtasks = epics.get(epicId).getSubtasks().indexOf(subtask);
        epics.get(epicId).getSubtasks().remove(indexOfSubtasks);
        checkEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        return epic.getSubtasks();
    }

    private void checkEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        if (epic.getSubtasks().size() == 0) {
            epic.setStatus("NEW");
            return;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus().equals("DONE")) {
                countDone++;
            } else if (subtask.getStatus().equals("NEW")) {
                countNew++;
            }
            if (countNew == epic.getSubtasks().size()) {
                epic.setStatus("NEW");
            } else if (countDone == epic.getSubtasks().size()) {
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
    }
}
