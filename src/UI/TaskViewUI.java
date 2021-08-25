package UI;

import Entities.*;
import IO.FileIOManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.*;

public class TaskViewUI
{
    private final String[] PATHS = { "C:\\Users\\gd689057\\IdeaProjects\\B_Proj\\Data\\Tasks.csv", "C:\\Users\\gd689057\\IdeaProjects\\B_Proj\\Data\\Employees.csv" };

    private JButton cmdUpdate;
    private JButton cmdCreateNew;
    private JTable tblTasks;
    private JScrollPane pnlTableContainer;
    private JPanel pnlMain;
    private JPanel pnlContainer;

    private ArrayList<EmployeeInfo> employees = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Task> newTasks = new ArrayList<>();

    public TaskViewUI()
    {
        initialiseEmployees();
        loadTaskInformation();

        TaskDataModel model = new TaskDataModel();
        model.addRange(tasks);
        tblTasks = new JTable(model);
        for(int i = 0; i < model.columns.length; i++)
        {
            tblTasks.setDefaultRenderer(model.getColumnClass(i), new TaskTableRenderer());
        }
        RowSorter<TaskDataModel> sorter = new TableRowSorter<>(model);
        tblTasks.setRowSorter(sorter);
        pnlTableContainer.setViewportView(tblTasks);
        TaskViewUI _this = this;

        // add listeners for add and update buttons
        cmdCreateNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                TaskMakerUI taskMaker = new TaskMakerUI(_this, -1);
            }
        });

        cmdUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String acc = "";
                for(Task t : tasks)
                {
                    acc += t.writeCSV();
                }
                for(Task nt : newTasks)
                {
                    acc += nt.writeCSV();
                }

                System.out.println("Attempting to save task data...");
                FileIOManager instance = FileIOManager.getInstance();
                instance.setPath(PATHS[0]);

                try
                {
                    instance.writeToFile(acc.toCharArray(), false);
                    System.out.println("Task data successfully saved.");
                }
                catch(IOException ex)
                {
                    System.out.println("Failed to save task data.\nReason: " + ex.getMessage());
                }
                reloadModel();
            }
        });

        tblTasks.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JTable tbl = (JTable)e.getSource();
                Point p = e.getPoint();
                int rowP = tbl.rowAtPoint(p);
                int colP = tbl.columnAtPoint(p);
                if(e.getClickCount() == 2 && rowP != -1)
                {
                    if(colP != 7)
                    {
                        TaskMakerUI taskMaker = new TaskMakerUI(_this, rowP);
                    }
                    else
                    {
                        TaskDataModel tdm = (TaskDataModel)tbl.getModel();
                        EmployeeInfo emp = (EmployeeInfo)tdm.getValueAt(rowP, colP);
                        EmployeeInfoView eView = new EmployeeInfoView(emp);
                    }

                }
            }
        });
        JFrame frame = new JFrame("Task viewer");
        frame.setContentPane(pnlMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Task GetTask(int inx)
    {
        int len = tasks.size();
        if(inx > -1 && inx < len)
        {
            return tasks.get(inx);
        }
        else if(inx >= len && inx < len + newTasks.size())
        {
            return newTasks.get(inx - len);
        }
        else
        {
            return null;
        }
    }

    public void AddTask(Task t)
    {
        newTasks.add(t);
        TaskDataModel tdm = (TaskDataModel)tblTasks.getModel();
        tdm.add(t);
    }

    public void UpdateTask(Task t, int inx)
    {
        int len = tasks.size();
        TaskDataModel tdm = (TaskDataModel)tblTasks.getModel();
        if(inx < len)
        {
            tasks.set(inx, t);
        }
        else
        {
            newTasks.set(inx - len, t);
        }
//        newTasks.set(inx - len, t);
        tdm.updateRow(t, inx);
    }

    public EmployeeInfo GetEmployee(String name)
    {
        return findEmployee(name);
    }

    public String[] GetEmployeeNames()
    {
        String[] ret = new String[employees.size()];
        for(int i = 0; i < ret.length; i++)
        {
            ret[i] = employees.get(i).toString();
        }
        return ret;
    }

    private void initialiseEmployees()
    {
        FileIOManager instance = FileIOManager.getInstance();
        instance.setPath(PATHS[1]);

        String eData = "";
        try
        {
            eData = instance.readFromFile();
        }
        catch(IOException ex)
        {
            System.out.println("Failed to load employee data.\nReason: " + ex.getMessage());
        }
        Pattern patt = Pattern.compile("(([a-zA-Z\\x20]+),([a-zA-Z\\x20]+),(\\d{2}/\\d{2}/\\d{4}),([a-zA-Z\\x20]*))");
        Matcher m = patt.matcher(eData);

        ArrayList<String> managers = new ArrayList<>();
        while(m.find())
        {
            String[] tokens = eData.substring(m.start(), m.end()).split(",");
            // Behold what has to be the most roundabout date creation logic I've *ever* used.
            String[] rawDateParts = tokens[2].split("/");
            int[] dateParts = { Integer.valueOf(rawDateParts[0]), Integer.valueOf(rawDateParts[1]), Integer.valueOf(rawDateParts[2]) };
            Date date = new GregorianCalendar(dateParts[2], dateParts[1] - 1, dateParts[0]).getTime();

            employees.add(new EmployeeInfo(tokens[0], tokens[1], date));
            if(tokens.length > 3)
            {
                managers.add(tokens[3]);
            }
        }

        // assign corresponding manager to each employee
        int len = managers.size();
        for(int i = 1; i < len + 1; i++)
        {
            if(managers.get(i - 1).isEmpty())
            {
                continue;
            }
            employees.get(i).manager(findEmployee(managers.get(i - 1)));
        }

        System.out.println("Employee loading complete. No. employee records loaded: " + employees.size());
    }

    private void loadTaskInformation()
    {
        FileIOManager instance = FileIOManager.getInstance();
        instance.setPath(PATHS[0]);

        String tData = "";
        try
        {
            tData = instance.readFromFile();
        }
        catch(IOException ex)
        {
            System.out.println("Failed to load task data.\nReason: " + ex.getMessage());
        }

        Pattern patt = Pattern.compile("(([\\w\\x20]+),([\\w\\x20]+),(\\d{1,5}),(NEW|OPEN|REVIEW|CLOSED),(\\d{1,2}/\\d{1,2}/\\d{1,4}),(\\d{1,2}/\\d{1,2}/\\d{4})?,(\\d{1,2}/\\d{1,2}/\\d{4})?,([a-zA-Z\\x20]+))");
        Matcher m = patt.matcher(tData);

        while(m.find())
        {
            String data = tData.substring(m.start(), m.end());
            String[] tokens = data.split(",");
            if(tokens.length == 8)
            {
                String[][] rawDateParts = { tokens[4].split("/"), tokens[5].split("/"), tokens[6].split("/") };
                int[][] dateParts =
                        {
                                {Integer.valueOf(rawDateParts[0][0]), Integer.valueOf(rawDateParts[0][1]), Integer.valueOf(rawDateParts[0][2])},
                                {Integer.valueOf(rawDateParts[1][0]), Integer.valueOf(rawDateParts[1][1]), Integer.valueOf(rawDateParts[1][2])},
                                {Integer.valueOf(rawDateParts[2][0]), Integer.valueOf(rawDateParts[2][1]), Integer.valueOf(rawDateParts[2][2])}
                        };
                Date createdDate = new GregorianCalendar(dateParts[0][2], dateParts[0][1] - 1, dateParts[0][0]).getTime();
                Date closedDate = new GregorianCalendar(dateParts[1][2], dateParts[1][1] - 1, dateParts[1][0]).getTime();
                Date dueDate = new GregorianCalendar(dateParts[2][2], dateParts[2][1] - 1, dateParts[2][0]).getTime();
                tasks.add(new Task(tokens[0], tokens[1], Short.valueOf(tokens[2]), TaskStatus.valueOf(tokens[3]), findEmployee(tokens[7]), createdDate, dueDate, closedDate));
            }
            else if(tokens.length == 7)
            {
                String[][] rawDateParts = { tokens[4].split("/"), tokens[5].split("/") };
                int[][] dateParts =
                        {
                                {Integer.valueOf(rawDateParts[0][0]), Integer.valueOf(rawDateParts[0][1]) - 1, Integer.valueOf(rawDateParts[0][2])},
                                {Integer.valueOf(rawDateParts[1][0]), Integer.valueOf(rawDateParts[1][1]) - 1, Integer.valueOf(rawDateParts[1][2])}
                        };

                Date createdDate = new GregorianCalendar(dateParts[0][2], dateParts[0][1], dateParts[0][0]).getTime();

                // determine missing date value
                int count = 0, inx = 0;
                while(count < 4)
                {
                    inx = data.indexOf(",", inx);
                    count++;
                }

                if(data.indexOf(",", inx) - inx == 1)
                {
                    // closed date missing.
                    Date dueDate = new GregorianCalendar(dateParts[1][2], dateParts[1][1], dateParts[1][0]).getTime();
                    tasks.add(new Task(tokens[0], tokens[1], Short.valueOf(tokens[2]), TaskStatus.valueOf(tokens[3]), findEmployee(tokens[7]), createdDate, dueDate, null));
                }
                else
                {
                    // due date missing.
                    Date closedDate = new GregorianCalendar(dateParts[1][2], dateParts[1][1], dateParts[1][0]).getTime();
                    tasks.add(new Task(tokens[0], tokens[1], Short.valueOf(tokens[2]), TaskStatus.valueOf(tokens[3]), findEmployee(tokens[7]), createdDate, null, closedDate));
                }
            }
            else if(tokens.length < 7)
            {
                // both due and closed dates missing, only add created date
                String[] rawDateParts = tokens[4].split("/");
                int[] dateParts = { Integer.valueOf(rawDateParts[0]), Integer.valueOf(rawDateParts[1]) - 1, Integer.valueOf(rawDateParts[2]) };

                Date createdDate = new GregorianCalendar(dateParts[2], dateParts[1], dateParts[0]).getTime();

                tasks.add(new Task(tokens[0], tokens[1], Short.valueOf(tokens[2]), TaskStatus.valueOf(tokens[3]), findEmployee(tokens[7]), createdDate, null,  null));
            }
        }
        System.out.println("Tasks loading complete. No. tasks loaded: " + tasks.size());
    }

    private void reloadModel()
    {
        tasks.clear();
        newTasks.clear();
        loadTaskInformation();
        TaskDataModel model = new TaskDataModel();
        model.addRange(tasks);
        tblTasks.setModel(model);
    }

    private EmployeeInfo findEmployee(String name)
    {
        EmployeeInfo ret = null;
        if(name == null)
        {
            return ret;
        }
        int len = employees.size();
        for(int i = 0; i < len; i++)
        {
            EmployeeInfo e = employees.get(i);
            if(e.fullName().equals(name))
            {
                ret = e;
                break;
            }
        }
        return ret;
    }
}

