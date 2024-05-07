package task.manager.javenger;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for CommandLine class.
 *
 * @author Jingjing Ji
 * @author Menglin Lin
 */
public class CommandLineTest {

    String[] commandLine1;
    String[] commandLine2;

    //Completed by Jingjing Ji
    @Test
    public void testremoveFirstEmptyString() {
        String[] commandLine1 = {"","--csv-file path", "--display"};
        String[] commandLine2 = {"--csv-file path", "--display"};
        String[] resultArray = CommandLine.removeFirstEmptyString(commandLine1);
        assertArrayEquals(commandLine2, resultArray);
    }

    @Test
    public void testRemoveFirstEmptyStringWithEmptyArray() {
        String[] originalArray = {};
        String[] expectedArray = {};
        String[] resultArray = CommandLine.removeFirstEmptyString(originalArray);
        assertArrayEquals(expectedArray, resultArray);
    }

    @Test
    public void testRemoveFirstEmptyStringWithNullArray() {
        String[] originalArray = null;
        String[] expectedArray = {};
        String[] resultArray = CommandLine.removeFirstEmptyString(originalArray);
        assertArrayEquals(expectedArray, resultArray);
    }

    // Tests for checkHelpCommandExist method.
    @Test
    public void testCheckHelpCommandExistWithHelpCommand() {
        String[] commands = {"help", "display", "command2"};
        boolean result = CommandLine.checkHelpCommandExist(commands);
        assertTrue(result);
    }

    @Test
    public void testCheckHelpCommandExistWithoutHelpCommand() {
        String[] commands = {"command1", "command2"};
        boolean result = CommandLine.checkHelpCommandExist(commands);
        assertFalse(result);
    }

    @Test
    public void testCheckHelpCommandExistWithEmptyArray() {
        String[] commands = {};
        boolean result = CommandLine.checkHelpCommandExist(commands);
        assertFalse(result);
    }

    // @Test
    // public void testCheckHelpCommandExistWithNullArray() {
    //     String[] commands = null;
    //     boolean result = CommandLine.checkHelpCommandExist(commands);
    //     assertFalse(result);
    // }

    // Test if only "--help" command is inputed
    @Test
    public void testRunHelpCommandOnlyHelp() {

        String[] commands = {"help"};
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        CommandLine.runHelpCommand(commands);
        assertTrue(outContent.toString().contains("Provide a brief description"));
    }

    // Test if one command that is not help is inputed
    @Test
    public void testRunHelpCommandOnlyNotHelp() {

        String[] commands = {"display"};
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.runHelpCommand(commands);
        });
        assertEquals("Exception: --help Command must be the first command and only on its own." , exception.getMessage());
    }

    // Test if "--help <command>" is inputed and command is valid
    @Test
    public void testRunHelpCommandHelpValidCommand() {
        String[] commands = {"help display"};
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        CommandLine.runHelpCommand(commands);
        assertEquals("Display Tasks. If none of the following optional arguments are provided, displays all Tasks.\n", outContent.toString());
    }


    // Test if "--help <command>" is inputed but command is invalid -> IllegalArgumentException
    @Test
    public void testRunHelpCommandHelpInvalidCommand() {
        String[] commands = {"help invalidCommand"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.runHelpCommand(commands);
        });
        assertEquals("Exception: <command> in --help <command> not valid", exception.getMessage());
    }
    

    // Test case when "--help" is not the only command inputed -> IllegalArgumentException
    @Test
    public void testRunHelpCommandNotOnlyHelp() {
        String[] commands = {"help display", "display"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.runHelpCommand(commands);
        });
        assertEquals("Exception: --help Command must be the first command and only on its own.", exception.getMessage());
    }


    // Test case when "--csv-file" command with valid path is provided
    @Test
    public void testCheckCSVFileCommandWithValidPath() {

        String[] commands = {"csv-file path"};
        String result = CommandLine.checkCSVFileCommand(commands);
        assertEquals("path", result);
    }

    // Test when "--csv-file" command with missing path is inputed -> IllegalArgumentException
    @Test
    public void testCheckCSVFileCommandWithMissingPath() {
        String[] commands = {"csv-file"};
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.checkCSVFileCommand(commands);
        });
        assertEquals("Exception: Command '--csv-file <path/to/file>' path is missing.", exception.getMessage());
    }

    // Test case when "--csv-file" command with multiple paths is inputed -> IllegalArgumentException
    @Test
    public void testCheckCSVFileCommandWithMultiplePaths() {
        // Arrange
        String[] commands = {"csv-file path1 path2"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.checkCSVFileCommand(commands);
        });
        assertEquals("Exception: Command '--csv-file <path/to/file>' multiple paths were passed.", exception.getMessage());
    }

    // Test case when "--csv-file" command is not inputed -> IllegalArgumentException
    @Test
    public void testCheckCSVFileCommandNotProvided() {
        String[] commands = {"display", "task"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommandLine.checkCSVFileCommand(commands);
        });
        assertEquals("Exception: Command '--csv-file <path/to/file>' not found.", exception.getMessage());
    }

    //Completed by Menglin Lin
    @BeforeEach
    void SetUp() {
        commandLine1 = new String[] { "add-Task", "completed" };
        commandLine2 = new String[] { "completed", "completed" };
    }

    @Test
    void findAddTask() {
        assertTrue(CommandLine.findAddTask(0, 1, commandLine1));
        assertFalse(CommandLine.findAddTask(0, 1, commandLine2));
    }

    @Test
    void findDuplicateTask() {
        assertTrue(CommandLine.findDuplicateTask(-1, 1, commandLine2, "completed"));
        assertFalse(CommandLine.findDuplicateTask(-1, 1, commandLine1, "completed"));
    }


}