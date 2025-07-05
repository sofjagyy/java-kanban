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
import java.util.Collections;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(CSV_HEADER + "\n");

            tasks.forEach((id, task) -> sb.append(task.toCSV()).append("\n"));
            epics.forEach((id, epic) -> sb.append(epic.toCSV()).append("\n"));
            subtasks.forEach((id, subtasks) -> sb.append(subtasks.toCSV()).append("\n"));

            Files.write(file.toPath(), Collections.singleton(sb));
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

    private void convertTasksToManagerFromString(String value) {
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

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                manager.convertTasksToManagerFromString(line);
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






