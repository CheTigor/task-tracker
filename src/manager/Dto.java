package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

public class Dto {
    public static final String HEADER = "id,type,name,status,description,epic";
    private final String id;
    private final String type;
    private final String name;
    private final String status;
    private final String description;
    private final String linkIds;  // not null for epics

    public Dto(Task task) {
        this.id = task.getId().toString();
        this.name = task.getName();
        this.status = task.getStatus().toString();
        this.description = task.getDescription();
        if (task instanceof Epic) {
            this.linkIds = ((Epic) task).getSubtasksIdtoString();
            this.type = TaskType.EPIC.toString();
        } else if (task instanceof Subtask) {
            this.linkIds = String.valueOf(((Subtask)task).getEpicId());
            this.type = TaskType.SUBTASK.toString();
        } else {
            this.linkIds = null;
            this.type = TaskType.TASK.toString();
        }
    }

    public static Dto cons(Task task) {
        return new Dto(task);
    }

    public String asString() {
        return String.format(
                "%s,%s,%s,%s,%s,%s",
                id,
                type,
                name,
                status,
                description,
                linkIds != null ? linkIds : ""
        );
    }
}
