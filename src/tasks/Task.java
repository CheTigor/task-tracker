package tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private final int id;
    private TaskStatus status;
    private final TaskType type;

    public Task(int id, String name, String description, TaskStatus status, TaskType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    //Второй конструктор для класса Epic, так как он не должен сам назначать себе статус
    public Task(int id, String name, String description, TaskType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getName(), task.getName())
                && Objects.equals(getDescription(), task.getDescription())
                && Objects.equals(getId(), task.getId())
                && Objects.equals(getStatus(), task.getStatus());
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
                "status='" + getStatus() + "'\n" +
                '}';
    }
}
