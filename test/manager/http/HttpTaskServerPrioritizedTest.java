package manager.http;
import com.google.gson.Gson;
import manager.task.TaskManager;
import manager.task.memory.InMemoryTaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerPrioritizedTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskServerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals("[]", response.body());
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {

        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "First task", Status.NEW, Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task 2", "Second task", Status.NEW, Duration.ofMinutes(30), now.plusHours(1));
        Task task3 = new Task("Task 3", "Third task", Status.NEW, Duration.ofMinutes(30), now.plusHours(3));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());


        assertNotNull(response.body());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
        assertTrue(response.body().contains("Task 3"));
    }

    @Test
    public void testGetPrioritizedTasksWithTasksWithoutTime() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        Task taskWithTime = new Task("Task With Time", "Task with time", Status.NEW, Duration.ofMinutes(30), now.plusHours(1));
        Task taskWithoutTime = new Task("Task Without Time", "Task without time", Status.NEW);

        manager.addTask(taskWithTime);
        manager.addTask(taskWithoutTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
    }
}