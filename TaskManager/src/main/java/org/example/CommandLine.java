
package org.example;


import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Class for executing use command
 */
public class CommandLine {

    // Declare the HashMap as a class variable
    private static final Map<String, String> commandMap = new HashMap<>();

    // Initialize the HashMap with commands and descriptions
    static {
        commandMap.put("help", "Provide a brief description of all of the supported commands. Param <command>: Provide a full description of the command and additional info required for that command. Please ensure --help is the only command, otherwise commands after --help will not be executed");

        commandMap.put("csv-file <path/to/file>", "The CSV file containing the Tasks. This option is required unless printing help.");

        commandMap.put("add-Task", "Add a new Task. If this option is provided, then --Task-text must also be provided.");

        commandMap.put("Task-text <description of Task>", "A description of the Task.");

        commandMap.put("completed", "(Optional) Sets the completed status of a new Task to true.");

        commandMap.put("due ", "(Optional) Sets the due date of a new Task. You may choose how the date should be formatted. Param <due date> to set due date in format YYYY-MM-DD");

        commandMap.put("--priority <1, 2, or 3>", "(Optional) Sets the priority of a new Task. The values represent LOW, MEDIUM, or HIGH.");

        commandMap.put("category <a category name>", "(Optional) Sets the category of a new Task. The value can be any String. Categories do not need to be pre-defined.");

        commandMap.put("complete-Task <id>", "Mark the Task with the provided ID as complete.");

        commandMap.put("display", "Display Tasks. If none of the following optional arguments are provided, displays all Tasks.");

        commandMap.put("show-incomplete", "(Optional) If --display is provided, only incomplete Tasks should be displayed.");

        commandMap.put("show-category", "(Optional) If --display is provided, and command is followed by <category>, only Tasks with the given category should be displayed.");

        commandMap.put("sort-by-date", "(Optional) If --display is provided, sort the list of Tasks by date order (ascending). Cannot be combined with --sort-bypriority.");

        commandMap.put("sort-by-priority", "(Optional) If --display is provided, sort the list of Tasks by priority (ascending). Cannot be combined with --sort-by-date.");
    }

