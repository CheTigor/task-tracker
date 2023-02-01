import manager.Manager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();
        Task task1 = new Task("Волейбол", "01.02.2023 в 12:00", "NEW");
        Task task2 = new Task("Valorant", "Выйграть турнир", "DONE");
        Task task3 = new Task("Ужин", "В пятницу на этой неделе", "IN_PROGRESS");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        Epic epic1 = new Epic("Обучение в практикуме", "Нужно пройти все спринты");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Спринт 1", "Очень нравится", "DONE", 4);
        Subtask subtask2 = new Subtask("Спринт 2", "Задание было сложным", "IN_PROGRESS", 4);
        Subtask subtask3 = new Subtask("Спринт 3", "Очень непонятное задание", "DONE", 4);
        Subtask subtask4 = new Subtask("Спринт 4", "Что то новое", "DONE", 4);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.createSubtask(subtask4);

    }
}
