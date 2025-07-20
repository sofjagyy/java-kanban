package manager;

import manager.history.HistoryManager;
import manager.history.memory.InMemoryHistoryManager;
import manager.task.TaskManager;
import manager.task.memory.InMemoryTaskManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
