package manager.history;

import manager.task.InMemoryTaskManager;
import manager.task.TaskManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Должен добавлять несколько задач в правильном порядке")
    void add_addedInCorrectOrder() {

        Task task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        epic.setId(2);
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 2);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }

    @Test
    @DisplayName("Должен удалять дубликат при повторном добавлении задачи")
    void add_multipleTasksDeleted() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        epic.setId(2);
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 2);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.add(task);

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(epic, history.get(0));
        assertEquals(subtask, history.get(1));
        assertEquals(task, history.get(2));
    }

    @Test
    @DisplayName("Должен удалять задачу из середины истории")
    void remove_TaskRemovedFromTheMiddle() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача1", "Описание", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача2", "Описание", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Задача3", "Описание", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); //удаляем по id задачи

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    @DisplayName("Должен удалять единственную задачу в истории")
    void remove_SingleTaskRemoved() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача1", "Описание", Status.NEW);
        task1.setId(1);

        historyManager.add(task1);

        historyManager.remove(1);

        ArrayList<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Не должен падать при удалении несуществующей задачи")
    void addingNonExistingTaskId_WorksCorrectly() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача1", "Описание", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача2", "Описание", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        assertDoesNotThrow(() -> historyManager.remove(999));

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    @DisplayName("Эпик с подзадачами должен быть удален при удалении Эпика")
    void removeEpic_removedEpic_itsSubtasksRemovedToo() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        //по условию финальных заданий предыдущих спринтов именнно Manager отвечает за расчет статуса и подзадач Epic, поэтому необходимо создать экземпляр
        Task task = new Task("Задача", "Описание", Status.NEW);
        task.setId(1);
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        epic.setId(2);
        Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, 2);
        subtask.setId(3);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.historyManager.add(task);
        taskManager.historyManager.add(epic);
        taskManager.historyManager.add(subtask);

        ArrayList<Task> history = (ArrayList<Task>) taskManager.historyManager.getHistory();

        assertEquals(3, history.size());

        taskManager.removeEpic(2);

        history = (ArrayList<Task>) taskManager.historyManager.getHistory();

        assertEquals(1, history.size());
    }
}
