import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskParser {


    public List<Task> parseTasks(String content) {
        List<Task> taskList = new ArrayList<>();

        if (content == null || content.trim().length() < 2) {
            return taskList;
        }

        String innerContent = content.trim();
        if (innerContent.startsWith("[") && innerContent.endsWith("]")) {
            innerContent = innerContent.substring(1, innerContent.length() - 1).trim();
        }

        if (innerContent.isEmpty()) return taskList;

        List<String> jsonObjects = splitJsonObjects(innerContent);

        for (String jsonObject : jsonObjects) {
            if (!jsonObject.startsWith("{")) jsonObject = "{" + jsonObject;
            if (!jsonObject.endsWith("}")) jsonObject = jsonObject + "}";

            Task task = parseSingleTask(jsonObject);
            if (task != null) {
                taskList.add(task);
            }
        }

        return taskList;
    }



    private Task parseSingleTask(String json) {
        try {
            Task task = new Task();

            String idStr = extractValue(json, "id");
            task.setId(Integer.parseInt(idStr));

            task.setDescription(extractValue(json, "description"));
            task.setStatus(extractValue(json, "status"));

            String createdAtStr = extractValue(json, "createdAt");
            if (createdAtStr != null && !createdAtStr.isEmpty()) {
                task.setCreatedAt(LocalDate.parse(createdAtStr));
            }

            String updatedAtStr = extractValue(json, "updatedAt");
            if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
                task.setUpdatedAt(LocalDate.parse(updatedAtStr));
            }

            List<String> notes = extractArray(json, "notes");
            task.setNotes(notes);

            return task;
        } catch (Exception e) {
            System.err.println("Error parsing task: " + e.getMessage());
            return null;
        }
    }


    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);

        if (startIndex == -1) return null;

        startIndex += searchKey.length();


        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\n')) {
            startIndex++;
        }

        char firstChar = json.charAt(startIndex);


        if (firstChar == '"') {
            startIndex++;
            StringBuilder value = new StringBuilder();
            boolean isEscaped = false;

            for (int i = startIndex; i < json.length(); i++) {
                char c = json.charAt(i);

                if (isEscaped) {
                    value.append(c);
                    isEscaped = false;
                } else {
                    if (c == '\\') {
                        isEscaped = true;
                    } else if (c == '"') {

                        return value.toString();
                    } else {
                        value.append(c);
                    }
                }
            }
        }

        else {
            int endIndex = startIndex;
            while (endIndex < json.length()) {
                char c = json.charAt(endIndex);
                // القيمة بتخلص لو لقينا فاصلة , أو قفلة القوس }
                if (c == ',' || c == '}') {
                    return json.substring(startIndex, endIndex).trim();
                }
                endIndex++;
            }
        }

        return null;
    }

    private List<String> extractArray(String json, String key) {
        List<String> list = new ArrayList<>();
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);

        if (startIndex == -1) return list;

        startIndex += searchKey.length();
        int arrayStart = json.indexOf("[", startIndex);
        int arrayEnd = json.indexOf("]", arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) return list;

        String arrayContent = json.substring(arrayStart + 1, arrayEnd).trim();

        if (arrayContent.isEmpty()) return list;


        boolean insideQuote = false;
        StringBuilder currentItem = new StringBuilder();

        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);

            if (c == '"') {
                insideQuote = !insideQuote;
            } else if (c == ',' && !insideQuote) {

                if (currentItem.length() > 0) {
                    list.add(currentItem.toString().trim());
                    currentItem = new StringBuilder();
                }
            } else {
                currentItem.append(c);
            }
        }

        if (currentItem.length() > 0) {
            list.add(currentItem.toString().trim());
        }

        return list;
    }


    private List<String> splitJsonObjects(String innerContent) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int startIndex = 0;

        for (int i = 0; i < innerContent.length(); i++) {
            char c = innerContent.charAt(i);

            if (c == '{') {
                if (braceCount == 0) startIndex = i + 1;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // نهاية أوبجيكت
                    objects.add(innerContent.substring(startIndex, i));
                }
            }
        }
        return objects;
    }


    public String serializeTasks(List<Task> tasks) {
        if (tasks.isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": ").append(task.getId()).append(",\n");
            sb.append("    \"description\": \"").append(escapeString(task.getDescription())).append("\",\n");
            sb.append("    \"status\": \"").append(task.getStatus()).append("\",\n");
            sb.append("    \"createdAt\": \"").append(task.getCreatedAt()).append("\",\n");
            sb.append("    \"updatedAt\": \"").append(task.getUpdatedAt()).append("\",\n");

            sb.append("    \"notes\": [");
            List<String> notes = task.getNotes();
            for (int j = 0; j < notes.size(); j++) {
                sb.append("\"").append(escapeString(notes.get(j))).append("\"");
                if (j < notes.size() - 1) sb.append(", ");
            }
            sb.append("]\n");

            sb.append("  }");
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }


    private String escapeString(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}