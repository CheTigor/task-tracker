package manager;

import java.nio.file.Path;

public abstract class Managers {

    public static HistoryManager inMemoryHistoryManager;

    public static TaskManager inMemoryTaskManager;

    public static FileBackedTasksManager fileBackedTasksManager;

    //Такая реализация утилитарного класса сделана, чтобы при повторной вызове
    //этой функции не создавался новый класс, а использовался уже существующий
    public static HistoryManager getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            TaskManager inMemoryTaskManager = new InMemoryTaskManager();
        }
        return inMemoryTaskManager;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        if (inMemoryTaskManager == null) {
            fileBackedTasksManager = new FileBackedTasksManager(path);
        }
        return fileBackedTasksManager;
    }
}
