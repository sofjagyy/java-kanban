package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test //проверьте, что наследники класса Task равны друг другу, если равен их id;
    void CreateDifferentSubtasksWithSameIds_Compare_getEqual() {
        Subtask subtask1 = new Subtask("Задача 1", "Описание 1", Status.NEW, 2);
        subtask1.setId(1);

        Subtask subtask2 = new Subtask("Задача 2", "Описание 2", Status.IN_PROGRESS, 3);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Задачи с одинаковым ID считаются одинаковыми");
    }
}