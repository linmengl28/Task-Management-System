package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


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

    //这里有可能需要将localdate直接变为常用格式的due
    //因为new task 完全有特殊情况暴露的风险，不能传入
    //这里的id应该是传入不了的,这里的
    public Task(int id, String text, boolean completed, LocalDate due, Priority priority, String category) {
        setId(id);
        setText(text);
        this.completed = completed;
        setDue(due);
        setPriority(priority);
        setCategory(category);
    }
    /**
     * 'due' 和 'category' 不设置为空字段，默认为 null
     * @param text
     */
    public Task(String text) {
        setText(text);
        this.completed = false;
        this.priority = Priority.LOW;
    }

    //constructor+set属性，但是这样子就不能属性设置为final了，javaBeans模式
    //这里set属性需不需要抛出异常 例如id不能小于《=0 duedate的格式不对 prority出现了
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive value.");
        }
        this.id = id;
    }

    // can text be
    public void setText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty,it is required");
        }
        this.text = text.replace(REPLACEMENT_CHARACTER, ",");
    }

    public void setDue(LocalDate due) { //due可以为null置空，代表这个任务没有截止日期
        this.due = due;
    }

    public void setPriority(Priority priority) { //如果没有指定优先级，则Task可以视为LOW
        this.priority = (priority != null) ? priority : Priority.LOW;
    }

    public void setCategory(String category) {  //如果该字段为空，则存储null在Task对象中，表示尚未分配类别。
        this.category = (category == null || category.trim().isEmpty()) ? null : category;
    }


    public void markCompleted(boolean completed) { //completed 不可能为空
        this.completed = completed;
    }



    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    //complete status iscompleted是否可以有

    public LocalDate getDue() {
        return due;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }



    /**
     * Constructor to create a Task from a CSV line
     * @param line String Line为csv文件中的每一行，读入为为一整个String
     * @return new task creating
     */
    //如果读取时为空，说明
    public static Task readFromCsv(String line){ //line是每一行的string（text中含有� -> String[] -> String->id, text, completed, due, priority, category, return new task()
        String[] elements = line.split(CSV_SEPARATOR, -1); //这里有可能会有问题，按理来说-1：最后的空字符串也会被读入进String[]中

        int id = Integer.parseInt(elements[0]); //string -> int

        String text = elements[1].replace(REPLACEMENT_CHARACTER, ","); //呈现给user需要替换字符为“,”

        boolean completed = false;
        if (!elements[2].isEmpty()) {
            completed = Boolean.parseBoolean(elements[2]);
        }

        LocalDate due = null;
        if (!elements[3].isEmpty()) {
            try {
                due = LocalDate.parse(elements[3], CSV_DATE_FORMAT); //parse string to localDate variable String"yyyy/dd/MM" ->LocalDate("yyyy-MM-dd")
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        Priority priority;
        try {
            priority = Priority.valueOf(elements[4].isEmpty() ? "LOW" : elements[4].toUpperCase());//String -> Priority
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown priority '" + elements[4] + "' for task ID " + id + ", setting to LOW.");
            priority = Priority.LOW;  // Set to default value when encountering unknown Priority values
        }

        String category = null;
        if (!elements[5].isEmpty()){
            category = elements[5];
        }

        return new Task(id, text, completed, due, priority, category);
    }

    /**
     * Transfers and joins attributes to csvline format
     * @return
     */
    //为了读入读出的一致性以及老师提供的file，为了以防万一，我们在读入的时候按照"YYYY/dd/MM"去操作，但是在due的处理时用YYYY-MM-dd
    public String toCSVLine() {
        String formattedDue = (due != null) ? due.format(CSV_DATE_FORMAT) : "";
        return id + CSV_SEPARATOR + text.replace(",", REPLACEMENT_CHARACTER) + CSV_SEPARATOR +
                completed + CSV_SEPARATOR + formattedDue + CSV_SEPARATOR +
                (priority != null ? priority : "") + CSV_SEPARATOR + (category != null ? category : "");
    }
    //也能够安全地将其读入为一个空字符串而不是 null 字符串。

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
     *
     * @param other the object to be compared.
     * @return
     */
    //那本身是否树
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