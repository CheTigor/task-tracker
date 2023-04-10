package manager;

import java.io.File;

//C:/Users/chetv/dev/java-kanban/resources/task.cvs

public abstract class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("resources/task.cvs"));
    }
}
