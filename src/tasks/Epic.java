package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList();

    public ArrayList<Subtask> getSubtasks() {
        return this.subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public String toString() {
        String var10000 = this.getName();
        return "Tasks.Epic{name='" + var10000 + "', description='" + this.getDescription().length() + "', id='" + this.getId() + "', status='" + this.getStatus() + "', subtasks=" + this.subtasks + "'}";
    }
}
