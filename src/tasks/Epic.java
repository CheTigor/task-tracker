package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList();
    //private ArrayList<Integer> subtasksId = new ArrayList();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    /*public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void cleanSubtasksId() {
        subtasksId.clear();
    }*/

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void cleanSubtasks() {
        subtasks.clear();
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
                && Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" + "\n" +
                "name='" + getName() + "',\n" +
                "description='" + getDescription().length() + "',\n" +
                "id=" + getId() + "',\n" +
                "status='" + getStatus() + "',\n" +
                "subtasks=" + subtasks.size() + "'\n" +
                '}';
    }
}
