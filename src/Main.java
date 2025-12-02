import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
       if (args.length < 1) {
           System.out.println("Please provide an correct command for to do List from this commands.");
           System.out.println("1. add <task description>");
           System.out.println("2. list");
           System.out.println("2.2 listDone ");
           System.out.println("3. complete <task id>");
           System.out.println("4. delete <task id>");
           System.out.println("5- addNote <note description> to <task id>");
           return;
       }
         String command = args[0];
       TodoHandler todoHandler = new TodoHandler(Path.of("data.json"));

        switch (command) {
            case "add":
                // Code to add a task
                System.out.println("Adding a new task...");
                String taskDescription = args[1];
                todoHandler.addTask(taskDescription);
                break;

            case "list":
                // Code to list tasks
                System.out.println("Listing all tasks...");
                todoHandler.getAllTasks();
                break;

            case "listDone":
                // Code to list completed tasks
                System.out.println("Listing completed tasks...");
                 todoHandler.getAllTasksDone();
                break;
            case "complete":
                // Code to complete a task
                System.out.println("Completing a task...");
                todoHandler.makeTaskDone(Integer.parseInt( args[1]));
                break;
            case "delete":
                // Code to delete a task
                System.out.println("Deleting a task...");
                todoHandler.removeTask(Integer.parseInt( args[1]));
                break;
            case "addNote":
                // Code to add a note to a task
                String noteDescription = args[1];
                int taskId = Integer.parseInt(args[3]);
                System.out.println("Adding a note to a task... "+taskId );
                todoHandler.addNoteToTask(taskId, noteDescription);
                break;

            case "getNotes":
                // Code to get notes of a task
                System.out.println("Getting notes of a task...");
                int tId = Integer.parseInt(args[1]);
                todoHandler.getNotesOfTask(tId);
                break;
            default:
                System.out.println("Unknown command. Please use one of the following commands:");
                System.out.println("1. add <task description>");
                System.out.println("2. list");
                System.out.println("2.2 list done ");
                System.out.println("3. complete <task id>");
                System.out.println("4. delete <task id>");
                System.out.println("5- add note <note description> to <task id>");
        }


    }
}