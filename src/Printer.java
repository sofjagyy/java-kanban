public class Printer {

    public void printTask(Task task) {
        System.out.println("Задача №" + task.getId() + "\n" +
                "Название: " + task.getName() + "\n" +
                "Описание: " + task.getDescription() + "\n" +
                "Статус: " + task.getStatus());
    }

    public void printEpicTask(EpicTask epicTask) {
        System.out.println("Epic-Задача №" + epicTask.getId() + "\n" +
                "Название: " + epicTask.getName() + "\n" );
        for (Task task : epicTask.getTasks()) {
            printTask(task);
        }
    }

}
