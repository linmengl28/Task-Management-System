package task.manager.javenger;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Task is a class representing each task needed to be organized in the Task Manager
 */
public class Task implements Comparable<Task>{
    private int id;
    private String text;
    private boolean completed;
    private LocalDate due;
    private Priority priority;
    private String category;

    private static final String CSV_SEPARATOR = ",";
    private static final String REPLACEMENT_CHARACTER = "�";
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/dd/MM");

    /**
     * Constructs a new Task instance with its information.
     *
     * @param id The ID of the task.
     * @param text a description of the task to be done. This field is required.
     * @param completed The completion status of the task. If not specified, this field should be false by default. 
     * @param due The due date of the task.This field is optional.
     * @param priority The priority of the task. This field is optional. If no priority is specified, the Task can be treated as LOW.
     * @param category  a user-specified String that can be used to group related Tasks
     */
    public Task(int id, String text, boolean completed, LocalDate due, Priority priority, String category) {
        setId(id);
        setText(text);
        this.completed = completed;
        setDue(due);
        setPriority(priority);
        setCategory(category);
    }

    /**
     * Constructs a new Task instance with the requried field, text description. Other attributes is default or null.
     *
     * @param text The text description of the task.
     */
    public Task(String text) {
        setText(text);
        this.completed = false;
        this.priority = Priority.LOW;
    }

    /**
     * Setter of id and catch the exception of invalid id
     * @param id
     */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive value.");
        }
        this.id = id;
    }

    /**
     * Sets the task's text description.
     *
     * @param text The text to be set. 
     * @throws IllegalArgumentException if setted text is null or empty.
     */
    public void setText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty,it is required");
        }
        this.text = text.replace(REPLACEMENT_CHARACTER, ",");
    }

    /**
     * Sets the due date of the task.
     *
     * @param due the due date to be set and it is a LocalDate object. It can be null, meaning there is no deadline in this task.
     */
    public void setDue(LocalDate due) { 
        this.due = due;
    }

    /**
     * Sets the priority level of the task.
     *
     * @param priority The priority level to be set. it is setted as LOW if null.
     */
    public void setPriority(Priority priority) {
        this.priority = (priority != null) ? priority : Priority.LOW;
    }

    /**
     * Sets the category of the task.
     *
     * @param category The category name to be setted. it can be null or empty, meaning the task is uncategorized.
     */
    public void setCategory(String category) {  
        this.category = (category == null || category.trim().isEmpty()) ? null : category;
    }

    /**
     * Marks and changes the task's completion status.
     *
     * @param completed mark and change the task as completed since it is immutable. it only canbe true or false.
     */
    public void markCompleted(boolean completed) { 
        this.completed = completed;
    }

    /**
     * Gets the ID of the task.
     *
     * @return the ID of the task.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the text of the task.
     *
     * @return the text of the task.
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the completion status of the task.
     *
     * @return the the completion status of the task.
     */
    public boolean isCompleted() {
        return completed;
    }
   
    /**
     * Gets the due date of the task.
     *
     * @return the due date of the task.
     */
    public LocalDate getDue() {
        return due;
    }

    /**
     * Gets the due date of the task.
     *
     * @return the due date of the task.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Gets the category of the task.
     *
     * @return the category of the task.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Constructor to create a Task from a CSV line.
     * 
     * @param line the String Line is each line of the csv file.
     * @return new task creating from csvline.
     */
    public static Task readFromCsv(String line){
        String[] elements = line.split(CSV_SEPARATOR, -1);

        int id = Integer.parseInt(elements[0]); //string -> int

        String text = elements[1].replace(REPLACEMENT_CHARACTER, ","); 

        boolean completed = false;
        if (!elements[2].isEmpty()) {
            completed = Boolean.parseBoolean(elements[2]);
        }

        LocalDate due = null;
        if (!elements[3].isEmpty()) {
            try {
                due = LocalDate.parse(elements[3], CSV_DATE_FORMAT); 
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        Priority priority;
        try {
            priority = Priority.valueOf(elements[4].isEmpty() ? "LOW" : elements[4].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown priority '" + elements[4] + "' for task ID " + id + ", setting to LOW.");
            priority = Priority.LOW;  
        }

        String category = null;
        if (!elements[5].isEmpty()){
            category = elements[5];
        }

        return new Task(id, text, completed, due, priority, category);
    }

    /**
     * Transfers and joins attributes to csvline format.
     * 
     * @return A string in CSV line format. It considers the null or empty condition.
     */
    public String toCSVLine() {
        String formattedDue = (due != null) ? due.format(CSV_DATE_FORMAT) : "";
        return id + CSV_SEPARATOR + text.replace(",", REPLACEMENT_CHARACTER) + CSV_SEPARATOR +
                completed + CSV_SEPARATOR + formattedDue + CSV_SEPARATOR +
                (priority != null ? priority : "") + CSV_SEPARATOR + (category != null ? category : "");
    }

    /**
     * Overrides the toString method to print a task object.
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", completed=" + completed +
                ", due='" + due + '\'' +
                ", priority='" + priority + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    /**
     * Compares two task objects
     * 
     * @param other the object to be compared.
     * @return 1 if this task is bigger and vice versa.
     */
    @Override
    public int compareTo(Task other) {
        // Implement your sorting logic here; example by due date, then by priority
        if (this.due != null && other.due != null) {
            int dateComparison = this.due.compareTo(other.due);
            if (dateComparison != 0) {
                return dateComparison;
            }
        } else if (this.due != null) {
            return -1;
        } else if (other.due != null) {
            return 1;
        }
        return this.priority.compareTo(other.priority);
    }

}