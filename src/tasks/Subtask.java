package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;
    private TaskType type;

    public Subtask(int id, String name, String description, TaskStatus status, TaskType type, int epicId) {
        super(id, name, description, status, type);
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
                && Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription().length() + "',\n" +
                "id=" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "epicId=" + epicId + "'\n" +
                '}';
    }
}
