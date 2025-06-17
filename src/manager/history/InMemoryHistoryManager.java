package manager.history;

import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node tail;
    private Node head;
    public InMemoryHistoryManager() {
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();
        if (nodeMap.containsKey(taskId)) {
            removeNode(nodeMap.get(taskId));
        }

        Node newNode = linkLast(task);
        nodeMap.put(taskId, newNode);
        System.out.println(nodeMap.get(taskId));
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodeMap.get(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            nodeMap.remove(id);
        }
    };

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        return newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;
    }

}
