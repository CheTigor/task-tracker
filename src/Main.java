import manager.Managers;
import manager.TaskManager;
import tasks.TaskStatus;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        /*TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Волейбол", "01.02.2023 в 12:00", TaskStatus.NEW);
        Task task2 = new Task("Valorant", "Выйграть турнир", TaskStatus.DONE);
        Task task3 = new Task("Ужин", "В пятницу на этой неделе", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        Epic epic1 = new Epic("Обучение в практикуме", "Нужно пройти все спринты");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Спринт 1", "Очень нравится", TaskStatus.DONE, 4);
        Subtask subtask2 = new Subtask("Спринт 2", "Задание было сложным", TaskStatus.IN_PROGRESS, 4);
        Subtask subtask3 = new Subtask("Спринт 3", "Очень непонятное задание", TaskStatus.DONE, 4);
        Subtask subtask4 = new Subtask("Спринт 4", "Что то новое", TaskStatus.DONE, 4);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getEpicById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(5);
        taskManager.removeTask(4);
        System.out.println(taskManager.getHistory());*/
        Path path = Paths.get("C:\\Users\\chetv\\dev\\java-kanban\\resources\\InMemoryTaskManager.txt");
        TaskManager taskManager = Managers.loadFromFile(path);
        System.out.println(taskManager.getHistory());

    }
}
