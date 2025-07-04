package manager.task;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

import java.util.ArrayList;

public interface TaskManager {

    List<Task> getHistory();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getSubtasksByEpic(Epic epic);

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int epicId);

    void removeSubtask(int subtaskId);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);
}