class TaskDataModel extends AbstractTableModel
{
    final String[] columns = { "Title", "Description", "Priority", "Status", "Created date", "Closed date", "Due date", "Owner" };

    final Class[] classes = { String.class, String.class, short.class, String.class, Date.class, Date.class, Date.class, EmployeeInfo.class };

    final Vector data = new Vector();

    public void add(Task newTask)
    {
        int len = data.size() - 1;
        data.add(newTask);
        fireTableRowsInserted(len + 1, len + 1);
    }

    public void addRange(List<Task> tasks)
    {
        int len = data.size() - 1;
        for(Task t : tasks)
        {
            data.add(t);
        }
        fireTableRowsInserted(len, data.size() - 1);
    }

    public void updateRow(Task newData, int row)
    {
        data.setElementAt(newData, row);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public int getColumnCount()
    {
        return columns.length;
    }

    @Override
    public int getRowCount()
    {
        return data.size();
    }

    @Override
    public String getColumnName(int column)
    {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return classes[columnIndex];
    }

    public Task getAtRow(int row)
    {
        // Cast should work OK, only storing Task objects.
        return (Task)data.get(row);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Task t = getAtRow(rowIndex);
        switch(columnIndex)
        {
            case 0:
                return t.title();
            case 1:
                return t.description();
            case 2:
                return t.priority();
            case 3:
                return t.status().status();
            case 4:
                return t.createdDate();
            case 5:
                return t.closedDate();
            case 6:
                return t.dueDate();
            case 7:
                return t.owner();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        switch(columnIndex)
        {
            case 0:
                data.setElementAt(((Task)data.get(rowIndex)).title((String)aValue), rowIndex);
                break;
            case 1:
                data.setElementAt(((Task)data.get(rowIndex)).description((String)aValue), rowIndex);
                break;
            case 2:
                data.setElementAt(((Task)data.get(rowIndex)).priority((Short)aValue), rowIndex);
                break;
            case 3:
                data.setElementAt(((Task)data.get(rowIndex)).status((TaskStatus) aValue), rowIndex);
                break;
            case 4:
                data.setElementAt(((Task)data.get(rowIndex)).createdDate((Date)aValue), rowIndex);
                break;
            case 5:
                data.setElementAt(((Task)data.get(rowIndex)).closedDate((Date)aValue), rowIndex);
                break;
            case 6:
                data.setElementAt(((Task)data.get(rowIndex)).dueDate((Date)aValue), rowIndex);
                break;
            case 7:
                data.setElementAt(((Task)data.get(rowIndex)).owner((EmployeeInfo)aValue), rowIndex);
            default:
                break;
        }
    }
}

class TaskTableRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        TaskDataModel tdm = (TaskDataModel)table.getModel();
        Task t = tdm.getAtRow(row);
        Date today = new Date();
        if(t.dueDate().before(today))
        {
            setBackground(Color.RED);
        }
        else if(t.status() == TaskStatus.NEW)
        {
            setBackground(Color.CYAN);
        }
        else if(t.status() == TaskStatus.OPEN)
        {
            setBackground(Color.WHITE);
        }
        else if(t.status() == TaskStatus.REVIEW)
        {
            setBackground(Color.YELLOW);
        }
        else if(t.status() == TaskStatus.CLOSED)
        {
            setBackground(Color.GRAY);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
