package manager.task.memory;

import manager.Managers;
import manager.history.HistoryManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int idCounter = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private void incIdCounter() {
        idCounter += 1;
    }

    public InMemoryTaskManager() {
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
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
        ArrayList<Subtask> result = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtasksIds()) {
            result.add(subtasks.get(subtaskId));
        }

        return result;
    }

    //Добавление
    public Task addTask(Task task) {
        task.setId(idCounter);
        tasks.put(task.getId(), task);
        incIdCounter();

        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
        incIdCounter();

        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (epics.containsKey(epicId)) {
            subtask.setId(idCounter);
            incIdCounter();
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }

        return subtask;
    }

    //Удаление
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    public void removeEpic(int epicId) {
        if(epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> subtaskIds = epic.getSubtasksIds();

            for (int id : subtaskIds) {
                subtasks.remove(id);
                historyManager.remove(id);
            }

            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            int epicId = subtasks.get(subtaskId).getEpicId();
            Epic epic = epics.get(epicId);

            epic.removeSubtaskId(subtaskId);
            updateEpicStatus(epic);

            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }


    public void removeTasks(){
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    public void removeEpics(){
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks(){
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        subtasks.clear();

        if (!epics.isEmpty()) {
            epics.values().forEach(epic -> {
                epic.clearSubtasksIds();
                epic.setStatus(Status.NEW);
            });
        }

    }

    // Обновление
    public void updateTask(Task task) {
        updateItemIfExists(task.getId(), task, tasks);
    }

    public void updateSubtask(Subtask subtask) {
        if (updateItemIfExists(subtask.getId(), subtask, subtasks)) {
            Epic parentEpic = epics.get(subtask.getEpicId());
            updateEpicStatus(parentEpic);
        }
    }

    private void updateEpic(Epic epic)      {
        if (updateItemIfExists(epic.getId(), epic, epics)) {
            updateEpicStatus(epic);
        }
    }


    private <T> boolean updateItemIfExists(int id, T item, HashMap<Integer, T> collection) {
        if(collection.containsKey(id)) {
            collection.put(id, item);
            return true;
        } else {
            System.out.println("Элемент с ID " + id + " не найден и не обновлен.");
            return false;
        }
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasksByEpic(epic);
        Status status = epic.getStatus();

        if (epic.getSubtasksIds().isEmpty()) {
            status = Status.NEW;
            return;
        }

        Status currentStatus = subtasks.getFirst().getStatus();

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                status = Status.IN_PROGRESS;
                return;
            }
        }

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != currentStatus) {
                status = Status.IN_PROGRESS;
                return;
            }
        }

        status = currentStatus;
    }
}


