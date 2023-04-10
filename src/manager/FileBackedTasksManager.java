package manager;


import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(File file) {
        this.path = file.toPath();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(CSVTaskFormat.getHeader());
            writer.newLine();
            for (Task task : tasks.values()) {
                writer.write(CSVTaskFormat.toString(task));
                writer.newLine();
            }
            for (Task subtask : subtasks.values()) {
                writer.write(CSVTaskFormat.toString(subtask));
                writer.newLine();
            }
            for (Task epic : epics.values()) {
                writer.write(CSVTaskFormat.toString(epic));
                writer.newLine();
            }
            writer.newLine();
            writer.write(CSVTaskFormat.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Error on saving to file", e);
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        try {
            //file.toPath() - переводит File в Path
            final String csv = Files.readString(file.toPath());
            //lineSeparator("\n");
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            List<Integer> history = Collections.emptyList();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    history = CSVTaskFormat.historyFromString(lines[i + 1]);
                    break;
                }
                final Task task = CSVTaskFormat.taskFromString(line);
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
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read form file: " + file.getName(), e);
        }
        return taskManager;
    }

    private void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    protected Task findTask(Integer id) {
        final Task task = tasks.get(id);
        if (task != null) {
            return task;
        }
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }
        return epics.get(id);
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, int duration) {
        super.createTask(name, description, status, duration);
        save();
    }

    @Override
    public void createTask(String name, String description, TaskStatus status, int duration, LocalDateTime startTime) {
        super.createTask(name, description, status, duration, startTime);
        save();
    }

    @Override
    public void createSubtask(String name, String description, TaskStatus status, int duration, int epicId) {
        super.createSubtask(name, description, status, duration, epicId);
        save();
    }

    @Override
    public void createSubtask(String name, String description, TaskStatus status,
                              int duration, LocalDateTime startTime, int epicId) {
        super.createSubtask(name, description, status, duration, startTime, epicId);
        save();
    }

    @Override
    public void createEpic(String name, String description) {
        super.createEpic(name, description);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }
}

