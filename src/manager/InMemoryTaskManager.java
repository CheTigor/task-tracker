package manager;

import java.time.LocalDateTime;
import java.util.*;

import tasks.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final List<Task> sortedTasksByTime = new ArrayList<>();

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
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            sortedTasksByTime.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer taskId : subtasks.keySet()) {
            historyManager.remove(taskId);
            sortedTasksByTime.remove(tasks.get(taskId));
        }
        for (Epic epic: epics.values()) {
            epic.cleanSubtasksId();
            checkEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                historyManager.remove(subtaskId);
                sortedTasksByTime.remove(tasks.get(subtaskId));
            }
            historyManager.remove(epicId);
        }
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

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, int duration) {
        Task task = new Task(nextId++, name, description, status, TaskType.TASK, duration);
        sortedTasksByTimeAdd(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, int duration, LocalDateTime startTime) {
        Task task = new Task(nextId++, name, description, status, TaskType.TASK, duration, startTime);
        sortedTasksByTimeAdd(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createSubtask(String name, String description, TaskStatus status, int epicId, int duration) {
        Subtask subtask = new Subtask(nextId++, name, description, status, TaskType.SUBTASK, epicId, duration);
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        sortedTasksByTimeAdd(subtask);
        epic.getSubtasksId().add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        setEpicStartAndEndTime(epic);
        checkEpicStatus(epic);
    }

    @Override
    public void createSubtask(String name, String description, TaskStatus status,
                              int duration, LocalDateTime startTime, int epicId) {
        Subtask subtask = new Subtask(nextId++, name, description, status, TaskType.SUBTASK,
                duration, startTime, epicId);
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        sortedTasksByTimeAdd(subtask);
        epic.getSubtasksId().add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        setEpicStartAndEndTime(epic);
        checkEpicStatus(epic);
    }

    @Override
    public void createEpic(String name, String description) {
        Epic epic = new Epic(nextId++, name, description, TaskType.EPIC);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        sortedTasksByTime.remove(savedTask);
        sortedTasksByTimeAdd(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        sortedTasksByTime.remove(savedSubtask);
        sortedTasksByTimeAdd(subtask);
        setEpicStartAndEndTime(epic);
        subtasks.put(subtask.getId(), subtask);
        checkEpicStatus(epic);
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
        final Task task = tasks.remove(id);
        historyManager.remove(id);
        sortedTasksByTime.remove(task);
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        final Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().remove(subtask.getId());
        sortedTasksByTime.remove(subtask);
        setEpicStartAndEndTime(epic);
        checkEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) {
            historyManager.remove(subtaskId);
            sortedTasksByTime.remove(subtasks.remove(subtaskId));
        }
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksId()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getListOfHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (sortedTasksByTime.isEmpty()) {
            return null;
        }
        sortedTasksByTime.sort((task1, task2) -> {
            if (task1.getStartTime() == null || task2.getStartTime() == null)
                return 0;
            return task1.getStartTime().compareTo(task2.getStartTime());
        });
        return sortedTasksByTime;
    }

    private boolean checkOverlaps(Task task1, Task task2) {
        if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return task1.getStartTime().isBefore(task2.getEndTime());
        } else {
            return task1.getEndTime().isAfter(task2.getStartTime());
        }
    }

    private void sortedTasksByTimeAdd(Task task) {
        if (sortedTasksByTime.isEmpty()) {
            sortedTasksByTime.add(task);
        } else {
            for (Task value : sortedTasksByTime) {
                if (checkOverlaps(task, value)) {
                    throw new OverlapsException("Обнаружено пересечение по времени");
                }
            }
            sortedTasksByTime.add(task);
        }
    }

    private void setEpicStartAndEndTime(Epic epic) {
        if (epic == null) {
            return;
        } else if (epic.getSubtasksId().isEmpty()) {
            epic.setStartTime(null);
            return;
        }
        LocalDateTime startTime = subtasks.get(epic.getSubtasksId().get(0)).getStartTime();
        LocalDateTime endTime = subtasks.get(epic.getSubtasksId().get(0)).getEndTime();
        for (int i = 1; i < epic.getSubtasksId().size(); i++ ) {
            LocalDateTime subtaskST = subtasks.get(epic.getSubtasksId().get(i)).getStartTime();
            LocalDateTime subtaskET = subtasks.get(epic.getSubtasksId().get(i)).getEndTime();
            if (startTime.isAfter(subtaskST)) {
                startTime = subtaskST;
            } else if (endTime.isBefore(subtaskET)) {
                endTime = subtaskET;
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    private void checkEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
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

