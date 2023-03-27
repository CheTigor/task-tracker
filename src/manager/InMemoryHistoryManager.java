package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 5;
    private Node head;
    private Node tail;
    private int size = 0;
    private final Map<Integer, Node> history = new HashMap<>();

    public Map<Integer, Node> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        removeNode(id);
        linkLast(task);
        history.put(id, tail);
    }

    @Override
    public List<Task> getListOfHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private List<Task> getTasks() {
        List<Task> tasksHistory = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasksHistory.add(node.data);
            node = node.next;
        }
        return tasksHistory;
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task, tail, null);
        if (head == null) {
            head = newNode;
        }
        else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
        if (size > MAX_HISTORY_SIZE) {
            removeNode(head.data.getId());
        }
    }

    private void removeNode(int id) {
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            head = node.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
        size--;
    }

    private static class Node {

        private final Task data;
        private Node next;
        private Node prev;

        public Node(Task data, Node prev, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

}
