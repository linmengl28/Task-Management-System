package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskManager {

    private static final String[] HEADER = { "id", "text", "completed", "due", "priority", "category" };
    private static final String CSV_DELIMITER = ",";
    private static final String REPLACEMENT_CHAR = "�";
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/dd/MM");
    private List<Task> tasks;
    private Path csvPath;

    public TaskManager(String csvFile) {
        tasks = new ArrayList<>();
        this.csvPath = Paths.get(csvFile); //需要寻找位于项目的根目录的文件名为sampledata.csv的文件
        loadTasksFromCSV();
    }

    //addtask() 只通过传入text参数先建立好一个一个task，后面的参数等待扫描到的时候再通过set去添加
    public void addTask(String text) { //addTask在将Task加入tasks时需要updateCSV
        Task newTask = new Task(text);
        int id = generateNewId();
        newTask.setId(id);
        tasks.add(newTask);
        updateCSV();
    }

    // 这里也提供了正常的addTask()
    public void addTask(String text, boolean completed, LocalDate due, Priority priority, String category) {
        int id = generateNewId();
        Task newTask = new Task(id,text,completed,due,priority,category);
        tasks.add(newTask);
        updateCSV();
    }

    /**
     * 对tasks列表添加csv文件中所有line生成的task对象
     */
    public void loadTasksFromCSV() { //需要在tasks上进行add操作
        tasks.clear();
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String headerLine = br.readLine();
            if (headerLine == null) { // 如果文件为空，则不往tasks中加入任何task实例
                return;
            }
            String line;
            while ((line = br.readLine()) != null) {
                Task task = Task.readFromCsv(line);
                if (task != null) { // 因为解析错误返回null将被忽略，双重确保在line不为空的情况下，fromCsv没有返回null,这里应该需要去考虑各种可能的不匹配解析错误的异常，后期需要再增加
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将目前的updated tasks中的task object toCVline 写入到文件中
     */
    protected void updateCSV() {
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(String.join(CSV_DELIMITER, HEADER));
            bw.newLine();
            for (Task task : tasks) {
                bw.write(task.toCSVLine());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Complete an existing Task. The user set the completed status of an existing Task to true.
    //When the Completed(showing status is changed, the CSV file should be updated.
    //commandlinex需要通过ID来将相应ID的task的completed,

    //这里有一个问题，如果没有找到id，什么都不会发生
    public void completeTask(int id) {
        Task task = findTaskById(id);
        if (task == null) {
            // 如果未找到任务，抛出异常
            throw new IllegalArgumentException("Task ID " + id + " does not exist.");
        }
        // 标记任务为已完成
        task.markCompleted(true);
        // 更新 CSV 文件
        updateCSV();
    }

    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    // sorted,都不会在原List<Tasks>上进行任何排序， ID会不会发生变化
    public int generateNewId() {
        int maxId = getCurrentMaxID();
        int newId = maxId + 1;
        return newId;
    }

    //以下Helper函数是可能会需要使用到的API：（先创建着后期会删掉，现在不确定会不会用到或者需不需要）
    /**
     * 获得目前ID的指针，也就是目前最大的ID value
     * @return
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
     * Getter method of List<Task> tasks
     * @return tasks列表，存有所有行生成的task对象
     */
    public List<Task> getTasks() {
        return tasks;
    }


    public void displayTasks(Boolean showIncomplete, String showCategory, Boolean sortByDate, Boolean sortByPriority) {
        // 创建一个新的列表来存储过滤后的任务
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : tasks) {
            boolean isMatched = true;
            // showIncomplete = True只显示不完整的内容,如果isCompleted=true就不符合筛选要求
            if (showIncomplete != null && showIncomplete && task.isCompleted()) {
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
            throw new IllegalArgumentException("Cannot sort by both date and priority simultaneously.");
        }
        if (sortByDate != null && sortByDate) {
            Collections.sort(filteredTasks, Comparator.comparing(Task::getDue, Comparator.nullsLast(Comparator.naturalOrder()))); //设置将所有
        } else if (sortByPriority != null && sortByPriority) {
            Collections.sort(filteredTasks, Comparator.comparing(Task::getPriority));
        }
        for (Task task : filteredTasks) {
            System.out.println(task);
        }
    }

    /**
     * Gets a new sorted list by due date based on tasks, but tasks itself is not changed after calling this method
     *
     * @param comparator
     * @return a new sorted list<Task> by due date
     */
    public List<Task> getSortedTasks(Comparator<Task> comparator) {
        List<Task> sortedTasks = new ArrayList<>(tasks); // 创建任务列表的副本
        Collections.sort(sortedTasks, comparator); // 使用Collections.sort进行排序
        return sortedTasks;
    }

    /**
     * Filters tasks by completion status
     * @param completed
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
     * 通过传入的category去筛选留下属于该类的task对象，并将这些符合筛选体哦见
     * @param category
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

    public void setCsvFilePath(String path) {
        this.csvPath = Paths.get(path);
    }
}