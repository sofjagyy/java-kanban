package manager.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.http.HttpTaskServer;
import manager.task.TaskManager;
import manager.task.memory.NotFoundException;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (requestMethod) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
                    break;
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.equals("/tasks")) {
                // GET /tasks - получить все задачи
                List<Task> tasks = taskManager.getTasks();
                String response = gson.toJson(tasks);
                sendText(exchange, response);
            } else if (path.startsWith("/tasks/")) {
                // GET /tasks/{id} - получить задачу по id
                String idStr = path.substring("/tasks/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    Task task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String response = gson.toJson(task);
                        sendText(exchange, response);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() == null) {
                Task createdTask = taskManager.addTask(task);
                String response = gson.toJson(createdTask);
                sendCreated(exchange, response);
            } else {
                taskManager.updateTask(task);
                String response = gson.toJson(task);
                sendCreated(exchange, response);
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.startsWith("/tasks/")) {
                String idStr = path.substring("/tasks/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    taskManager.removeTask(id);
                    sendText(exchange, "{}");
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }
}