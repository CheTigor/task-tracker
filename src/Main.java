import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.TaskStatus;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask("Волейбол", "01.02.2023 в 12:00", TaskStatus.NEW);
        taskManager.createTask("Valorant", "Выйграть турнир", TaskStatus.DONE);
        taskManager.createTask("Ужин", "В пятницу на этой неделе", TaskStatus.IN_PROGRESS);
        taskManager.createEpic("Обучение в практикуме", "Нужно пройти все спринты");
        taskManager.createSubtask("Спринт 1", "Очень нравится", TaskStatus.DONE, 4);
        taskManager.createSubtask("Спринт 2", "Задание было сложным", TaskStatus.IN_PROGRESS, 4);
        taskManager.createSubtask("Спринт 3", "Очень непонятное задание", TaskStatus.DONE, 4);
        taskManager.createSubtask("Спринт 4", "Что то новое", TaskStatus.DONE, 4);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(4);
        taskManager.getSubtaskById(7);

        TaskManager taskManager1 = FileBackedTasksManager.loadFromFile(new File("C:/Users/chetv/dev/java-kanban/resources/task.cvs"));
        //System.out.println(taskManager.getHistory());
//        System.out.println(taskManager1.getTaskById(1));
//        System.out.println(taskManager1.getSubtaskById(5));
//        System.out.println(taskManager1.getEpicById(4));
//        System.out.println(taskManager1.getSubtaskById(7));
//        System.out.println(taskManager.getTasks());
//        System.out.println(taskManager1.getTasks());
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager1.getHistory());
    }
}
