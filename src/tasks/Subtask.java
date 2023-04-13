package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status,
                   LocalDateTime startTime, long duration, int epicId) {
        super(name, description, status, startTime, duration);
        setType(TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String getEpicIdToString() {
        return String.valueOf(epicId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(getName(), subtask.getName())
                && Objects.equals(getDescription(), subtask.getDescription())
                && Objects.equals(getId(), subtask.getId())
                && Objects.equals(getStatus(), subtask.getStatus())
                && Objects.equals(epicId, subtask.epicId)
                && Objects.equals(getType(), subtask.getType())
                && Objects.equals(getDuration(), subtask.getDuration())
                && Objects.equals(getStartTime(), subtask.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription() + "',\n" +
                "id='" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "startTime='" + getStartTime() + "'\n" +
                "duration='" + getDuration() + "',\n" +
                "epicId='" + epicId + "'\n" +
                '}';
    }
}
