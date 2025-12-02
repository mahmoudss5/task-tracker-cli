import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoHandler {
    private Path path;
    private List<Task> tasks;
    private TaskParser taskParser = new TaskParser();

    public TodoHandler(Path path) {
        this.path = path;
        readFile();
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
    }

    private void readFile() {
        String fileContent = "";
        try {
            if (Files.exists(path)) {
                fileContent = Files.readString(path);
            } else {
                System.out.println("File not found, a new file will be created upon saving tasks.");
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileContent == null || fileContent.trim().isEmpty()) {
            this.tasks = new ArrayList<>();
        } else {
            this.tasks = taskParser.parseTasks(fileContent);
        }
    }

    private void updateFile() {
        String fileContent = taskParser.serializeTasks(tasks);
        try {
            Files.writeString(path, fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTask(String taskDescription) {
        Task task = new Task();
        int id = 1;
        if (!tasks.isEmpty()) {
            id = tasks.get(tasks.size() - 1).getId() + 1;
        }

        LocalDate currentDate = LocalDate.now();
        task.setDescription(taskDescription);
        task.setStatus("todo");
        task.setId(id);
        task.setCreatedAt(currentDate);
        task.setUpdatedAt(currentDate);
        tasks.add(task);
        updateFile();
        System.out.println("Task added successfully (ID: " + id + ")");
    }

    public void getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
    }

    public void getAllTasksDone() {
        boolean found = false;
        for (Task task : tasks) {
            if ("done".equalsIgnoreCase(task.getStatus())) {
                System.out.println(task.toString());
                found = true;
            }
        }
        if (!found) System.out.println("No completed tasks found.");
    }

    public void makeTaskDone(int targetId) {
        Task task = findTaskById(targetId);
        if (task != null) {
            task.setStatus("done");
            task.setUpdatedAt(LocalDate.now());
            updateFile();
            System.out.println("Task " + targetId + " marked as done.");
        } else {
            System.out.println("Task with ID " + targetId + " not found.");
        }
    }

    public void removeTask(int targetId) {
        boolean removed = tasks.removeIf(task -> task.getId() == targetId);
        if (removed) {
            updateFile();
            System.out.println("Task " + targetId + " deleted successfully.");
        } else {
            System.out.println("Task with ID " + targetId + " not found.");
        }
    }

    public void addNoteToTask(int targetId, String noteDescription) {
        Task task = findTaskById(targetId);
        System.out.println("Adding note to task " + targetId);
        if (task != null) {
            task.addNote(noteDescription);
            task.setUpdatedAt(LocalDate.now());
            updateFile();
            System.out.println("Note added to task " + targetId);
        } else {
            System.out.println("Task with ID " + targetId + " not found.");
        }
    }

    public void getNotesOfTask(int targetId) {
        Task task = findTaskById(targetId);

        if (task == null) {
            System.out.println("Task with ID " + targetId + " not found.");
            return;
        }

        List<String> notes = task.getNotes();
        if (notes == null || notes.isEmpty()) {
            System.out.println("No notes found for task " + targetId);
        } else {
            System.out.println("Notes for Task " + targetId + ":");
            for (String note : notes) {
                System.out.println("- " + note);
            }
        }
    }

    private Task findTaskById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}