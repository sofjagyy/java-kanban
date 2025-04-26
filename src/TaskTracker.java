import java.util.ArrayList;

public class TaskTracker {
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<EpicTask> epicTasks = new ArrayList<>();
    private int id = 1;

    public TaskTracker(ArrayList<Task> tasks, ArrayList<EpicTask> epicTasks) {
        this.tasks = tasks;
        this.epicTasks = epicTasks;
    }
    public TaskTracker() {

    }

    public void incId() {
        this.id += 1;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addEpicTask(EpicTask epicTask) {
        epicTasks.add(epicTask);
    }

    public Object getTaskById (int id) {
        for (Task task : tasks){
            if (task.getId() == id) {
                return task;
            }
        }

        for (EpicTask epicTask : epicTasks){
            if (epicTask.getId() == id) {
                return epicTask;
            }
        }

        return "Задачи с таким id не существует!";
    }

    public void deleteTaskById (int id) {
        Object task = getTaskById(id);

        if (task.getClass() == EpicTask.class) {
            epicTasks.remove(task);
        } else {
            int epicID = ((Task) task).getEpicID();
            if (epicID != -1) {
                EpicTask epicTask = (EpicTask) getTaskById(epicID);
                epicTask.removeTask((Task) task);
            }
        }
    }
}