package task.manager.javenger;


import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.LocalDate;



class TaskManagerTest {

    private static Path testCsvPath;
    private TaskManager taskManager;

    @BeforeAll
    static void setup() throws IOException {
        testCsvPath = Files.createTempFile("testTasks", ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(testCsvPath)) {
            writer.write(String.join(",", "id", "text", "completed", "due", "priority", "category"));
            writer.newLine();
            writer.write("1,the first task now,false,2024/31/01,MEDIUM,Preme");
            writer.newLine();
            writer.write("2,the second task now,true,2024/02/02,HIGH,Grocery");
            writer.newLine();
        }
    }

    @BeforeEach
    void init() {
        taskManager = new TaskManager(testCsvPath.toString());
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(testCsvPath); 
    }

    @Test
    void testAddTask() {
        taskManager.addTask("New Task", false, LocalDate.now(), Priority.LOW, "Storage");

        List<Task> tasks = taskManager.getTasks();

        assertEquals(3, tasks.size());

        try {
            List<String> allLines = Files.readAllLines(testCsvPath);
            assertTrue(allLines.get(allLines.size() - 1).contains("New Task"));
        } catch (IOException e) {
            fail("Please check, IOException occurred while reading tasks from CSV file: " + e.getMessage());
        }

    }

    @Test
    void testAddTaskAndCsvUpdate() throws IOException {
        taskManager.addTask("need to fix bugs");
        List<Task> tasks = taskManager.getTasks();

        assertEquals(4, tasks.size());

        List<String> allLines = Files.readAllLines(testCsvPath);
        assertTrue(allLines.get(allLines.size() - 1).contains("need to fix bugs"));

        Task firstTask = tasks.get(0);
        assertEquals("the first task now", firstTask.getText());
        assertFalse(firstTask.isCompleted());

    }

    @Test
    void testLoadTasksFromEmptyCSV() throws IOException {
        Path newtestCsvPath = Files.createTempFile("nothing", ".csv");
        TaskManager newtaskManager = new TaskManager(newtestCsvPath.toString());

        assertTrue(newtaskManager.getTasks().isEmpty());

        Files.deleteIfExists(newtestCsvPath); 
    }


    @Test
    void testCompleteTaskAndCsvUpdate() throws IOException {
        taskManager.completeTask(1);
        Task completedTask = null;
        for (Task task : taskManager.getTasks()) {
            if (task.getId() == 1) {
                completedTask = task;
                break;
            }
        }

        assertNotNull(completedTask);
        assertTrue(completedTask.isCompleted());

        List<String> allLines = Files.readAllLines(testCsvPath);
        boolean foundCompletedTaskInCSV = false;
        for (String line : allLines) {
            if (line.contains("1,the first task now,true")) {
                foundCompletedTaskInCSV = true;
                break;
            }
        }
        assertTrue(foundCompletedTaskInCSV);
    }

    @Test
    void testCompleteTaskThrowsExceptionForUnknownId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.completeTask(100);
        });

        String expectedMessage = "Task ID 100 does not exist.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDisplayTasks() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        taskManager.displayTasks(true, null, false, false);
        assertTrue(outContent.toString().contains("the first task now"));
        assertFalse(outContent.toString().contains("the second task now"));
        outContent.reset();

        taskManager.displayTasks(false, "Preme", false, false);
        assertTrue(outContent.toString().contains("the first task now"));
        assertFalse(outContent.toString().contains("the second task now"));
        outContent.reset();

        taskManager.displayTasks(false, null, true, false);
        assertTrue(outContent.toString().contains("the first task now") && outContent.toString().contains("the second task now"));
        assertTrue(outContent.toString().indexOf("the first task now") < outContent.toString().indexOf("the second task now"));
        outContent.reset();

        taskManager.displayTasks(false, null, false, true);
        assertTrue(outContent.toString().contains("the first task now") && outContent.toString().contains("the second task now"));
        assertTrue(outContent.toString().indexOf("the first task now") < outContent.toString().indexOf("the second task now"));
        outContent.reset();

        System.setOut(originalOut);
    }

    @Test
    void testDisplayTasksSortByDateAndPriorityThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.displayTasks(false, null, true, true);
        });
        String expectedMessage = "A Task cannot sort by both date and priority simultaneously.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "A Task cannot sort by both date and priority simultaneously.");
    }

        @Test
    void testGetTasksByCompletion() {
        // Assuming there's a method getTasks() to fetch all tasks for testing
        List<Task> allTasks = taskManager.getTasks();
        assertEquals(2, allTasks.size()); // Check initial number of tasks loaded from CSV

        // Test for completed tasks
        List<Task> completedTasks = taskManager.getTasksByCompletion(true);
        assertEquals(1, completedTasks.size());
        assertTrue(completedTasks.get(0).isCompleted());

        // Test for incomplete tasks
        List<Task> incompleteTasks = taskManager.getTasksByCompletion(false);
        assertEquals(1, incompleteTasks.size());
        assertFalse(incompleteTasks.get(0).isCompleted());
    }

    @Test
    void testGetTasksByCategory() {
        // Assuming there's a method getTasks() to fetch all tasks for testing
        List<Task> allTasks = taskManager.getTasks();
        assertEquals(4, allTasks.size()); // Check initial number of tasks loaded from CSV

        // Test for a specific category
        List<Task> categoryTasks = taskManager.getTasksByCategory("Grocery");
        assertEquals(1, categoryTasks.size());
        assertEquals("Grocery", categoryTasks.get(0).getCategory());

        // Test for a category that does not exist
        List<Task> noCategoryTasks = taskManager.getTasksByCategory("NonExistent");
        assertTrue(noCategoryTasks.isEmpty());
    }




}