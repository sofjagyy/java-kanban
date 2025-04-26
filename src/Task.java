public class Task {
    private String name;
    private String description;
    private final int id;
    private Status status;
    private int epicID = -1;

    public Task(String name, String description, int id, Status status) {
        this(name, description, id, status.NEW, -1);
    }

    public Task(String name, String description, int id, Status status, int epicID) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.epicID = epicID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getEpicID() {
        return epicID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setEpicID(int id) {
        this.epicID = id;
    }
}
