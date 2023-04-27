package manager;

import manager.exceptions.ManagerSaveException;
import manager.file.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    //private static final File TEST_FILE = new File("resources/task_test.cvs");
    private static final Path TEST_FILE_PATH = Path.of("resources/task_test.cvs");

    @BeforeEach
    void setUp() {
        taskManager = new FileBackedTasksManager(TEST_FILE_PATH.toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TEST_FILE_PATH);
    }

    @Test
    void shouldSave() throws IOException {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60));
        taskManager.createEpic(new Epic("TestEpic", "EpicDescription"));
        taskManager.createSubtask(new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,1,0,0),60, 2));
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        List<String> result = Files.readAllLines(TEST_FILE_PATH);

        assertEquals(List.of(
                "id,type,name,status,description,duration,epic",
                "1,TASK,TestTask,NEW,TaskDescription,2023-04-10T00:00:00,60,2023-04-10T01:00:00,",
                "3,SUBTASK,TestSubtask,NEW,SubtaskDescription,2023-04-10T01:00:00,60,2023-04-10T02:00:00,2",
                "2,EPIC,TestEpic,NEW,EpicDescription,2023-04-10T01:00:00,60,2023-04-10T02:00:00,",
                "","1,3,2"),
                result, "Неверное сохранение в файл.");
    }

    @Test
    void shouldLoadFromFile() throws IOException {
        shouldSave();
        TaskManager taskManager1 = FileBackedTasksManager.loadFromFile(TEST_FILE_PATH.toFile());

        assertEquals(taskManager1.getTasks(), taskManager.getTasks());
        assertEquals(taskManager1.getSubtasks(), taskManager.getSubtasks());
        assertEquals(taskManager1.getEpics(), taskManager.getEpics());
        assertEquals(taskManager1.getHistory(), taskManager.getHistory());

        taskManager1.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60));

        assertEquals(4, taskManager1.getTaskById(4).getId());
    }

    @Test
    void shouldLoadFromEmptyFile() {
        final ManagerSaveException exception = assertThrows(ManagerSaveException.class, () ->
                FileBackedTasksManager.loadFromFile(new File("resources/task_test1.cvs")));

        // можно проверить, находится ли в exception ожидаемый текст
        assertEquals("Can't read form file: task_test1.cvs", exception.getMessage());
    }

    @Test
    void shouldSaveEmptyHistory() throws IOException {
        taskManager.createTask(new Task("TestTask", "TaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,0,0,0), 60));
        taskManager.createEpic(new Epic("TestEpic", "EpicDescription"));
        taskManager.createSubtask(new Subtask("TestSubtask", "SubtaskDescription", TaskStatus.NEW,
                LocalDateTime.of(2023,4,10,1,0,0), 60,2));
        List<String> result = Files.readAllLines(TEST_FILE_PATH);

        assertEquals(List.of(
                        "id,type,name,status,description,duration,epic",
                        "1,TASK,TestTask,NEW,TaskDescription,2023-04-10T00:00:00,60,2023-04-10T01:00:00,",
                        "3,SUBTASK,TestSubtask,NEW,SubtaskDescription,2023-04-10T01:00:00,60,2023-04-10T02:00:00,2",
                        "2,EPIC,TestEpic,NEW,EpicDescription,2023-04-10T01:00:00,60,2023-04-10T02:00:00,",
                        ""),
                result, "Неверное сохранение в файл.");
    }

    @Test
    void shouldSaveEpicWithoutSubtasks() throws IOException {
        taskManager.createEpic(new Epic("TestEpic", "EpicDescription"));
        List<String> result = Files.readAllLines(TEST_FILE_PATH);

        assertEquals(List.of(
                        "id,type,name,status,description,duration,epic",
                        "1,EPIC,TestEpic,NEW,EpicDescription,,0,,",
                        ""),
                result, "Неверное сохранение в файл.");
    }
}