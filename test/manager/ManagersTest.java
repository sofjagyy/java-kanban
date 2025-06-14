package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.task.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    void getDefault_ReturnNormalTaskManagerAndHistory() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Не может быть Null");

        Task task = new Task("Задача 1", "Описание 1", Status.NEW);
        taskManager.addTask(task);

        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Коллекция задач не может быть пустой");
        assertEquals(1, tasks.size(), "В коллекции должна быть только одна задача");

        Task newTask = taskManager.getTaskById(1);
        assertTrue(newTask.getId() == 1, "ID существует и равен 1");
    }

    @Test
    void getDefaultHistory_WorksNormal() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Не null объект");

        assertTrue(historyManager instanceof InMemoryHistoryManager,
                "getDefaultHistory возвращает экземпляр InMemoryHistoryManager");

        List<Task> initialHistory = historyManager.getHistory();
        assertTrue(initialHistory.isEmpty(), "сначала список пустой");


        Task task = new Task("Тестовая задача", "Описание тестовой задачи", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> updatedHistory = historyManager.getHistory();
        assertEquals(1, updatedHistory.size(), "содержит только одну задачу");
    }
}