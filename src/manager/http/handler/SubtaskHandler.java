package manager.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.http.HttpTaskServer;
import manager.task.TaskManager;
import manager.task.memory.NotFoundException;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
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
            if (path.equals("/subtasks")) {
                List<Subtask> subtasks = taskManager.getSubtasks();
                String response = gson.toJson(subtasks);
                sendText(exchange, response);
            } else if (path.startsWith("/subtasks/")) {
                String idStr = path.substring("/subtasks/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                    } else {
                        String response = gson.toJson(subtask);
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
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getId() == null) {
                Subtask createdSubtask = taskManager.addSubtask(subtask);
                String response = gson.toJson(createdSubtask);
                sendCreated(exchange, response);
            } else {
                taskManager.updateSubtask(subtask);
                String response = gson.toJson(subtask);
                sendCreated(exchange, response);
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace(); // для отладки
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.startsWith("/subtasks/")) {
                String idStr = path.substring("/subtasks/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    taskManager.removeSubtask(id);
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