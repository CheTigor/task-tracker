package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;

    public Epic(int id, String name, String description, TaskType type) {
        super(id, name, description, type);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void cleanSubtasksId() {
        subtasksId.clear();
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
                && Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), subtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription().length() + "',\n" +
                "id=" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "subtasks=" + subtasksId.size() + "'\n" +
                '}';
    }
}
