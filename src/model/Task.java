package model;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Cloneable{
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task() {
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setDurationInMinutes(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public long getDurationInMinutes() {
        if (duration == null) {
            return 0;
        }
        return duration.toMinutes();
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
