package manager;

import tasks.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {

    public static String getHeader() {
        return "id,type,name,status,description,duration,epic";
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String toString(Task task) {
        return task.getId()
                + "," + task.getType()
                + "," + task.getName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + "," + (task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "")
                + "," + task.getDuration()
                + "," + (task.getEndTime() != null ? task.getEndTime().format(DATE_TIME_FORMATTER) : "")
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
        final LocalDateTime startTime = LocalDateTime.parse(values[5],DATE_TIME_FORMATTER);
        final long duration = Long.parseLong(values[6]);
        final LocalDateTime endTime = LocalDateTime.parse(values[7],DATE_TIME_FORMATTER);
        if (type == TaskType.TASK) {
            Task task = new Task(name, description, status, startTime, duration);
            task.setId(id);
            return task;
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[8]);
            Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);
            subtask.setId(id);
            return subtask;
        }
        Epic epic;
        if (values[5].equals("")) {
            epic = new Epic(name, description);
        } else {
            epic = new Epic(name, description, startTime, endTime);
        }
        epic.setId(id);
        return epic;
    }
}
