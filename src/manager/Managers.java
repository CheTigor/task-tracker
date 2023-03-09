package manager;

public abstract class Managers {

    public static HistoryManager inMemoryHistoryManager;

    public static TaskManager inMemoryTaskManager;

    public static HistoryManager getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager();
        }
        return inMemoryTaskManager;
    }
}
