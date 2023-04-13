package manager;

import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    public void createTasksForHistory() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,0,0,0), 60));
        taskManager.createEpic(new Epic ("TestTask", "TaskDescription"));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,1,0,0),
                60, 2));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,2,0,0),
                60, 2));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,3,0,0),
                60, 2));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,4,0,0),
                60, 2));
    }

    public void createEpicWithSubtasks() {
        taskManager.createEpic(new Epic("TestEpic", "TaskDescription"));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,0,0,0),
                60, 1));
        taskManager.createSubtask(new Subtask ("TestTask", "TaskDescription",
                TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,1,0,0),
                60, 1));
    }


    @Test
    void getTasks() {
        assertTrue(taskManager.getTasks().isEmpty(), "Список должен быть пуст");

        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,0,0,0),60));
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,1,0,0), 60));

        final Task task = taskManager.getTaskById(1);
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void getSubtasks() {
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список должен быть пуст");

        createEpicWithSubtasks();
        final Subtask subtask = taskManager.getSubtaskById(2);
        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются");
        assertEquals(2, subtasks.size(), "Неверное количество задач");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void getEpics() {
        assertTrue(taskManager.getEpics().isEmpty(), "Список должен быть пуст");

        taskManager.createEpic(new Epic("TestEpic", "TaskDescription"));
        taskManager.createEpic(new Epic("TestEpic", "TaskDescription"));
        final Epic epic = taskManager.getEpicById(1);
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

    }

    @Test
    void removeAllTasks() {
        taskManager.removeAllTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Список должен быть пуст");

        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0),60));
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,1,0,0),60));
        taskManager.removeAllTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Список должен быть пуст");
    }

    @Test
    void removeAllSubtasks() {
        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Список должен быть пуст");

        createEpicWithSubtasks();
        taskManager.removeAllSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Список должен быть пуст");
        assertTrue(taskManager.getEpicSubtasks(1).isEmpty(), "Список должен быть пуст");
    }

    @Test
    void removeAllEpics() {
        taskManager.removeAllEpics();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Список должен быть пуст");
        assertTrue(taskManager.getEpics().isEmpty(), "Список должен быть пуст");

        createEpicWithSubtasks();
        taskManager.createEpic(new Epic("TestEpic", "TaskDescription"));
        taskManager.removeAllEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Список должен быть пуст");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список должен быть пуст");
    }

    @Test
    void getTaskById() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,0,0,0),60));

        assertNull(taskManager.getTaskById(2));
        assertNotNull(taskManager.getTaskById(1));
    }

    @Test
    void getSubtaskById() {
        createEpicWithSubtasks();

        assertNull(taskManager.getSubtaskById(4));
        assertNotNull(taskManager.getSubtaskById(2));
    }

    @Test
    void getEpicById() {
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));

        assertNull(taskManager.getEpicById(2));
        assertNotNull(taskManager.getEpicById(1));
    }

    @Test
    void createTask() {
        final Task task = new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0),60);
        task.setId(1);
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0),60));
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,1,0,0),60));
        final Task testTask = taskManager.getTaskById(1);
        final Task testTask2 = taskManager.getTaskById(2);

        assertNotNull(testTask, "Задача не найдена");
        assertEquals(task, testTask, "Задачи не совпадают");
        assertNotEquals(testTask, testTask2, "id не должен совпадать");
    }

    @Test
    void createSubtask() {
        createEpicWithSubtasks();
        final Subtask task = new Subtask( "TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,0,0,0), 60,1);
        task.setId(2);
        final Subtask testTask = taskManager.getSubtaskById(2);
        final Subtask testTask2 = taskManager.getSubtaskById(3);

        assertNotNull(testTask, "Задача не найдена");
        assertEquals(task, testTask, "Задачи не совпадают");
        assertNotEquals(testTask, testTask2, "id не должен совпадать");

        //Проверка наличия эпика
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,1,1,0,0,0),
                60,  2));
        final Subtask testTask3 = taskManager.getSubtaskById(4);
        assertNull(testTask3, "Задачи не должно быть в базе");
    }

    @Test
    void createEpic() {
        final Epic epic = new Epic("TestEpic", "TestDescription");
        epic.setId(1);
        taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
        taskManager.createEpic(new Epic("TestEpic", "TestDescription"));
        final Epic testEpic = taskManager.getEpicById(1);
        final Epic testEpic2 = taskManager.getEpicById(2);

        assertNotNull(testEpic, "Эпик не найден");
        assertEquals(epic, testEpic, "Эпики не совпадают");
        assertNotEquals(testEpic, testEpic2, "id не должны совпадать");
    }

    @Test
    void updateTask() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60));
        final Task testTask = taskManager.getTaskById(1);
        final Task task = new Task("TestTask2", "TaskDescription2", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60);
        task.setId(1);

        assertNotEquals(task, testTask, "Задачи не должны совпадать");

        taskManager.updateTask(task);
        final Task testTask2 = taskManager.getTaskById(1);

        assertEquals(task, testTask2, "Задачи должны совпадать");
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0),60, 1));
        final Subtask testTask = taskManager.getSubtaskById(2);
        final Subtask task = new Subtask("TestTask2", "TaskDescription2", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60,1);
        task.setId(2);

        assertNotEquals(task, testTask, "Задачи не должны совпадать");

        taskManager.updateSubtask(task);
        final Subtask testTask2 = taskManager.getSubtaskById(2);

        assertEquals(task, testTask2, "Задачи должны совпадать");
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));
        final Epic testTask = taskManager.getEpicById(1);
        final Epic task = new Epic("TestTask2", "TaskDescription2");
        task.setId(1);

        assertNotEquals(task, testTask, "Задачи не должны совпадать");

        taskManager.updateEpic(task);
        final Epic testTask2 = taskManager.getEpicById(1);

        assertEquals(task, testTask2, "Задачи должны совпадать");
    }

    @Test
    void removeTask() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60));
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,1,0,0), 60));
        taskManager.removeTask(1);
        taskManager.removeTask(2);

        assertNull(taskManager.getTaskById(1), "Задачи не должно быть");
        assertNull(taskManager.getTaskById(2), "Задачи не должно быть");
    }

    @Test
    void removeSubtask() {
        createEpicWithSubtasks();

        assertNotNull(taskManager.getSubtaskById(3), "Задача не создалась");

        taskManager.removeSubtask(2);
        taskManager.removeSubtask(3);

        assertNull(taskManager.getSubtaskById(2), "Задачи не должно быть");
        assertNull(taskManager.getSubtaskById(3), "Задачи не должно быть");
    }

    @Test
    void removeEpic() {
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));

        assertNotNull(taskManager.getEpicById(2), "Задача не создалась");

        taskManager.removeEpic(2);
        taskManager.removeEpic(3);

        assertNull(taskManager.getEpicById(2), "Задачи не должно быть");
        assertNull(taskManager.getEpicById(3), "Задачи не должно быть");
    }

    @Test
    void getEpicSubtasks() {
        createEpicWithSubtasks();

        assertEquals(2, taskManager.getEpicSubtasks(1).size());
        assertNull(taskManager.getEpicSubtasks(2));
    }

    @Test
    void getHistory() {
        createTasksForHistory();
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        List<Integer> ids = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            ids.add(task.getId());
        }
        //Проверили максимальный размер (5шт) и повторы
        assertEquals(List.of(1,2,4,5,6), ids);
    }

    @Test
    void removeEpicFromHistory() {
        createTasksForHistory();
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.removeEpic(2);

        assertEquals(1,taskManager.getHistory().size());
        assertEquals(taskManager.getTasks().get(0), taskManager.getHistory().get(0));
    }

    @Test
    void removeFromHistoryBeginning() {
        createTasksForHistory();
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.removeTask(1);
        List<Integer> ids = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            ids.add(task.getId());
        }

        assertEquals(List.of(3,2), ids);
    }

    @Test
    void removeFromHistoryEnding() {
        createTasksForHistory();
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.removeSubtask(3);
        List<Integer> ids = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            ids.add(task.getId());
        }

        assertEquals(List.of(1,2), ids);
    }

    @Test
    void removeFromHistoryMiddle() {
        createTasksForHistory();
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.removeSubtask(3);
        List<Integer> ids = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            ids.add(task.getId());
        }

        assertEquals(List.of(1,2), ids);
    }

    @Test
    void checkEpicStatus() {
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));
        Epic epic = taskManager.getEpicById(1);

        //Пустой
        assertEquals(TaskStatus.NEW, epic.getStatus());

        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,0,0,0),60,  1));
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,1,0,0), 60,1));

        //Все NEW
        assertEquals(TaskStatus.NEW, epic.getStatus());

        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.DONE,
                 LocalDateTime.of(2023,4,10,3,0,0),60, 1));

        //NEW и DONE
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023,4,10,4,0,0),60,  1));

        //NEW DONE IN_PROGRESS
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        Subtask subtask = new Subtask("TestTask", "TaskDescription", TaskStatus.DONE,
                LocalDateTime.of(2023,4,10,5,0,0), 60,1);
        subtask.setId(2);
        Subtask subtask2 = new Subtask("TestTask", "TaskDescription", TaskStatus.DONE,
                LocalDateTime.of(2023,4,10,6,0,0), 60,1);
        subtask2.setId(3);
        Subtask subtask3 = new Subtask( "TestTask", "TaskDescription", TaskStatus.DONE,
                LocalDateTime.of(2023,4,10,7,0,0), 60,1);
        subtask3.setId(5);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        //all DONE
        assertEquals(TaskStatus.DONE, epic.getStatus());

        taskManager.removeAllSubtasks();
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023,4,10,0,0,0), 60,1));
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2023,4,10,1,0,0),60,1));

        //all IN_PROGRESS
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void checkOverlaps() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.now(), 200));
        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                        LocalDateTime.now(),200)));
        assertEquals("Обнаружено пересечение по времени", exception.getMessage());
    }

    @Test
    void sortedTasksByTimeAdd() {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,4,0,0),60));
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,5,0,0),60));
        taskManager.createEpic(new Epic("TestTask", "TaskDescription"));
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,3,0,0),60, 3));
        taskManager.createSubtask(new Subtask("TestTask", "TaskDescription", TaskStatus.NEW,
                 LocalDateTime.of(2023,4,10,6,0,0),60, 3));
        List<Integer> ids = new ArrayList<>();
        for (Task task : taskManager.getPrioritizedTasks()) {
            ids.add(task.getId());
        }

        assertEquals(List.of(4, 1, 2, 5), ids);

    }

    @Test
    void setEpicStartAndEndTime() {
        createEpicWithSubtasks();

        assertEquals("2023-01-01T00:00", taskManager.getEpicById(1).getStartTime().toString());
        assertEquals("2023-01-01T02:00", taskManager.getEpicById(1).getEndTime().toString());
    }
}