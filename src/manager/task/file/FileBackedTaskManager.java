
package manager.task.file;

import manager.task.memory.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String CSV_HEADER = "id,type,name,status,description,epic,duration,startTime";

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    private String toCSV(Task task) {
        return String.format("%d,%s,%s,%s,%s,,%s,%s",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                formatDuration(task.getDuration()),
                formatStartTime(task.getStartTime()));
    }

    private String toCSV(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,,%s,%s",
                epic.getId(),
                TaskType.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription(),
                formatDuration(epic.getDuration()),
                formatStartTime(epic.getStartTime()));
    }

    private String toCSV(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                subtask.getId(),
                TaskType.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicId(),
                formatDuration(subtask.getDuration()),
                formatStartTime(subtask.getStartTime()));
    }

    private String formatDuration(Duration duration) {
        return duration != null ? String.valueOf(duration.toMinutes()) : "";
    }

    private String formatStartTime(LocalDateTime startTime) {
        return startTime != null ? startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
    }

    private void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(CSV_HEADER + "\n");

            tasks.forEach((id, task) -> sb.append(toCSV(task)).append("\n"));
            epics.forEach((id, epic) -> sb.append(toCSV(epic)).append("\n"));
            subtasks.forEach((id, subtask) -> sb.append(toCSV(subtask)).append("\n"));

            Files.write(file.toPath(), Collections.singleton(sb));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public Task addTask(Task task) {
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
    public Subtask addSubtask(Subtask subtask) {
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
        super.removeEpic(id);
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
            idCounter = id + 1;
        }

        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = null;
        LocalDateTime startTime = null;

        if (parts.length > 6 && !parts[6].trim().isEmpty()) {
            try {
                duration = Duration.ofMinutes(Long.parseLong(parts[6].trim()));
            } catch (NumberFormatException e) {
                duration = null;
            }
        }

        if (parts.length > 7 && !parts[7].trim().isEmpty()) {
            try {
                startTime = LocalDateTime.parse(parts[7].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                startTime = null;
            }
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setDuration(duration);
                task.setStartTime(startTime);
                tasks.put(id, task);
                break;

            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                if (duration != null && startTime != null) {
                    epic.setStartTime(startTime);
                    epic.setDuration(duration);
                }
                epics.put(id, epic);
                break;

            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                subtasks.put(id, subtask);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи " + type);
        }
    }

    protected static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists()) {
            return manager;
        }

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            Arrays.stream(lines)
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(manager::convertTasksToManagerFromString);

            manager.getSubtasks().stream()
                    .forEach(subtask -> manager.getEpicById(subtask.getEpicId())
                            .addSubtaskId(subtask.getId()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getAbsolutePath(), e);
        }
        return manager;
    }
}





