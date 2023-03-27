package manager;


import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
        read();
    }

    private void save() {
        try (BufferedWriter writer = getWriter()) {
            writer.write(Dto.HEADER);
            writer.write("\n");
            for (Task task : tasks.values()) {
                writer.write(Dto.cons(task).asString());
                writer.write("\n");
            }
            for (Task subtask : subtasks.values()) {
                writer.write(Dto.cons(subtask).asString());
                writer.write("\n");
            }
            for (Task epic : epics.values()) {
                writer.write(Dto.cons(epic).asString());
                writer.write("\n");
            }
            writer.write("\n");
            writer.write(historyIdsToString());
        } catch (IOException e) {
            throw new ManagerSaveException("Error on saving to file", e);
        }
    }

    private BufferedWriter getWriter() throws IOException {
        return new BufferedWriter(new FileWriter(path.toFile()));
    }

    private BufferedReader getReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(path.toFile()));
    }

    private String historyIdsToString() {
        StringBuilder ids = new StringBuilder();
        HistoryManager history = Managers.getDefaultHistory();
        for (Integer id : ((InMemoryHistoryManager)history).getHistory().keySet()) {
            ids.append(id);
            ids.append(",");
        }
        if (ids.length() != 0) {
            ids.deleteCharAt(ids.length() - 1);
        }
        return ids.toString();
    }

    public void read() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = getReader()) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        for (int i = 1; i < lines.size() - 1; i++) {
            if (!lines.get(i).isBlank()) {
                String[] elements = lines.get(i).split(",");
                switch (elements[1]) {
                    case ("TASK"):
                        readTask(elements);
                        break;
                    case ("SUBTASK"):
                        readSubtask(elements);
                        break;
                    case ("EPIC"):
                        readEpic(elements);
                        break;
                }
            }
        }
        String[] historyIds = lines.get(lines.size() - 1).split(",");
        for (String str : historyIds) {
            int id = Integer.parseInt(str);
            if (tasks.get(id) != null) {
                historyManager.add(tasks.get(id));
            } else if (subtasks.get(id) != null) {
                historyManager.add(subtasks.get(id));
            } else if (epics.get(id) != null) {
                historyManager.add(epics.get(id));
            }
        }
    }

    private void readTask(String[] elements) {
        Task task = new Task(elements[2], elements[4], chooseStatus(elements[3]));
        task.setId(Integer.parseInt(elements[0]));
        tasks.put(task.getId(), task);
    }

    private void readSubtask(String[] elements) {
        Subtask subtask = new Subtask(elements[2], elements[4], chooseStatus(elements[3]), Integer.parseInt(elements[5]));
        subtask.setId(Integer.parseInt(elements[0]));
        subtasks.put(subtask.getId(), subtask);
    }

    private void readEpic(String[] elements) {
        Epic epic = new Epic(elements[2], elements[4]);
        epic.setStatus(chooseStatus(elements[3]));
        epic.setSubtasksId(readSubtasksIds(elements[5]));
        epics.put(epic.getId(), epic);
    }

    private TaskStatus chooseStatus(String string) {
        TaskStatus status;
        switch (string) {
            case ("IN_PROGRESS"):
                status = TaskStatus.IN_PROGRESS;
                break;
            case ("DONE"):
                status = TaskStatus.DONE;
                break;
            default:
                status = TaskStatus.NEW;
                break;
        }
        return status;
    }

    private List<Integer> readSubtasksIds(String line) {
        List<Integer> subtasksIds = new ArrayList<>();
        String[] elements = line.split("\\.");
        for (String element : elements) {
            subtasksIds.add(Integer.parseInt(element));
        }
        return subtasksIds;
    }



    /*public void fromString() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = getReader()) {
            while (br.ready()) {
                lines.add(br.readLine());
            }

        } catch (IOException e) {

        }
        for (int i = 1; i < lines.size(); i++) {
            String[] elements = lines.get(i).split(",");
            if (elements[1].equals("Task")) {
                if (elements[3].equals("new")) {
                    TaskStatus ts = TaskStatus.NEW;
                }
                Task task = new Task(elements[2], elements[4], );
                tasks.put(new Task())
            }
        }
    }*/

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
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
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
}

