package model;

import manager.task.FileBackedTaskManager.TaskType;

import java.util.Objects;

public class Task implements Cloneable{
    protected String name;
    protected String description;
    protected Integer id = null;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getEpicIdForCSV() {
        return "";
    }

    @Override
    public Task clone() {
        try {
            Task taskClone = (Task) super.clone();
            return taskClone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Ошибка при клонировании", e);
        }
    }

    public String getName () {
        return this.name;
    }

    public String getDescription () {
        return this.description;
    }

    public Integer getId () {
        return this.id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        return task.id == this.id;
    }


    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,%s",
                getId(),
                getType(),
                getName(),
                getStatus(),
                getDescription(),
                getEpicIdForCSV());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                "}";
    }
}
