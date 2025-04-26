import java.util.ArrayList;
import java.util.Scanner;
import java.util.Objects;
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskTracker taskTracker = new TaskTracker();

        Task task1 = new Task("Помыть кота", "Помыть кота в ванной с новым шампунем", taskTracker.getId(), Status.NEW);
        taskTracker.incId();

        Task subtask1 = new Task("Протереть пыль", "тщательно протереть пыль во всей квартире", taskTracker.getId(), Status.NEW);
        taskTracker.incId();

        Task subtask2 = new Task("помыть пол", "тщательно помыть пол во всей квартире", taskTracker.getId(), Status.NEW);

        ArrayList<Task> subTasks = new ArrayList<Task>();
        EpicTask epicTask1 = new EpicTask("Генеральная уборка", subTasks, Status.NEW, taskTracker.getId());
        taskTracker.incId();

        epicTask1.addTask(subtask1);
        epicTask1.addTask(subtask2);

        taskTracker.addTask(task1);
        taskTracker.addEpicTask(epicTask1);
        while(true) {
            printMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    addNewTask(taskTracker);
                    break;
                case "2":
                    addNewEpicTask(taskTracker);
                    break;
                case "3":
                    PrintTasks(taskTracker);
                    break;
                case "4":
                    renewStatus(taskTracker);
                    break;
                case "5":
                    removeTask(taskTracker);
                    break;
                default:
                    System.out.println("Вы ввели неверную команду, попробуйте еще раз!");
                    break;
            }

        }
    }


    private static void printMenu() {
        System.out.println("Вас приветствует Трекер Задач!");
        System.out.println("1 - Добавить новую задачу.");
        System.out.println("2 - Добавить новую Epic-задачу");
        System.out.println("3 - Печать задач в консоли.");
        System.out.println("4 - Обновить статус задачи по ID.");
        System.out.println("5 - Удаление задачи по ID.");
    }

    public static void addNewTask(TaskTracker taskTracker) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите заголовок задачи:");
        String name = scanner.nextLine();
        System.out.println("Добавьте описание задачи:");
        String description = scanner.nextLine();

        Task task = new Task(name, description, taskTracker.getId(), Status.NEW);
        taskTracker.addTask(task);
        taskTracker.incId();
    }

    public static void addNewEpicTask(TaskTracker taskTracker) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите заголовок Epic-задачи:");
        String epicName = scanner.nextLine();
        int epicId = taskTracker.getId();
        taskTracker.incId();
        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            System.out.println("Введите имя подзадачи:");
            String subTaskName = scanner.nextLine();
            System.out.println("Введите описание подзадачи");
            String subTaskDescription = scanner.nextLine();

            int subId = taskTracker.getId();
            taskTracker.incId();

            Task subtask = new Task(subTaskName, subTaskDescription, subId, Status.NEW, epicId);
            tasks.add(subtask);

            System.out.println("Хотите добавить еще одну подзадачу? Введите в консоль да/нет");
            String answer = scanner.nextLine();

            if (!answer.equals("да")) {
                break;
            }
        }

        EpicTask epicTask = new EpicTask(epicName, tasks, Status.NEW, epicId);
        taskTracker.addEpicTask(epicTask);
    }

    public static void PrintTasks(TaskTracker taskTracker) {
        Scanner scanner = new Scanner(System.in);
        Printer printer = new Printer();

        System.out.println("Введите в консоль соответствующую цифру.");
        System.out.println("1. Распечатать все задачи.");
        System.out.println("2. Распечатать все Epic-задачи.");
        System.out.println("3. Распечатать все обычные задачи.");
        System.out.println("4. Распечатать задачу по ID");

        String command = scanner.nextLine();

        switch (command) {
            case "1":
                for(Task task : taskTracker.getTasks()) {
                    printer.printTask(task);
                }

                for(EpicTask epicTask : taskTracker.getEpicTasks()) {
                    printer.printEpicTask(epicTask);
                }
                break;
            case "2":
                for(EpicTask epicTask : taskTracker.getEpicTasks()) {
                    printer.printEpicTask(epicTask);
                }
                break;
            case "3":
                for(Task task : taskTracker.getTasks()) {
                    printer.printTask(task);
                }
                break;
            case "4":
                System.out.println("Введите ID задачи.");
                int id = scanner.nextInt();

                Object task = taskTracker.getTaskById(id);

                if (task.getClass() == Task.class) {
                    printer.printTask((Task) task);
                } else {
                    printer.printEpicTask((EpicTask) task);
                }
                break;
            default:
                break;
        }
    }

    public static void renewStatus(TaskTracker taskTracker) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID задачи.");
        int id = scanner.nextInt();

        Object task = taskTracker.getTaskById(id);

        if (task.getClass() == EpicTask.class) {
            System.out.println("Вы не можете обновлять статус Epic-задачи вручную, сначала выполните все подзадачи!");
        } else {
            System.out.println("Введите в консоль:");
            System.out.println("1. Задать статус В ПРОЦЕССЕ.");
            System.out.println("2. Задать статус ВЫПОЛНЕНО");

            String command = scanner.nextLine();

            if (command.equals("1")) {
                ((Task) task).setStatus(Status.IN_PROGRESS);
                if (((Task) task).getEpicID() != -1) {
                    EpicTask epicTask = (EpicTask) taskTracker.getTaskById(id);
                    epicTask.setStatus(Status.IN_PROGRESS);
                }
            } else {
                ((Task) task).setStatus(Status.DONE);
                if (((Task) task).getEpicID() != -1) {
                    EpicTask epicTask = (EpicTask) taskTracker.getTaskById(id);

                    if (epicTask.isDone()) {
                        epicTask.setStatus(Status.DONE);
                    }
                }
            }
        }
    }

    public static void removeTask(TaskTracker taskTracker) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID задачи.");

        int id = scanner.nextInt();

        taskTracker.deleteTaskById(id);
    }
}
