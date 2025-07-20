package manager.http;

import com.google.gson.Gson;
import manager.http.HttpTaskServer;
import manager.task.TaskManager;
import manager.task.memory.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubtasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW,
                addedEpic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test Subtask", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testAddSubtaskWithoutEpic() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW, 999,
                Duration.ofMinutes(10), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddSubtaskWithTimeOverlap() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        Subtask subtask1 = new Subtask("Subtask 1", "First subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(60), startTime);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Second subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(60), startTime.plusMinutes(30));
        String subtaskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(10), LocalDateTime.now());
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNotNull(response.body());
        assertTrue(response.body().contains("Test Subtask"));
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {

        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(10), LocalDateTime.now());
        Subtask addedSubtask = manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + addedSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNotNull(response.body());
        assertTrue(response.body().contains("Test Subtask"));
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(10), LocalDateTime.now());
        Subtask addedSubtask = manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + addedSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(0, subtasks.size(), "Подзадача должна быть удалена");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic", Status.NEW);
        Epic addedEpic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", Status.NEW, addedEpic.getId(),
                Duration.ofMinutes(10), LocalDateTime.now());
        Subtask addedSubtask = manager.addSubtask(subtask);


        addedSubtask.setName("Updated Subtask");
        addedSubtask.setStatus(Status.IN_PROGRESS);
        String subtaskJson = gson.toJson(addedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Subtask updatedSubtask = manager.getSubtaskById(addedSubtask.getId());
        assertEquals("Updated Subtask", updatedSubtask.getName());
        assertEquals(Status.IN_PROGRESS, updatedSubtask.getStatus());
    }
}