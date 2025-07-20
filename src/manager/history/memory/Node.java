package manager.history.memory;

import model.Task;

public class Node {
    protected Task task;
    protected Node prev;
    protected Node next;

    Node(Task task) {
        this.task = task;
    }
}



