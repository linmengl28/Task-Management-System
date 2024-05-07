package task.manager.javenger;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {


    @Test
    public void testTaskConstructorAndGetters() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task = new Task(1, "Test task", false, dueDate, Priority.HIGH, "Work");
        assertEquals(1, task.getId());
        assertEquals("Test task", task.getText());
        assertFalse(task.isCompleted());
        assertEquals(dueDate, task.getDue());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals("Work", task.getCategory());
    }

    @Test
    public void testTaskConstructorWithNullValue() {
        Task task = new Task(1, "Test task", false, null, null, null);
        assertEquals(1, task.getId());
        assertEquals("Test task", task.getText());
        assertFalse(task.isCompleted());
        assertEquals(null, task.getDue());
        assertEquals(Priority.LOW, task.getPriority());
        assertEquals(null, task.getCategory());
    }


    @Test
    public void testSetIdWithNegativeValue() {
        Task task = new Task("Test task");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> task.setId(-1));
    }

    @Test()
    public void testSetTextWithNull() {
        Task task = new Task("Test task");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->  task.setText(null));
    }

    @Test
    public void testSetTextWithWhitespace() {
        Task task = new Task("Initial text");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->  task.setText("   "));
    }

    @Test
    public void testSetDueWithNull() {
        Task task = new Task("Test task");
        task.setDue(null); 
        assertNull(task.getDue());
    }

    @Test
    public void testSetPriorityWithNull() {
        Task task = new Task("Test task");
        task.setPriority(null);
        assertEquals(Priority.LOW, task.getPriority());
    }

    @Test
    public void testSetCategoryWithEmptyString() {
        Task task = new Task("Test task");
        task.setCategory("");
        assertNull(task.getCategory());
        task.setCategory("   ");
        assertNull(task.getCategory());
    }


    @Test
    public void testMarkCompleted() {
        Task task = new Task("Test task");
        assertFalse(task.isCompleted());
        task.markCompleted(true);
        assertTrue(task.isCompleted());
    }


    @Test
    public void testReadFromCsv() {
        String csvLine = "1,Test task,true,2024/29/03,HIGH,Work";
        Task task = Task.readFromCsv(csvLine);
        assertEquals(1, task.getId());
        assertEquals("Test task", task.getText());
        assertTrue(task.isCompleted());
        assertEquals(LocalDate.of(2024, 3, 29), task.getDue());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals("Work", task.getCategory());
    }

    @Test
    public void testReadFromCsvWithInvalidDate() {
        String csvLine = "1,Test task,true,2024/30/29,HIGH,Work";
        Task task = Task.readFromCsv(csvLine);
        assertNull(task.getDue()); 
    }

    @Test
    public void testReadFromCsvWithInvalidPriority() {
        String csvLine = "1,Test task,true,2024/03/29,UNKNOWN,Work";
        Task task = Task.readFromCsv(csvLine);
        assertEquals(Priority.LOW, task.getPriority()); 
    }

    @Test
    public void testReadFromCsvWithInvalidFormat() {
        String csvLine = "1,Test task,true";
        ArrayIndexOutOfBoundsException e=  assertThrows(ArrayIndexOutOfBoundsException.class,() -> Task.readFromCsv(csvLine));
    }

    @Test
    void testCompletedStatus() {
        String lineWithCompletedTrue = "1,Test Task,true,,LOW,";
        Task taskWithCompleted = Task.readFromCsv(lineWithCompletedTrue);
        assertTrue(taskWithCompleted.isCompleted());

        String lineWithCompletedFalse = "1,Test Task,false,,LOW,";
        Task taskWithCompletedFalse = Task.readFromCsv(lineWithCompletedFalse);
        assertFalse(taskWithCompletedFalse.isCompleted());

        String lineWithCompletedEmpty = "1,Test Task,,,,";
        Task taskWithCompletedEmpty = Task.readFromCsv(lineWithCompletedEmpty);
        assertFalse(taskWithCompletedEmpty.isCompleted());
    }
    

    @Test
    public void testToCSVLine() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task = new Task(1, "Test, task", false, dueDate, Priority.HIGH, "Work");
        String expectedCsvLine = "1,Test� task,false,2024/29/03,HIGH,Work";
        assertEquals(expectedCsvLine, task.toCSVLine());
    }

    @Test
    public void testToCsvLineWithNullDue() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task = new Task(1, "information", true, dueDate, null, "Preme");
        String csvLine = task.toCSVLine();
        String expected= "1,information,true,2024/29/03,LOW,Preme";
        assertEquals(expected,csvLine);
    }

    @Test
    void testCSVLineFormatting() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task = new Task(1, "Test Task", false, null, Priority.MEDIUM, "Work");
        String expectedCSVLine = "1,Test Task,false,,MEDIUM,Work";
        assertEquals(expectedCSVLine, task.toCSVLine());
    
        Task taskWithNullCategory = new Task(1, "Test Task", false, dueDate, Priority.MEDIUM, null);
        String expectedCSVLineWithNullCategory = "1,Test Task,false,2024/29/03,MEDIUM,";
        assertEquals(expectedCSVLineWithNullCategory, taskWithNullCategory.toCSVLine());
    }

    @Test
    void testPrioritySetting() {
        String lineWithPriorityHigh = "1,Test Task,false,,HIGH,";
        Task taskWithHighPriority = Task.readFromCsv(lineWithPriorityHigh);
        assertEquals(Priority.HIGH, taskWithHighPriority.getPriority());

        String lineWithPriorityEmpty = "1,Test Task,false,,,";
        Task taskWithEmptyPriority = Task.readFromCsv(lineWithPriorityEmpty);
        assertEquals(Priority.LOW, taskWithEmptyPriority.getPriority());
    }



    @Test
    public void testToStringMethod() {
        Task task = new Task(1, "information", true, null, null, null);
        String expected = "Task{id=1, text='information', completed=true, due='null', priority='LOW', category='null'}";
        String actual = task.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testToStringWithNormalValue() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task = new Task(1, "information", true, dueDate, Priority.LOW, "Preme");
        String expected = "Task{id=1, text='information', completed=true, due='2024-03-29', priority='LOW', category='Preme'}";
        String actual = task.toString();
        assertEquals(expected, actual);
    }



    @Test
    public void testCompareTo() {
        LocalDate dueDate1 = LocalDate.of(2024, 3, 29);
        LocalDate dueDate2 = LocalDate.of(2024, 3, 30);
        Task task1 = new Task(1, "Task 1", false, dueDate1, Priority.MEDIUM, "Work");
        Task task2 = new Task(2, "Task 2", false, dueDate2, Priority.HIGH, "Home");

        assertTrue(task1.compareTo(task2) < 0);
    }

    @Test
    public void testCompareToWithSameDueDifferentPriority() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task1 = new Task(1, "Task 1", false, dueDate, Priority.LOW, "Work");
        Task task2 = new Task(2, "Task 2", false, dueDate, Priority.HIGH, "Home");

        assertTrue(task1.compareTo(task2) < 0);
    }

    @Test
    public void testCompareToWithNullDuePriority() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task1 = new Task(1, "Task 1", false, dueDate, Priority.LOW, "Work");
        Task task2 = new Task(2, "Task 2", false, null, Priority.HIGH, "Home");
        assertTrue(task1.compareTo(task2) < 0);
    }

    @Test
    public void testCompareToWithNullThisDuePriority() {
        LocalDate dueDate = LocalDate.of(2024, 3, 29);
        Task task1 = new Task(1, "Task 1", false, null, Priority.LOW, "Work");
        Task task2 = new Task(2, "Task 2", false, dueDate, Priority.HIGH, "Home");
        assertTrue(task1.compareTo(task2) > 0);
    }

    @Test
    public void testCompareToBothDueNull() {
        Task task1 = new Task(1, "Task 1", false, null, Priority.MEDIUM, "Work");
        Task task2 = new Task(2, "Task 2", false, null, Priority.LOW, "Home");
        assertTrue(task1.compareTo(task2) > 0);
    }

}