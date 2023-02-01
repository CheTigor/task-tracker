package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Manager {
    private int nextId = 1;
    public HashMap<Integer, Task> tasks = new HashMap();
    public HashMap<Integer, Subtask> subtasks = new HashMap();
    public HashMap<Integer, Epic> epics = new HashMap();


    public HashMap<Integer, Task> getListOfTasks() {
        return this.tasks;
    }

    public HashMap<Integer, Subtask> getListOfSubtasks() {
        return this.subtasks;
    }

    public HashMap<Integer, Epic> getListOfEpics() {
        return this.epics;
    }

    public void removeAllTasks() {
        this.tasks.clear();
    }

    public void removeAllSubtasks() {
        this.subtasks.clear();
    }

    public void removeAllEpics() {
        this.epics.clear();
    }

    public Task getTaskById(int id) {
        return this.tasks.containsKey(id) ? (Task)this.tasks.get(id) : null;
    }

    public Subtask getSubtaskById(int id) {
        return this.subtasks.containsKey(id) ? (Subtask)this.subtasks.get(id) : null;
    }

    public Epic getEpicById(int id) {
        return this.epics.containsKey(id) ? (Epic)this.epics.get(id) : null;
    }

    public void createTask(Task task) {
        task.setId(this.nextId);
        this.tasks.put(this.nextId++, task);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(this.nextId);
        ((Epic)this.epics.get(subtask.getEpicId())).getSubtasks().add(subtask);
        this.subtasks.put(this.nextId++, subtask);
        this.checkEpicStatus((Epic)this.epics.get(subtask.getEpicId()));
    }

    public void createEpic(Epic epic) {
        epic.setId(this.nextId);
        epic.setStatus("NEW");
        this.epics.put(this.nextId++, epic);
    }

    public void updateTask(Task task) {
        if (this.tasks.containsKey(task.getId())) {
            this.tasks.put(task.getId(), task);
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    public void updateSubtask(Subtask subtask) {
        if (this.subtasks.containsKey(subtask.getId())) {
            int epicId = subtask.getEpicId();
            int id = subtask.getId();
            ArrayList<Subtask> epicSubtasks = ((Epic)this.epics.get(epicId)).getSubtasks();
            int indexOfSubtasks = epicSubtasks.indexOf(this.subtasks.get(subtask.getId()));
            this.subtasks.remove(id);
            epicSubtasks.remove(indexOfSubtasks);
            this.checkEpicStatus((Epic)this.epics.get(subtask.getEpicId()));
            this.subtasks.put(subtask.getId(), subtask);
            epicSubtasks.add(subtask);
            this.checkEpicStatus((Epic)this.epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    public void updateEpic(Epic epic) {
        if (this.epics.containsKey(epic.getId())) {
            Iterator var2 = ((Epic)this.epics.get(epic.getId())).getSubtasks().iterator();

            Subtask subtask;
            while(var2.hasNext()) {
                subtask = (Subtask)var2.next();
                this.subtasks.remove(subtask.getId());
            }

            var2 = epic.getSubtasks().iterator();

            while(var2.hasNext()) {
                subtask = (Subtask)var2.next();
                this.subtasks.put(subtask.getId(), subtask);
            }

            this.epics.put(epic.getId(), epic);
            this.checkEpicStatus(epic);
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    private void checkEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        Iterator var4 = epic.getSubtasks().iterator();

        while(var4.hasNext()) {
            Subtask subtask = (Subtask)var4.next();
            if (subtask.getStatus().equals("DONE")) {
                ++countDone;
            } else if (subtask.getStatus().equals("NEW")) {
                ++countNew;
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

    public void removeTask(Task task) {
        if (this.tasks.containsKey(task.getId())) {
            this.tasks.remove(task.getId());
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    public void removeSubtask(Subtask subtask) {
        if (this.subtasks.containsKey(subtask.getId())) {
            int epicId = subtask.getEpicId();
            int id = subtask.getId();
            ArrayList<Subtask> epicSubtasks = ((Epic)this.epics.get(epicId)).getSubtasks();
            int indexOfSubtasks = epicSubtasks.indexOf(this.subtasks.get(subtask.getId()));
            epicSubtasks.remove(indexOfSubtasks);
            this.subtasks.remove(id);
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    public void removeEpic(Epic epic) {
        if (this.epics.containsKey(epic.getId())) {
            this.epics.remove(epic.getId());
        } else {
            System.out.println("Нет задачи с таким идентификатором");
        }

    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }
}
