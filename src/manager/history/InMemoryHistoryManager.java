package manager.history;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        Task taskCopy = task.clone();
        history.add(taskCopy);

        if(history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
