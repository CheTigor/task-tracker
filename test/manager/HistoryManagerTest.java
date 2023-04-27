package manager;

import manager.history.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
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
        task = new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 0, 0, 0), 60);
        task.setId(1);
        epic = new Epic("TestTask", "TaskDescription");
        epic.setId(2);
        subtask1 = new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 1, 0, 0),  60, 2);
        subtask1.setId(3);
        subtask2 = new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 2, 0, 0), 60, 2);
        subtask2.setId(4);
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