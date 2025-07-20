package manager.task.file;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    @Test
    @DisplayName("Сохранение и загрузка нескольких задач")
    void SaveAndLoadDifferentTasksType_getCorrectContent() {
        try {
            File tempfile = File.createTempFile("several_tasks", ".csv");
            tempfile.deleteOnExit();

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempfile);

            Task task = new Task("Task", "Description", Status.NEW);
            Epic epic = new Epic("Epic", "Description", Status.NEW);
            Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 2);

            manager.addTask(task);
            manager.addEpic(epic);
            manager.addSubtask(subtask);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempfile))) {
                reader.lines().forEach(System.out::println);
            } catch (IOException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempfile);

            assertEquals(manager.getTaskById(1), loadedManager.getTaskById(1));
            assertEquals(manager.getEpicById(2), loadedManager.getEpicById(2));
            assertEquals(manager.getSubtaskById(3), loadedManager.getSubtaskById(3));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла: ", e);
        }
    }

    @Test
    @DisplayName("Проверка CSV формата")
    void testCSVFormat() {
        try {
            File tempFile = File.createTempFile("csv_format", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

            Task task = new Task("Task", "Description", Status.NEW);

            manager.addTask(task);

            String content = Files.readString(tempFile.toPath());
            String[] lines = content.split("\n");
            System.out.println(lines[0]);
            assert lines[0].equals("id,type,name,status,description,epic");
            assert lines[1].equals("1,TASK,Task,NEW,Description");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла: ", e);
        }
    }

    @Test
    @DisplayName("Значение id сохраняется при загрузке из файла")
    void createFileLoadFile_getEquals() {
        try {
            File tempFile = File.createTempFile("csv_format", ".csv");
            tempFile.deleteOnExit();

            Task task = new Task("Task", "Description", Status.NEW);
            Epic epic = new Epic("Epic", "Description", Status.NEW);

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

            manager.addTask(task);
            manager.addEpic(epic);

            Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 2);
            manager.addSubtask(subtask);

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                reader.lines().forEach(System.out::println);
            } catch (IOException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            Task loadedTask = loadedManager.getTaskById(1);
            Epic loadedEpic = loadedManager.getEpicById(2);
            Subtask loadedSubtask = loadedManager.getSubtaskById(3);

            assertEquals(task.getId(), loadedTask.getId());
            assertEquals(epic.getId(), loadedEpic.getId());
            assertEquals(subtask.getId(), loadedSubtask.getId());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    @DisplayName("Удаленная задача не восстанавливается при загрузке из файла")
    void createFile_AddTwoTasks_RemoveOne_readOneFromFile() {
        try {
            File tempFile = File.createTempFile("csv_format", ".csv");
            tempFile.deleteOnExit();

            Task task1 = new Task("Task1", "Description", Status.NEW);
            Task task2 = new Task("Task2", "Description", Status.NEW);

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

            manager.addTask(task1);
            manager.addTask(task2);

            assertEquals(2, manager.getTasks().size());

            manager.removeTask(1);
            assertEquals(1, manager.getTasks().size());

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            assertEquals(1, loadedManager.getTasks().size());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    @DisplayName("Корректная работа при смешанных операциях добавления и удаления задач")
    void addTwoTask_RemoveOne_ReadFromFile_addTwo_GetThreeInManager(){
        try {
            File tempFile = File.createTempFile("csv_format", ".csv");
            tempFile.deleteOnExit();

            Task task1 = new Task("Task1", "Description", Status.NEW);
            Task task2 = new Task("Task2", "Description", Status.NEW);

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

            manager.addTask(task1);
            manager.addTask(task2);

            assertEquals(2, manager.getTasks().size());

            manager.removeTask(1);
            assertEquals(1, manager.getTasks().size());

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            assertEquals(1, loadedManager.getTasks().size());

            Task task3 = new Task("Task3", "Description", Status.NEW);
            Task task4 = new Task("Task4", "Description", Status.NEW);

            loadedManager.addTask(task3);
            loadedManager.addTask(task4);

            assertEquals(3, loadedManager.getTasks().size());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    @DisplayName("Связь между Epic и Subtask сохраняется при загрузке из файла")
    void addEpicAddSubtask_readFromFile_getEpicsSubtasksList() {
        try {
            File tempFile = File.createTempFile("csv_format", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

            Epic epic = new Epic("Epic", "Description", Status.NEW);
            manager.addEpic(epic);

            Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, 1);
            manager.addSubtask(subtask);

            assertEquals(1, manager.getEpics().size());
            assertEquals(1, manager.getSubtasks().size());

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            assertEquals(loadedManager.getEpicById(1).getSubtasksIds(), manager.getEpicById(1).getSubtasksIds());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }
}
