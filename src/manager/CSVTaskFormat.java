package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) {
        return task.getId()
                + "," + task.getType()
                + "," + task.getName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + (task.getType().equals(TaskType.SUBTASK) ? task.getEpicIdToString() : "");
    }

    public static String historyToString(HistoryManager historyManager) {
        final List<Task> history = historyManager.getListOfHistory();
        if (history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < history.size(); i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String line) {
        final String[] values = line.split(",");
        final List<Integer> historyIds = new ArrayList<>();
        for (String value : values) {
            historyIds.add(Integer.parseInt(value));
        }
        return historyIds;
    }

    public static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final TaskStatus status = TaskStatus.valueOf(values[3]);
        final String description = values[4];
        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, type);
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[5]);
            return new Subtask(id, name, description, status, type, epicId);
        }
        return new Epic(id, name, description, type);
    }
}
