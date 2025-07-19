package manager.task.memory;

import manager.task.TaskManagerTest;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void ifSubtasksNEWEpicNEW() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", Status.NEW, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть NEW, когда все подзадачи NEW");
    }

    @Test
    void ifAllSubtasksDONEEpicDONE() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", Status.DONE, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть DONE, когда все подзадачи DONE");
    }

    @Test
    void ifSubtasksNEWandDONEEpicIN_PROGRESS() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", Status.DONE, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть INPROGRESS, когда подзадачи только NEW и DONE");
    }

    @Test
    void ifSubtasksIN_PROGRESSEpicIN_PROGRESS() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.IN_PROGRESS, epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", Status.IN_PROGRESS, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, когда все подзадачи IN_PROGRESS");
    }
}
