package manager.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.http.HttpTaskServer;
import manager.task.TaskManager;
import manager.task.memory.NotFoundException;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
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
            if (path.equals("/epics")) {
                List<Epic> epics = taskManager.getEpics();
                String response = gson.toJson(epics);
                sendText(exchange, response);
            } else if (path.startsWith("/epics/") && path.endsWith("/subtasks")) {
                String pathWithoutSubtasks = path.substring(0, path.lastIndexOf("/subtasks"));
                String idStr = pathWithoutSubtasks.substring("/epics/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        List<Subtask> subtasks = taskManager.getSubtasksByEpic(epic);
                        String response = gson.toJson(subtasks);
                        sendText(exchange, response);
                    }
                } catch (NumberFormatException e) {
                    sendNotFound(exchange);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
            } else if (path.startsWith("/epics/")) {
                String idStr = path.substring("/epics/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        String response = gson.toJson(epic);
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
            Epic epic = gson.fromJson(body, Epic.class);

            Epic createdEpic = taskManager.addEpic(epic);
            String response = gson.toJson(createdEpic);
            sendCreated(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        try {
            if (path.startsWith("/epics/")) {
                String idStr = path.substring("/epics/".length());
                try {
                    int id = Integer.parseInt(idStr);
                    taskManager.removeEpic(id);
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