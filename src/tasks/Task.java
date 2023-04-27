package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private TaskType type;
    private long duration;
    private LocalDateTime startTime;

    //Конструктор для отложенной задачи
    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        if (duration <= 0) {
            throw new IllegalArgumentException("duration can't be <= 0");
        } else {
            this.duration = duration;
        }
        this.startTime = startTime;
        this.type = TaskType.TASK;
    }

    //Третий конструктор для класса Epic, так как он не должен сам назначать себе статус
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setType(TaskType type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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
                "description='" + getDescription() + "',\n" +
                "id='" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "startTime='" + getStartTime()+ "'\n" +
                "duration='" + getDuration() + "'\n" +
                '}';
    }
}
