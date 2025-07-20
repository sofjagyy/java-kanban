package manager.task.memory;

import manager.Managers;
import manager.history.HistoryManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int idCounter = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private int getNextId() {
        return idCounter++;
    }

    public InMemoryTaskManager() {
    }

    private HistoryManager getHistoryManager() {
        return historyManager;
    }


    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasksIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public Task addTask(Task task) {
        checkTimeInterception(task);
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    public Epic addEpic(Epic epic) {
        checkTimeInterception(epic);
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Эпик с ID " + epicId + " не найден");
        }

        checkTimeInterception(subtask);
        subtask.setId(getNextId());

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
            updateEpicTime(epic);
        }

        return subtask;
    }

    public void removeTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            epic.getSubtasksIds().stream()
                    .peek(id -> {
                        Subtask subtask = subtasks.get(id);
                        if (subtask != null) {
                            prioritizedTasks.remove(subtask);
                        }
                    })
                    .peek(subtasks::remove)
                    .forEach(historyManager::remove);

            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            prioritizedTasks.remove(subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);

            epic.removeSubtaskId(subtaskId);
            updateEpicStatus(epic);
            updateEpicTime(epic);

            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    public void removeTasks() {
        tasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Task::getId)
                .forEach(historyManager::remove);
        tasks.clear();
    }

    public void removeEpics() {
        epics.values().stream()
                .map(Epic::getId)
                .forEach(historyManager::remove);

        subtasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Subtask::getId)
                .forEach(historyManager::remove);

        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks() {
        subtasks.values().stream()
                .peek(prioritizedTasks::remove)
                .map(Subtask::getId)
                .forEach(historyManager::remove);

        subtasks.clear();

        if (!epics.isEmpty()) {
            epics.values().stream()
                    .peek(Epic::clearSubtasksIds)
                    .peek(epic -> epic.setStatus(Status.NEW))
                    .forEach(this::updateEpicTime);
        }
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Задача с ID " + task.getId() + " не найдена");
        }
        checkTimeInterception(task);
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Подзадача с ID " + subtask.getId() + " не найдена");
        }
        checkTimeInterception(subtask);
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            prioritizedTasks.remove(oldSubtask);
        }
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = epics.get(subtask.getEpicId());
        updateEpicStatus(parentEpic);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
            updateEpicTime(parentEpic);
        }
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasksByEpic(epic);
        Status status = epic.getStatus();

        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status currentStatus = subtasks.getFirst().getStatus();

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != currentStatus) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }

        epic.setStatus(currentStatus);
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        if (task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();

        return task1.getStartTime().isBefore(end2) && task2.getStartTime().isBefore(end1);
    }

    private void checkTimeInterception(Task task) {
        if (hasTimeConflict(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другими задачами");
        }
    }

    private boolean hasTimeConflict(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> isTasksOverlap(task, newTask));
    }

    private void updateEpicTime(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasksByEpic(epic);

        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
            }

            if (subtask.getEndTime() != null) {
                if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                    latestEnd = subtask.getEndTime();
                }
            }

            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        epic.setStartTime(earliestStart);
        epic.setDuration(totalDuration);
        epic.setEndTime(latestEnd);
    }
}