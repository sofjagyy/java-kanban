package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    @Test
    void CreateDifferentTasksWithSameIds_Compare_getEqual() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID считаются одинаковыми");
    }
}