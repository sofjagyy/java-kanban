package manager.task.FileBackedTaskManager;

import manager.task.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(CSV_HEADER);
            for (Task task : getTasks()) {
                lines.add(taskToStringForCSV(task));
            }

            for (Epic epic : getEpics()) {
                lines.add(taskToStringForCSV(epic));
            }

            for (Subtask subtask : getSubtasks()) {
                lines.add(taskToStringForCSV(subtask));
            }

            Files.write(file.toPath(), lines);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getAbsolutePath(), e);
        }
    }


    @Override
    public Task addTask(Task task){
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask){
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);;
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    private String taskToStringForCSV(Task task) {
        TaskType type;
        String epicId = "";

        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = TaskType.TASK;
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId
        );
    }

    private void fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);

        if (id >= idCounter) {
            idCounter = id+1;
        }

        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status =  Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                super.addTaskIfUnique(task, tasks);
                break;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                super.addTaskIfUnique(epic, epics);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                super.addTaskIfUnique(subtask, subtasks);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists()) {
            return manager;
        }

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            // Пропускаем заголовок
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                manager.fromString(line);
            }

            for (Subtask subtask : manager.getSubtasks()) {
                int epicId = subtask.getEpicId();
                manager.getEpicById(epicId).addSubtaskId(subtask.getId());
            }

        } catch (IOException e) {
                throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getAbsolutePath(), e);
            }
            return manager;
        }

    }






