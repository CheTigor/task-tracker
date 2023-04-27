package manager;

import java.time.LocalDateTime;
import java.util.*;

import manager.exceptions.OverlapsException;
import manager.history.HistoryManager;
import tasks.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getTasks() {
        for (Task task: tasks.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        for (Task task: subtasks.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        for (Task task: epics.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer taskId : subtasks.keySet()) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(subtasks.get(taskId));
        }
        for (Epic epic: epics.values()) {
            epic.cleanSubtasksId();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            for (Integer subtaskId : epics.get(epicId).getSubtasksId()) {
                historyManager.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
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
    public int createTask(Task task) {
        task.setId(nextId++);
        addSortedTask(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return 0;
        }
        subtask.setId(nextId++);
        addSortedTask(subtask);
        epic.getSubtasksId().add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicDuration(epic);
        updateEpicStatus(epic);
        return subtask.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        prioritizedTasks.remove(savedTask);
        addSortedTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        final Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        prioritizedTasks.remove(savedSubtask);
        addSortedTask(subtask);
        updateEpicDuration(epic);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
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
        prioritizedTasks.remove(task);
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        final Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().remove((Integer)subtask.getId()); //Удаляем как объект, а не индекс
        prioritizedTasks.remove(subtask);
        updateEpicDuration(epic);
        updateEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) {
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtasks.remove(subtaskId));
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
        if (prioritizedTasks.isEmpty()) {
            return null;
        }
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTaskOverlaps(Task task1, Task task2) {
        if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return task1.getStartTime().isBefore(task2.getEndTime());
        } else {
            return task1.getEndTime().isAfter(task2.getStartTime());
        }
    }

    protected void addSortedTask(Task task) {
        if (prioritizedTasks.isEmpty()) {
            prioritizedTasks.add(task);
        } else {
            for (Task value : prioritizedTasks) {
                if (isTaskOverlaps(task, value)) {
                    throw new OverlapsException("Обнаружено пересечение по времени");
                }
            }
            prioritizedTasks.add(task);
        }
    }

    private void updateEpicDuration(Epic epic) {
        List<Integer> subs = epic.getSubtasksId();
        if (subs.isEmpty()) {
            epic.setDuration(0L);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getDuration();
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    private void updateEpicStatus(Epic epic) {
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

    protected void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        updateEpicStatus(epic);
        updateEpicDuration(epic);
    }
}

