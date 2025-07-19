package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test //проверьте, что наследники класса Task равны друг другу, если равен их id;
    void CreateDifferentEpicsWithSameIds_Compare_getEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", Status.NEW);
        epic1.addSubtaskId(1);
        epic1.addSubtaskId(2);
        epic1.setId(1);

        Epic epic2 = new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS);
        epic2.addSubtaskId(3);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Задачи с одинаковым ID считаются одинаковыми");
    }

    @Test //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;

    void addSubtaskId_throwError_subtaskIdSameWithEpicId() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        epic.setId(1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtaskId(epic.getId());
        });


        String expectedMessage = "Эпик не может содержать сам себя";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Сообщение: " + expectedMessage);
    }
}