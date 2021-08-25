package Entities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Task
{
    private Date createdDate;
    private Date closedDate;
    private Date dueDate;
    private EmployeeInfo owner;
    private String title;
    private String description;
    private short priority;
    private TaskStatus status;

    public Task()
    {
        title = "";
        description = "";
        priority = -1;
        owner = null;
        createdDate = null;
        closedDate = null;
        dueDate = null;
        status = TaskStatus.NEW;
    }

    public Task(String _title, String _desc, short _priority, EmployeeInfo _owner)
    {
        title = _title;
        description = _desc;
        priority = _priority;
        owner = _owner;
        createdDate = new Date();
        closedDate = null;
        dueDate = null;
    }

    public Task(String _title, String _desc, short _priority, EmployeeInfo _owner, Date _createdDate, Date _dueDate, Date _closedDate)
    {
        title = _title;
        description = _desc;
        priority = _priority;
        owner = _owner;
        status = TaskStatus.NEW;
        createdDate = _createdDate;
        closedDate = _closedDate;
        dueDate = _dueDate;
    }

    public Task(String _title, String _desc, short _priority, TaskStatus _status, EmployeeInfo _owner, Date _createdDate, Date _dueDate, Date _closedDate)
    {
        title = _title;
        description = _desc;
        priority = _priority;
        status = _status;
        owner = _owner;
        createdDate = _createdDate;
        closedDate = _closedDate;
        dueDate = _dueDate;
    }
    
    public String title() { return title; }
    public Task title(String newTitle) { title = newTitle; return this; }
    
    public String description() { return description; }
    public Task description(String newDescription) { description = newDescription; return this; }
    
    public short priority() { return priority; }
    public Task priority(short _priority) { priority = _priority; return this; }
    
    public Date createdDate() { return createdDate; }
    public Task createdDate(Date _createdDate) { createdDate = _createdDate; return this; }

    public Date dueDate() { return dueDate; }
    public Task dueDate(Date _dueDate) { dueDate = _dueDate; return this; }

    public Date closedDate() { return closedDate; }
    public Task closedDate(Date _closedDate) { closedDate = _closedDate; return this; }

    public EmployeeInfo owner() { return owner; }
    public Task owner(EmployeeInfo _owner) { owner = _owner; return this; }

    public TaskStatus status() { return status; }
    public Task status(TaskStatus newStatus) { status = newStatus; return this; }

    public String writeCSV()
    {
        String[] dates = new String[3];

        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        dates[0] = toFormattedDate(cal);
        if(closedDate != null)
        {
            cal.setTime(closedDate);
            dates[1] = toFormattedDate(cal);
        }
        if(dueDate != null)
        {
            cal.setTime(dueDate);
            dates[2] = toFormattedDate(cal);
        }


        return title  + "," + description + "," + priority + "," + status.name() + "," + dates[0] + "," + dates[1] + "," + dates[2] + "," + owner + "\n";
    }

    private String toFormattedDate(Calendar cal)
    {
        return cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }
}
