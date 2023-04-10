package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest<T extends HistoryManager> {

    T historyManager;
    Task task;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void createTasks() {
        task = new Task(1, "TestTask", "TaskDescription", TaskStatus.NEW, TaskType.TASK, 200);
        epic = new Epic(2, "TestTask", "TaskDescription", TaskType.EPIC);
        subtask1 = new Subtask(3, "TestTask", "TaskDescription",
                TaskStatus.NEW,TaskType.SUBTASK, 200,  2);
        subtask2 = new Subtask(4, "TestTask", "TaskDescription",
                TaskStatus.NEW,TaskType.SUBTASK, 200, 2);
    }

    @Test
    void addTask() {
        historyManager.add(task);

        assertEquals(task, historyManager.getListOfHistory().get(0));
    }

    @Test
    void addEpic() {
        historyManager.add(epic);

        assertEquals(epic, historyManager.getListOfHistory().get(0));
    }

    @Test
    void addSubtask() {
        historyManager.add(subtask1);

        assertEquals(subtask1, historyManager.getListOfHistory().get(0));
    }

    //Проверка дублирования и максимального размера есть в методе getHistory интерфейса TaskManager
    @Test
    void getListOfHistory() {
        assertEquals(List.of(), historyManager.getListOfHistory()); //Я не уверен, что правильно делаю
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);
        historyManager.add(subtask2);

        assertEquals(4, historyManager.getListOfHistory().size());

        //При удалении Эпика удаляются subtasks в классе InMemoryTaskManager
        historyManager.remove(2);
        historyManager.remove(3);
        historyManager.remove(4);

        assertEquals(1, historyManager.getListOfHistory().size());
        assertEquals(task, historyManager.getListOfHistory().get(0));
    }
}