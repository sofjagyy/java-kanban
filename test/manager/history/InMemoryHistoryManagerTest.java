package manager.history;

import manager.task.TaskManager;
import manager.Managers;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/// Я не смогла найти тебя в пачке, поэтому пишу здесь, меня можно найти так @sofjagy
/// Все твои замечания считаю правильными и полезными, и обязательно реализую все что ты сказал, но не сейчас.
/// Я прекрасно понимаю что то что я написала это говнище, и что код должен быть читабельным и поддерживаемым,
/// но если я сейчас это начну его рефакторить, оно посыпется как гнилой забор, и я не успею сдать проект к понедельнику (жесткий дедлайн уже прошел).
/// Все красные замечания исправлю, остальное - к финальному проекту 6 спринта, прошу понять и простить, я забила на этот курс из-за работы и истратила все переходы.

class InMemoryHistoryManagerTest {
        @Test
        void addTask_getTask_noDifferenceAfterChanges() {
            Task task = new Task("Первоначальное название", "Первоначальное описание", Status.NEW);
            task.setId(1);
            HistoryManager historyManager = Managers.getDefaultHistory();
            historyManager.add(task);
            //Запомнили оригинал
            String originalName = task.getName();
            String originalDescription = task.getDescription();
            Status originalStatus = task.getStatus();
            //Поменяли
            task.setName("Измененное название");
            task.setDescription("Измененное описание");
            task.setStatus(Status.IN_PROGRESS);

            Task taskFromHistory = historyManager.getHistory().getFirst();

            assertNotEquals(originalName, task.getName(), "Имя изменилось");
            assertNotEquals(originalDescription,task.getDescription(), "Описание изменилось");
            assertNotEquals(originalStatus, task.getStatus(), "Статус изменился");

            assertEquals(originalName, taskFromHistory.getName(), "Название не изменилось");
            assertEquals(originalDescription, taskFromHistory.getDescription(), "Описание не изменилось");
            assertEquals(originalStatus, taskFromHistory.getStatus(), "Статус не изменился");

            assertNotSame(task, taskFromHistory, "Должны быть разные");
        }
}
