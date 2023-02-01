package tasks;

public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public String toString() {
        String var10000 = this.getName();
        return "Tasks.Task{name='" + var10000 + "', description='" + this.getDescription().length() + "', id='" + this.getId() + "', status='" + this.getStatus() + "', epicId='" + this.epicId + "}";
    }
}
