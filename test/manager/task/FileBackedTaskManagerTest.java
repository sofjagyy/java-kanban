package manager.task;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    @Test
    @DisplayName("Сохранение и загрузка пустого файла")
    void SaveAndLoadEmptyFile_getEmptyFile() {
        try {
            File tempFile = File.createTempFile("empty_tasks", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            assert loadedManager.getTasks().isEmpty();
            assert loadedManager.getEpics().isEmpty();
            assert loadedManager.getSubtasks().isEmpty();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    @DisplayName("Сохранение и загрузка нескольких задач")
    void SaveAndLoadDifferentTasksType_getCorrectContent() {
        try {
            File tempfile = File.createTempFile("several_tasks", ".csv");
            tempfile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempfile);

            Task task1 = new Task("Task", "Description", Status.NEW);
            Epic epic2 = new Epic("Epic", "Description", Status.NEW);
            task1.setId(1);
            epic2.setId(2);
            manager.addTask(task1);
            manager.addTask(epic2);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempfile);

            assert loadedManager.getTasks().size() == 1;
            assert loadedManager.getEpics().size() == 1;

            Task loadedTask = loadedManager.getTaskById(1);
            assert loadedTask.getName().equals("Task");
            assert loadedTask.getStatus().equals(Status.NEW);

            Epic loadedEpic = loadedManager.getEpicById(2);
            assert loadedEpic.getName().equals("Epic");
            assert loadedEpic.getStatus() == Status.NEW;

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

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task = new Task("Task", "Description", Status.NEW);

            manager.addTask(task);

            String content = Files.readString(tempFile.toPath());
            String[] lines = content.split("\r\n");
            System.out.println(lines[0]);
            assert lines[0].equals("id,type,name,status,description,epic");
            assert lines[1].equals("1,TASK,Task,NEW,Description,");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла: ", e);
        }
    }
}
