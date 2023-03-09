package manager;

import tasks.Node;
import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 5;
    private final TasksLinkedHashMap tasksLinkedHashMap = new TasksLinkedHashMap();


    @Override
    public void add(Task task) {
        tasksLinkedHashMap.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksLinkedHashMap.getTasks();
    }

    @Override
    public void remove(int id) {
        if (tasksLinkedHashMap.taskLinkedHashMap.containsKey(id)) {
            tasksLinkedHashMap.removeNode(tasksLinkedHashMap.taskLinkedHashMap.remove(id));
        }
    }

    public static class TasksLinkedHashMap {
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;
        private final Map<Integer, Node<Task>> taskLinkedHashMap = new HashMap<>();


        public void linkLast(Task task) {
            if (taskLinkedHashMap.containsKey(task.getId())) {
                removeNode(taskLinkedHashMap.get(task.getId()));
            }
            final Node<Task> t = tail;
            final Node<Task> newNode = new Node<>(task, t, null);
            tail = newNode;
            if (t == null)
                head = newNode;
            else
                t.setNext(newNode);
            taskLinkedHashMap.put(task.getId(), tail);
            size++;
            if (size > MAX_HISTORY_SIZE) {
                removeNode(head);
                taskLinkedHashMap.remove(head.getData().getId());
            }
        }

        public List<Task> getTasks() {
            List<Task> tasksHistory = new ArrayList<>();
            Node<Task> nextTask = head;
            int count = 0;
            while (count != size) {
                tasksHistory.add(nextTask.getData());
                nextTask = nextTask.getNext();
                count++;
            }
            return tasksHistory;
        }

        public void removeNode(Node<Task> node) {
            final Node<Task> next = node.getNext();
            final Node<Task> prev = node.getPrev();
            if (prev == null) {
                head = next;
            } else {
                prev.setNext(next);
            }
            if (next == null) {
                tail = prev;
            } else {
                next.setPrev(prev);
            }
            size--;
        }

    }

}
