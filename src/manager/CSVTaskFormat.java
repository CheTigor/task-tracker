package manager;

import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static String getHeader() {
        return "id,type,name,status,description,duration,epic";
    }

    public static String toString(Task task) {
        return task.getId()
                + "," + task.getType()
                + "," + task.getName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + (task.getStartTime() != null ? task.getStartTime().format(Task.DATE_TIME_FORMATTER) : "")
                + "," + task.getDuration()
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
        final long duration = Long.parseLong(values[6]);
        if (type == TaskType.TASK) {
            Task task = new Task(id, name, description, status, type, duration);
            task.setStartTime(LocalDateTime.parse(values[5],Task.DATE_TIME_FORMATTER));
            return task;
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[7]);
            Subtask subtask = new Subtask(id, name, description, status, type, duration, epicId);
            subtask.setStartTime(LocalDateTime.parse(values[5],Task.DATE_TIME_FORMATTER));
            return subtask;
        }
        Epic epic = new Epic(id, name, description, type);
        if ((values[5].equals(""))) {
            epic.setStartTime(null);
        } else {
            epic.setStartTime(LocalDateTime.parse(values[5], Task.DATE_TIME_FORMATTER));
        }
        return epic;
    }
}
