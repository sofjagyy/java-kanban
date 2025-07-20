package manager;

import manager.history.HistoryManager;
import manager.history.memory.InMemoryHistoryManager;
import manager.task.memory.InMemoryTaskManager;
import manager.task.TaskManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