    /**
     * Helper method for checking if --help command exist.
     * @param commands
     */
    public static boolean checkHelpCommandExist(String[] commands) {

        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            String[] part = command.trim().split(" ");
            if (part[0].equals("help")){
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method for checking execute --help Command if exist.
     * @param commands
     */
    public static void runHelpCommand(String[] commands) {
        if (commands.length == 1 && commands[0].startsWith("help")) {
            String[] parts = commands[0].split(" ", 2);

            if (parts.length > 1) {
                //searchCommand is the command following "--help "
                String searchCommand = parts[1];

                //when command in --help <command> is valid.
                if (commandMap.containsKey(searchCommand)){
                    System.out.println(commandMap.get(searchCommand));
                } else {
                    throw new IllegalArgumentException("Exception: <command> in --help <command> not valid");
                }
            } else {
                //when the command is "--help".
                for (Map.Entry<String, String> entry : commandMap.entrySet()) {
                    System.out.println("--" + entry.getKey() + ":    " + entry.getValue());
                }
            }
        } else {
            throw new IllegalArgumentException("Exception: --help Command must be the first command and only on its own.");
        }
    }

    /**
     * Helper method for Removing first element of array to get rid of "" before commands.
     * @param commands
     * @return
     */
    public static String[] removeFirstEmptyString(String[] commands) {
        // Check if the array is empty or has only one element
        if (commands == null || commands.length <= 1) {
            return new String[0]; // Return an empty array
        }

        // Create new array with size one less than the original array
        String[] newArray = new String[commands.length - 1];

        // Copy elements from the original array to the new array, skipping the first element
        System.arraycopy(commands, 1, newArray, 0, newArray.length);

        return newArray;
    }

    /**
     * Helper method to check if --csv-file command exist, if so execute to set path
     * @param commands
     * @return IllegalArgumentException if path is missing or multiple paths were passed.
     */
    public static String checkCSVFileCommand(String[] commands) {
        // Path Cannot pass in by reference, hence passed by value.
        String path = null;
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            String[] part = command.trim().split(" ");
            if(part[0].equals("csv-file")){
                if (part.length == 1) {
                    throw new IllegalArgumentException("Exception: Command '--csv-file <path/to/file>' path is missing.");
                }
                if(part.length > 2) {
                    throw new IllegalArgumentException("Exception: Command '--csv-file <path/to/file>' multiple paths were passed.");
                }
                path = part[1];
            }
        }
        if(path == null) {
            throw new IllegalArgumentException("Exception: Command '--csv-file <path/to/file>' not found.");
        }
        return path;
    }

    private static int addTaskIndex = -1;

    /**
     * Helper method to find whether --add-Task command exist before current command
     * Back traverse
     *
     * @param start      the earliest command
     * @param end        the latest command
     * @param commandarr command array for traverse
     * @return true if --add-Task is found and false if not
     */
    public static boolean findAddTask(int start, int end, String[] commandarr) {
        // Iterate through the command array from the current to the front
        for (int i = end - 1; i >= start; i--) {
            if (commandarr[i].trim().equals("add-Task")) {
                addTaskIndex = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to find whether duplicate command exists (this may cause
     * confusion)
     * Back traverse
     *
     * @param start      the earliest command
     * @param end        the latest command
     * @param commandarr command array for traverse
     * @param command    current command in regex
     * @return true if command duplicates and false if not
     */
    public static boolean findDuplicateTask(int start, int end, String[] commandarr, String command) {
        for (int i = end - 1; i > start; i--) {
            if (commandarr[i].trim().matches(command)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Main function
     * @param args user input of commands in string format
     */
    public static void main(String[] args) {

        TaskManager taskManager;
        Scanner scanner = new Scanner(System.in);
        //initialize path with null
        String path = null;


        while (true) {
            System.out.print("Please enter a command following \"--\" ");
            String input = scanner.nextLine();
            if(input.startsWith("--exist")){
                System.out.println("Bye bye");
                return;
            }
            String[] commands = input.split("--");


            //remove null string at index 0.
            commands = removeFirstEmptyString(commands);


            //if help command exist, execute help command based on whether parameter command is inputed.
            try {
                if (checkHelpCommandExist(commands)) {
                    runHelpCommand(commands);
                    continue;
                }

                //Check if input command includes exactly one --csv-file <path/to/file>.
                if (path==null){
                    path = checkCSVFileCommand(commands);
                    System.out.println(); // Empty line
                    System.out.println("Command '--csv-file <path/to/file>' successfully executed. Path set to" + path);
                    System.out.println(); // Empty line
                }
                taskManager = new TaskManager(path);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }


            //Start loop for checking individual commands.
            try {
                boolean addTaskFound;

                for(int i = 0; i < commands.length;i++){
                    String[] parts = commands[i].trim().split(" ",2);
                    String command = parts[0];
                    // System.out.println(Arrays.toString(part));
                    switch (command) {
                        case "display":
                            int count = 0;
                            boolean showIncomplete = false;
                            boolean sortByDate = false;
                            boolean sortByPriority = false;
                            String category = null;

                            for (int j=i+1; j<commands.length; j++) {
                                commands[j] = commands[j].trim();
                                if (commands[j].equals("show-incomplete")) {
                                    showIncomplete = true;
                                }


                                else if(commands[j].startsWith("show-category")) {
                                    String[] categoryArr = commands[j].trim().split(" ");
                                    if(categoryArr.length != 2) {
                                        throw new IllegalArgumentException("Category is missing.");
                                    }
                                    category = categoryArr[1];
                                }

                                else if(commands[j].equals("sort-by-date")) {
                                    sortByDate = true;
                                }

                                else if(commands[j].equals("sort-by-priority")) {
                                    sortByPriority = true;
                                }
                                count++;
                            }


                            taskManager.displayTasks(showIncomplete, category, sortByDate, sortByPriority);
                            i += count;
                            break;
                        case "csv-file":
                            break;

                        case "add-Task":
                            // Find --add-Task has argument or not
                            if (parts.length > 1) {
                                throw new IllegalArgumentException("Too many arguments for --add-Task command");
                            }

                            // --add-Task should have --Task-text in the following commands
                            if (i + 1 < commands.length) {
                                for (int j = i + 1; j < commands.length; j++) {
                                    // Iterate through the following command to find --Task-text
                                    // Should find --Task-text before the next --add-Task
                                    if (commands[j].equals("add-Task")) {
                                        throw new IllegalArgumentException("Missing Task description for --add-Task option");
                                    }


                                    // Find trailing --Task--text and should have argument: description
                                    if (commands[j].trim().matches("^Task-text\\s*.*")) {
                                        String[] text = commands[j].split("Task-text\\s+");
                                        if (text.length >= 2) {
                                            String taskDescription = text[1];
                                            taskManager.addTask(taskDescription);
                                            break;
                                        } else {
                                            throw new IllegalArgumentException("Missing task description");
                                        }

                                    }
                                }
                            } else {
                                throw new IllegalArgumentException("Missing Task description for --add-Task option");
                            }
                            break;

                        case "Task-text":
                            // --Task-text should have --add-Task before it
                            addTaskFound = CommandLine.findAddTask(0, i, commands);
                            if (!addTaskFound) {
                                throw new IllegalArgumentException("Missing --add-Task before --Task-text");
                            }

                            // --Task-text should not duplicate until the next --add-Task command
                            if (CommandLine.findDuplicateTask(addTaskIndex, i, commands, "^Task-text\\s*.*")) {
                                throw new IllegalArgumentException("Duplicate --Task-text command");
                            }
                            break;
                        case "completed":
                            // --completed should have --add-Task before it
                            addTaskFound = CommandLine.findAddTask(0, i, commands);
                            if (!addTaskFound) {
                                throw new IllegalArgumentException("Missing --add-Task before --completed");
                            }

                            // --completed should not duplicate until the next --add-Task command
                            if (CommandLine.findDuplicateTask(addTaskIndex, i, commands, "^completed\\s*")) {
                                throw new IllegalArgumentException("Duplicate --completed command");
                            }


                            // --completed should not any argument
                            if (parts.length > 1) {
                                throw new IllegalArgumentException("Too many arguments for --completed command");
                            }
                            taskManager.findTaskById(taskManager.getCurrentMaxID()).markCompleted(true);
                            break;
                        case "due":
                            // --due should have --add-Task before it
                            addTaskFound = CommandLine.findAddTask(0, i, commands);
                            if (!addTaskFound) {
                                throw new IllegalArgumentException("Missing --add-Task before --due");
                            }


                            // --due should not duplicate until the next --add-Task command
                            if (CommandLine.findDuplicateTask(addTaskIndex, i, commands, "^due\\s*.*")) {
                                throw new IllegalArgumentException("Duplicate --due command");
                            }

                            // --due should have 1 argument
                            if (parts.length <= 1) {
                                throw new IllegalArgumentException("Missing date yyyy-MM-dd for --due option");
                            }

                            // --due argument should have the correct format yyyy--mm-dd
                            try {
                                // boolean taskDate = parts[1].matches("due\\s+(?=\\d{4}-\\d{2}-\\d{2}).*");
                                LocalDate dueDate = LocalDate.parse(parts[1]);
                                taskManager.findTaskById(taskManager.getCurrentMaxID()).setDue(dueDate);
                            } catch (DateTimeParseException e) {
                                throw new IllegalArgumentException(
                                        "Invalid input format or missing date. Expected format: due yyyy-MM-dd");
                            }

                            break;
                        case "priority":
                            // --priority should have --add-Task before it
                            addTaskFound = CommandLine.findAddTask(0, i, commands);
                            if (!addTaskFound) {
                                throw new IllegalArgumentException("Missing --add-Task before --priority");
                            }


                            // --priority should not duplicate until the next --add-Task command
                            if (CommandLine.findDuplicateTask(addTaskIndex, i, commands, "^priority\\s*.*")) {
                                throw new IllegalArgumentException("Duplicate --priority command");
                            }

                            // --priority should have 1 argument with integer 1, 2 or 3
                            if (parts.length > 1) {
                                try {
                                    int priorityLevel = Integer.parseInt(parts[1]);
                                    if (priorityLevel != 1 && priorityLevel != 2 && priorityLevel != 3) {
                                        throw new IllegalArgumentException("Invalid priority level for --priority option");
                                    }
                                    taskManager.findTaskById(taskManager.getCurrentMaxID()).setPriority(Priority.getPriority(priorityLevel));
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException("Invalid priority level for --priority option");
                                }


                            } else {
                                throw new IllegalArgumentException("Missing priority level for --priority option");
                            }
                            break;

                        case "category":
                            // --category should have --add-Task before it
                            addTaskFound = CommandLine.findAddTask(0, i, commands);
                            if (!addTaskFound) {
                                throw new IllegalArgumentException("Missing --add-Task before --category");
                            }

                            // --category should not duplicate until the next --add-Task command
                            if (CommandLine.findDuplicateTask(addTaskIndex, i, commands, "^category\\s*.*")) {
                                throw new IllegalArgumentException("Duplicate --category command");

                            }

                            // --category should have at least 1 argument to specify
                            if (parts.length > 1) {
                                taskManager.findTaskById(taskManager.getCurrentMaxID()).setCategory(parts[1]);
                            } else {
                                throw new IllegalArgumentException("Missing category name for --category option");
                            }
                            break;
                        case "complete-Task":
                            // --complete-Task should have 1 argument: integer ID of a task
                            if (parts.length > 1) {
                                try {
                                    int taskId = Integer.parseInt(parts[1]);
                                    taskManager.findTaskById(taskId).markCompleted(true);
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException("Invalid task ID for --complete-Task");
                                }
                            } else {
                                throw new IllegalArgumentException("Missing task ID for --complete-Task");
                            }

                            break;
                        default:
                            throw new IllegalArgumentException("Exception: command not valid");
                    }

                }

                taskManager.updateCSV();
            }
            catch (Exception e) {
                System.err.println(e.toString());

            }
        }
        // scanner.close();
    }
}


