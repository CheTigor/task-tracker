package manager;

public class Managers {
    private static InMemoryTaskManager inMemoryTaskManager;
    private static InMemoryHistoryManager inMemoryHistoryManager;
    public static HistoryManager getDefaultHistory(Integer number) {
        if (number == 1) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
            return inMemoryHistoryManager;
        } else {
            return null;
        }
    }

    public static TaskManager getDefault(Integer number) {
        if (number == 1) {
            inMemoryTaskManager = new InMemoryTaskManager();
            return inMemoryTaskManager;
        } else {
            return null;
        }
    }
}
