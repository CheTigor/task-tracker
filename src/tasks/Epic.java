package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        setType(TaskType.EPIC);
        setStatus(TaskStatus.NEW);
    }

    public Epic(String name, String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(name, description);
        setType(TaskType.EPIC);
        setStatus(TaskStatus.NEW);
        setStartTime(startTime);
        setEndTime(endTime);
        setDuration(Duration.between(startTime, endTime).toMinutes());
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void cleanSubtasksId() {
        subtasksId.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getName(), epic.getName())
                && Objects.equals(getDescription(), epic.getDescription())
                && Objects.equals(getId(), epic.getId())
                && Objects.equals(getStatus(), epic.getStatus())
                && Objects.equals(subtasksId, epic.subtasksId)
                && Objects.equals(getType(), epic.getType())
                && Objects.equals(getDuration(), epic.getDuration())
                && Objects.equals(getStartTime(), epic.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), subtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription() + "',\n" +
                "id=" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "startTime='" + getStartTime() + "'\n" +
                "duration='" + getDuration() + "'\n" +
                "subtasks=" + subtasksId.size() + "'\n" +
                '}';
    }
}
