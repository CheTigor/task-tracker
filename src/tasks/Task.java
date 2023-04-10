package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private final int id;
    private TaskStatus status;
    private final TaskType type;
    private long duration;
    private LocalDateTime startTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Task(int id, String name, String description, TaskStatus status, TaskType type, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        if (duration <= 0) {
            throw new IllegalArgumentException("duration can't be <= 0");
        } else {
            this.duration = duration;
        }
        startTime = LocalDateTime.now();
    }

    public Task(int id, String name, String description, TaskStatus status, TaskType type,
                long duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        if (duration <= 0) {
            throw new IllegalArgumentException("duration can't be <= 0");
        } else {
            this.duration = duration;
        }
        this.startTime = startTime;
    }

    //Второй конструктор для класса Epic, так как он не должен сам назначать себе статус
    public Task(int id, String name, String description, TaskType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NEW;
    }

    //Метод не нужен в данном кассе, но наследуется другими
    public String getEpicIdToString() {
        return null;
    }

    //Метод не нужен в данном кассе, но наследуется другими
    public String getSubtasksIdtoString() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(Duration.ofMinutes(duration));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getName(), task.getName())
                && Objects.equals(getDescription(), task.getDescription())
                && Objects.equals(getId(), task.getId())
                && Objects.equals(getStatus(), task.getStatus())
                && Objects.equals(getType(), task.getType())
                && Objects.equals(getDuration(), task.getDuration())
                && Objects.equals(getStartTime(), task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus());
    }

    @Override
    public String toString() {
        return "Task{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription().length() + "',\n" +
                "id='" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "startTime='" + getStartTime().format(DATE_TIME_FORMATTER)+ "'\n" +
                "duration='" + getDuration() + "'\n" +
                '}';
    }
}
