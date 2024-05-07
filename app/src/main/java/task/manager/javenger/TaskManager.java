package task.manager.javenger;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * TaskManager is a class to manipulate and manager the tasks by organizing them into a list.
 */
public class TaskManager {

    private static final String[] HEADER = { "id", "text", "completed", "due", "priority", "category" };
    private static final String CSV_DELIMITER = ",";
    private static final String REPLACEMENT_CHAR = "�";
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/dd/MM");
    private List<Task> tasks;
    private Path csvPath;

    /**
     * Constructs a new TaskManager insatnce and loads tasks from the specified CSV file with its csvpath.
     *
     * @param csvFile The path to the CSV file.
     */
    public TaskManager(String csvFile) {
        tasks = new ArrayList<>();
        this.csvPath = Paths.get(csvFile); 
        loadTasksFromCSV();
    }

    /**
     * Adds a new task with the given text description and updates to the CSV file.
     * Text
     * @param text The text description of the new task.
     */
    public void addTask(String text) { 
        Task newTask = new Task(text);
        int id = generateNewId();
        newTask.setId(id);
        tasks.add(newTask);
        updateCSV();
    }

    /**
     * Adds a new task with all information the task and updates to the CSV file.
     *
     * @param text a description of the task to be done. This field is required.
     * @param completed The completion status of the task. If not specified, this field should be false by default. 
     * @param due The due date of the task.This field is optional.
     * @param priority The priority of the task. This field is optional. If no priority is specified, the Task can be treated as LOW.
     * @param category  a user-specified String that can be used to group related Tasks
     */
    public void addTask(String text, boolean completed, LocalDate due, Priority priority, String category) {
        int id = generateNewId();
        Task newTask = new Task(id,text,completed,due,priority,category);
        tasks.add(newTask);
        updateCSV();
    }
    
    /**
     * Reads all tasks from the CSV file into the organized task list. all tasks are cleared before loading.
     */
    public void loadTasksFromCSV() { 
        tasks.clear();
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String headerLine = br.readLine();
            if (headerLine == null) { 
                return;
            }
            String line;
            while ((line = br.readLine()) != null) {
                Task task = Task.readFromCsv(line);
                if (task != null) { 
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while loading tasks from the CSV file: " + e.getMessage()+ ",Please check the file path and permissions");
            e.printStackTrace();
        }
    }

    /**
     * Writes the updated tasks into CSV file
     */
    public void updateCSV() {
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(String.join(CSV_DELIMITER, HEADER));
            bw.newLine();
            for (Task task : tasks) {
                bw.write(task.toCSVLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("An error occurred while updating the CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Marks the complettion status task of its ID and evertime updates the CSV file.
     *
     * @param id The ID of the task 
     * @throws IllegalArgumentException if a task does not exist.
     */
    public void completeTask(int id) {
        Task task = findTaskById(id);
        if (task == null) {
            throw new IllegalArgumentException("Task ID " + id + " does not exist.");
        }
        task.markCompleted(true);

        updateCSV();
    }

    /**
     * Finds and returns a task by its ID.
     *
     * @param id The ID of the task to find.
     * @return The found task, or null if no task with the given ID exists.
     */
    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    /**
     * Generates a new unique ID for a task, which is just one greater than the current ID.
     *
     * @return The newly generated unique ID.
     */  
    public int generateNewId() {
        int maxId = getCurrentMaxID();
        int newId = maxId + 1;
        return newId;
    }

    /**.
     * Gets current largest ID
     * 
     * @return current largest ID.
     */
    public int getCurrentMaxID(){
        int maxId = 0;
        for (Task task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        return maxId;
    }

    /**
     * Getter method of List<Task> tasks.
     * 
     * @return List<Task> tasks.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Displays and filters tasks based by each attribute.
     *
     * @param showIncomplete If true, filter the list to only include incomplete Task.
     * @param showCategory If true, Filter the list to only include Tasks with a particular category.
     * @param sortByDate If true, Sort the Tasks by date (ascending).
     * @param sortByPriority If true, Sort the Tasks by priority (ascending).
     * @throws IllegalArgumentException if both sortByDate and sortByPriority are true.
     */
    public void displayTasks(Boolean showIncomplete, String showCategory, Boolean sortByDate, Boolean sortByPriority) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            boolean isMatched = true;
            if (showIncomplete && task.isCompleted()) {
                isMatched = false;
            }
            if (showCategory != null && !showCategory.isEmpty() && !showCategory.equals(task.getCategory())) {
                isMatched = false;
            }
            if (isMatched) {
                filteredTasks.add(task);
            }
        }
        if (sortByDate == true && sortByPriority ==true) {
            throw new IllegalArgumentException("A Task cannot sort by both date and priority simultaneously.");
        }
        if (sortByDate != null && sortByDate) {
            Collections.sort(filteredTasks, Comparator.comparing(Task::getDue, Comparator.nullsLast(Comparator.naturalOrder()))); 
        } else if (sortByPriority != null && sortByPriority) {
            Collections.sort(filteredTasks, Comparator.comparing(Task::getPriority));
        }
        for (Task task : filteredTasks) {
            System.out.println(task);
        }
    }

    /**
     * Filters tasks by completion status 
     * @param completed the completion status
     * @return 
     */
    public List<Task> getTasksByCompletion(boolean completed) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted() == completed) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    /**
     * Gets the tasks by the category 
     * @param category the category type
     * @return
     */
    public List<Task> getTasksByCategory(String category) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getCategory() != null &&task.getCategory().equalsIgnoreCase(category)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    /**
     * Sets the filepath
     * 
     * @param path
     */
    public void setCsvFilePath(String path) {
        this.csvPath = Paths.get(path);
    }


    
}



    




