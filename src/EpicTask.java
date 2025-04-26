import java.util.ArrayList;

public class EpicTask {
    private String name;
    private ArrayList<Task> tasks;
    private Status status;
    private int id;

    public EpicTask(String name, ArrayList<Task> tasks, Status status, int id) {
        this.name = name;
        this.tasks = tasks;
        this.status = status;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getId() {
        return id;
    }

    public void addTask(Task task) {
        task.setEpicID(this.id);
        tasks.add(task);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isDone() {
        for (Task task : tasks) {
            if (task.getStatus() != Status.DONE) {
                return false;
            }
        }
        return true;
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

}
