package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.jdesktop.swingx.JXDatePicker;


public class TaskManagerGUI extends JFrame {
    private JTable taskTable;
    private JTextField taskTextField;
    private JButton addButton;
    private JButton completeButton;
    private JButton chooseFileButton;
    private JButton priorityButton;
    private JButton filterButton;
    private JButton sortButton;
    private JPopupMenu popupMenuPriority;
    private JPopupMenu popupMenuCompleted;
    private JPopupMenu popupMenuFilter;
    private JPopupMenu popupMenuSort;
    private JTextField filePathTextField;
    private JButton chooseDueDateButton;
    private JLabel categoryLabel;
    private JTextField categoryTextField;
    private TaskManager taskManager;
    private JFileChooser fileChooser;
    private JXDatePicker dueDatePicker;
    private String selectedfilter;
    private String filterCategoryName;
    private String selectedSort;
    private String selectedPriority;
    private String selectedCompleted;

    public TaskManagerGUI(TaskManager taskManager) {
        this.taskManager = taskManager;

        //-------------Set up main panel-------------
        setTitle("Task Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //-------------Table for data---------------
        taskTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);

        //-------------Panel for selecting file path-------
        JPanel filePathPanel = new JPanel(new FlowLayout());

        JLabel filePathLabel = new JLabel("File Path");
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooseFileButton = new JButton("Choose File");
        filePathTextField = new JTextField("Path for csv file");
        filePathTextField.setPreferredSize(new Dimension(350,30));
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFilePath();
                filePathTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                updateTable();
            }
        });
        filePathPanel.add(filePathLabel);
        filePathPanel.add(filePathTextField);
        filePathPanel.add(chooseFileButton);
        add(filePathPanel,BorderLayout.NORTH);


        //-------------------Filter & Sort Penal-----------------------
        JPanel filterPanel = new JPanel(new FlowLayout());

        //Filter function implementation
        JLabel filterLabel = new JLabel("Filter by");
        filterPanel.add(filterLabel);
        filterButton = new JButton("Default");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenuFilter.show(filterButton, 0, filterButton.getHeight());
            }
        });
        filterPanel.add(filterButton);

        // Create the popup menu
        popupMenuFilter = new JPopupMenu();
        JMenuItem menuDefaultFilter = new JMenuItem("Default");
        JMenuItem menuIncomplete = new JMenuItem("Incomplete");
        JMenuItem menuCategory = new JMenuItem("Category");


        ActionListener menuActionListenerFilter = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedfilter = ((JMenuItem) e.getSource()).getText();
                filterButton.setText(selectedfilter);
                if (selectedfilter.equals("Incomplete") || selectedfilter.equals("Default")){
                    updateTable();
                }else if (selectedfilter.equals("Category")) {
                    // Show input dialog for custom category
                    String customCategory = JOptionPane.showInputDialog(null, "Enter custom category:");
                    if (customCategory != null) {
                        filterCategoryName = customCategory;
                        updateTable();
                    }
                }
            }
        };

        menuDefaultFilter.addActionListener(menuActionListenerFilter);
        menuIncomplete.addActionListener(menuActionListenerFilter);
        menuCategory.addActionListener(menuActionListenerFilter);

        popupMenuFilter.add(menuDefaultFilter);
        popupMenuFilter.add(menuIncomplete);
        popupMenuFilter.add(menuCategory);

        //Sort function implementation
        JLabel sortLabel = new JLabel("Sort by");
        filterPanel.add(sortLabel);

        sortButton = new JButton("Default");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenuSort.show(sortButton, 0, sortButton.getHeight());
                updateTable();
            }
        });
        filterPanel.add(sortButton);


        // Create the popup menu
        popupMenuSort = new JPopupMenu();
        JMenuItem menuDefaultSort = new JMenuItem("Default");
        JMenuItem menuDue = new JMenuItem("Due");
        JMenuItem menuPriority = new JMenuItem("Priority");


        ActionListener menuActionListenerSort = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedSort = ((JMenuItem) e.getSource()).getText();
                sortButton.setText(selectedSort);
                updateTable();
            }
        };

        menuDefaultSort.addActionListener(menuActionListenerSort);
        menuDue.addActionListener(menuActionListenerSort);
        menuPriority.addActionListener(menuActionListenerSort);

        popupMenuSort.add(menuDefaultSort);
        popupMenuSort.add(menuDue);
        popupMenuSort.add(menuPriority);

        //Need two south panel,the other one is input panel below
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(filterPanel);


        //------------------Input panel----------------
        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel addLabel = new JLabel("Add New");
        inputPanel.add(addLabel);

        //For typing task description
        taskTextField = new JTextField();
        taskTextField.setPreferredSize(new Dimension(150,30));
        inputPanel.add(taskTextField);

        //For choosing due date
        dueDatePicker = new JXDatePicker();
        chooseDueDateButton = new JButton("Due Date");
        chooseDueDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDueDate();
                if (dueDatePicker.getDate() != null){
                    chooseDueDateButton.setText(dueDatePicker.getDate().toString());
                }
            }
        });
        inputPanel.add(chooseDueDateButton);

        //For typing in category name
        categoryLabel = new JLabel("Category");
        inputPanel.add(categoryLabel,BorderLayout.SOUTH);
        categoryTextField = new JTextField();
        categoryTextField.setPreferredSize(new Dimension(80,30));
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryTextField);

        //Selection of priority level with popup menu
        priorityButton = new JButton("Select Priority");
        priorityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenuPriority.show(priorityButton, 0, priorityButton.getHeight());
            }
        });
        inputPanel.add(priorityButton);

        // Create the popup menu
        popupMenuPriority = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("1");
        JMenuItem menuItem2 = new JMenuItem("2");
        JMenuItem menuItem3 = new JMenuItem("3");


        ActionListener menuActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPriority = ((JMenuItem) e.getSource()).getText();
                priorityButton.setText("Priority: " + selectedPriority);
            }
        };

        menuItem1.addActionListener(menuActionListener);
        menuItem2.addActionListener(menuActionListener);
        menuItem3.addActionListener(menuActionListener);

        popupMenuPriority.add(menuItem1);
        popupMenuPriority.add(menuItem2);
        popupMenuPriority.add(menuItem3);

        completeButton = new JButton("Completed");
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenuCompleted.show(completeButton, 0, completeButton.getHeight());
            }
        });
        inputPanel.add(completeButton);

        // Create the popup menu
        popupMenuCompleted = new JPopupMenu();
        JMenuItem menuYes = new JMenuItem("Yes");
        JMenuItem menuNo = new JMenuItem("No");


        ActionListener menuActionListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCompleted = ((JMenuItem) e.getSource()).getText();
                completeButton.setText("Completed: " + selectedCompleted);
            }
        };

        menuYes.addActionListener(menuActionListener2);
        menuNo.addActionListener(menuActionListener2);

        popupMenuCompleted.add(menuYes);
        popupMenuCompleted.add(menuNo);


        //Add task button
        //Only when finishing entering all the information
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskDescription = taskTextField.getText().trim();
                if (!taskDescription.isEmpty()) {
                    taskManager.addTask(taskDescription);
                    taskTextField.setText("");
                }
                if (dueDatePicker.getDate() != null){
                    taskManager.findTaskById(taskManager.getCurrentMaxID()).setDue(dueDatePicker.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    taskManager.updateCSV();
                    dueDatePicker.setDate(null);
                }
                if (!categoryTextField.getText().isEmpty()){
                    taskManager.findTaskById(taskManager.getCurrentMaxID()).setCategory(categoryTextField.getText());
                    taskManager.updateCSV();
                    categoryTextField.setText("");
                }
                if(selectedPriority != null){
                    taskManager.findTaskById(taskManager.getCurrentMaxID()).setPriority((Priority.getPriority(Integer.parseInt(selectedPriority))));
                    taskManager.updateCSV();
                    selectedPriority = "";
                }
                if(selectedCompleted != null){
                    taskManager.findTaskById(taskManager.getCurrentMaxID()).markCompleted(selectedCompleted.equals("Yes"));
                    taskManager.updateCSV();
                    selectedCompleted = "";
                }
                updateTable();
                chooseDueDateButton.setText("Due Date");
                priorityButton.setText("Select Priority");
                completeButton.setText("Completed");

            }
        });
        inputPanel.add(addButton);
        southPanel.add(inputPanel);
        add(southPanel,BorderLayout.SOUTH);
    }

    private void chooseFilePath() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Set CSV file path in TaskManager
            taskManager.setCsvFilePath(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseDueDate() {
        int result = JOptionPane.showConfirmDialog(this, dueDatePicker, "Choose Due Date", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            Date selectedDate = dueDatePicker.getDate();
//        }
    }

    private void updateTable() {
        // Get all tasks from TaskManager
        taskManager.loadTasksFromCSV();
        List<Task> tasks = taskManager.getTasks();

        // Apply filtering if selected
        if (selectedfilter != null && !selectedfilter.equals("Default")) {
            switch (selectedfilter) {
                case "Incomplete":
                    tasks = taskManager.getTasksByCompletion(false);
                    break;
                case "Category":
                    tasks = taskManager.getTasksByCategory(filterCategoryName);
                    break;
                default:
                    break;
            }
        }

        // Apply sorting if selected
        if (selectedSort != null && !selectedSort.equals("Default")) {
            switch (selectedSort) {
                case "Due":
                    tasks.sort(Comparator.comparing(Task::getDue, Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "Priority":
                    tasks.sort(Comparator.comparing(Task::getPriority));
                    break;
                default:
                    break;
            }
        }

        // Create a 2D array to store task data
        Object[][] data = new Object[tasks.size()][6];
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            data[i][0] = task.getId();
            data[i][1] = task.getText();
            data[i][2] = task.isCompleted() ? "Yes" : "No";
            data[i][3] = task.getDue();
            data[i][4] = task.getPriority();
            data[i][5] = task.getCategory();
        }
        String[] columns = {"ID", "Description", "Complete", "Due", "Priority", "Category"};
        // Create a table model with data and column names
        DefaultTableModel model = new DefaultTableModel(data, columns);
        taskTable.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TaskManager taskManager = new TaskManager("/Users/amy/Downloads/sampledata.csv"); // Provide default file path
                TaskManagerGUI gui = new TaskManagerGUI(taskManager);
                gui.setVisible(true);
            }
        });
    }
}
