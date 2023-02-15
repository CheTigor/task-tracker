package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> tasksHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == MAX_HISTORY_SIZE) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
