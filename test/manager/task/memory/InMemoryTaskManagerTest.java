package manager.task.memory;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test //проверьте, что объект Subtask нельзя сделать своим же эпиком;
    void AddSubtask_TryToMakeHisOwnEpic_getException() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic", "Description", Status.NEW);
        epic.setId(2);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 2);
        subtask.setId(2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addSubtask(subtask);
        });

        String expectedMessage = "Подзадача не может быть своим собственным эпиком";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Сообщение должно содержать: " + expectedMessage);
    }

    @Test //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    void AddAndFindTasks_ByKeysInCollection() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача 1", "Описание 1", Status.NEW);
        taskManager.addTask(task);

        Epic epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        taskManager.addEpic(epic);

        int epicId = taskManager.getEpics().getFirst().getId();

        assertEquals(2, epicId, "EpicId равен 2, так как создан вторым");

        Subtask subtask = new Subtask("Подзадача 1", "Описание 1", Status.NEW, epicId);
        taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getEpicById(2).getSubtasksIds().size(), "Размер SubtasksId равен 1");
    }

    @Test //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    void WithoutConflict_ManualAndGeneratedIds() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        taskManager.addTask(task1);



        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        task2.setId(1);
        taskManager.addTask(task2);


        Task retrievedTask = taskManager.getTaskById(1);
        assertNotNull(retrievedTask, "Задача с ID=1 должна существовать");
        assertEquals("Задача 1", retrievedTask.getName(), "Имя задачи должно остаться прежним");
        assertEquals(Status.NEW, retrievedTask.getStatus(), "Статус задачи должен остаться прежним");
        assertEquals(1, taskManager.getTasks().size(), "Должна быть только одна задача");
    }

    @Test //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    void fieldsAreStable() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();;

        Task originalTask = new Task("Названиие", "Описание", Status.NEW);
        originalTask.setId(42);

        int originalId = originalTask.getId();
        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        taskManager.addTask(originalTask);

        assertEquals(originalId, originalTask.getId(), "ID задачи не должен измениться");
        assertEquals(originalName, originalTask.getName(), "Название задачи не должно измениться");
        assertEquals(originalDescription, originalTask.getDescription(), "Описание задачи не должно измениться");
        assertEquals(originalStatus, originalTask.getStatus(), "Статус задачи не должен измениться");


        Task retrievedTask = taskManager.getTaskById(originalId);


        assertNotNull(retrievedTask, "Задача должна быть найдена в менеджере");
        assertEquals(originalId, retrievedTask.getId(), "ID полученной задачи должен совпадать с оригиналом");
        assertEquals(originalName, retrievedTask.getName(), "Название полученной задачи должно совпадать с оригиналом");
        assertEquals(originalDescription, retrievedTask.getDescription(), "Описание полученной задачи должно совпадать с оригиналом");
        assertEquals(originalStatus, retrievedTask.getStatus(), "Статус полученной задачи должен совпадать");
    }
}
