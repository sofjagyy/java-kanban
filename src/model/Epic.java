package model;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;
    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtasksIds = new ArrayList<>();
        this.duration = null;
        this.startTime = null;
        this.endTime = null;
    }

    @Override
    public Epic clone() {
        Epic clone = (Epic) super.clone();
        return clone;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksIds.remove(Integer.valueOf(subtaskId));
    }

    public void addSubtaskId(int subtaskId){
        if (subtaskId == this.id) {
            throw new IllegalArgumentException("Эпик не может содержать сам себя");
        }
        subtasksIds.add(subtaskId);
    }

    public void clearSubtasksIds() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                ", subtasksId='" + subtasksIds + "'" +
                "}";
    }
}



