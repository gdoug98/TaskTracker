package UI;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import Entities.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

public class TaskMakerUI
{
    private JPanel pnlMain;
    private JPanel pnlContainer;
    private JButton cmdCreate;
    private JTextField txtTitle;
    private JFormattedTextField txtPriority;
    private JFormattedTextField txtDue;
    private JFormattedTextField txtClosed;
    private JTextArea txtDescription;
    private JLabel lblDescription;
    private JLabel lblTitle;
    private JLabel lblPrioity;
    private JLabel lblDue;
    private JLabel lblClosed;
    private JLabel lblOwner;
    private JComboBox cmbOwner;
    private JLabel lblStatus;
    private JRadioButton rbStatusOpen;
    private JRadioButton rbStatusReview;
    private JRadioButton rbStatusClosed;
    private JSpinner spnPriority;
    private JSpinner spnClosedDate;
    private JSpinner spnDueDate;

    private TaskViewUI parent;
    private int original;

    public TaskMakerUI(TaskViewUI _parent, int originalInx)
    {
        parent = _parent;
        original = originalInx;
        setupSpinners();
        initialiseStatusButtons();
        registerEmployeeOptions();
        if(original > -1)
        {
            intialiseTaskData();
        }
        cmdCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               JFrame src = (JFrame)SwingUtilities.windowForComponent((Component)e.getSource());

                String title = txtTitle.getText();
                String desc = txtDescription.getText();
                short priority = (short)((int)spnPriority.getValue());
                Date createdDate = new Date();
                Date dueDate = (Date)spnDueDate.getValue();
                Date closedDate = (Date)spnClosedDate.getValue();
                TaskStatus taskStatus = getStatus();
                EmployeeInfo owner = parent.GetEmployee((String)cmbOwner.getSelectedItem());
                if(original != -1)
                {
                    Task og = parent.GetTask(original);
                    dueDate = dueDate == null ? og.dueDate() : dueDate;
                    closedDate = closedDate == null ? og.closedDate() : closedDate;
                    Task tk = og.title(title).description(desc).priority(priority).status(taskStatus).dueDate(dueDate).closedDate(closedDate).owner(owner);
                    parent.UpdateTask(tk, originalInx);
                }/*
                */
                else
                {
                    Task tk = new Task(title, desc, priority, owner, createdDate, dueDate, closedDate);
                    parent.AddTask(tk);
                }
                JOptionPane.showMessageDialog(src, "Task details added successfully.");
                src.dispatchEvent(new WindowEvent(src, WindowEvent.WINDOW_CLOSING)); // close window once this is done
            }
        });
        spnPriority.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                System.out.println("Value set to " + evt.getNewValue());
            }
        });
        JFrame frame = new JFrame("Make/Update Task");
        frame.setContentPane(pnlMain);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private TaskStatus getStatus()
    {
        if(rbStatusOpen.isSelected())
        {
            return TaskStatus.OPEN;
        }
        else if(rbStatusReview.isSelected())
        {
            return TaskStatus.REVIEW;
        }
        else if(rbStatusClosed.isSelected())
        {
            return TaskStatus.CLOSED;
        }
        else
        {
            return TaskStatus.NEW;
        }
    }

    private void initialiseStatusButtons()
    {
        if(original == -1)
        {
            rbStatusClosed.setEnabled(false);
        }
        else
        {
            TaskStatus status = parent.GetTask(original).status();
            Date closedDate = (Date)spnClosedDate.getValue();
            if(status == TaskStatus.NEW || status == TaskStatus.OPEN || closedDate.after(new Date()))
            {
                rbStatusClosed.setEnabled(false);
            }
        }
    }

    private void registerEmployeeOptions()
    {
        for(String name : parent.GetEmployeeNames())
        {
            cmbOwner.addItem(name);
        }
    }

    private void setupSpinners()
    {
        // set up date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // set up number format
        NumberFormat nf = NumberFormat.getIntegerInstance();

        // doing this cos IntelliJ's form designer is shit at handling spinners.
        SpinnerNumberModel priorityModel = new SpinnerNumberModel();
        priorityModel.setMinimum(1);
        priorityModel.setMaximum(32767);
        priorityModel.setStepSize(1);
        priorityModel.setValue(1);


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -10);
        Date earliest = cal.getTime();
        Date latest = new GregorianCalendar(9999, 0, 1).getTime();
        SpinnerDateModel closedDateModel = new SpinnerDateModel(latest, earliest, latest, Calendar.DATE);
        SpinnerDateModel dueDateModel = new SpinnerDateModel(new Date(), earliest, latest, Calendar.DATE);

        spnPriority.setModel(priorityModel);
//        spnPriority.setPreferredSize(new Dimension(20,10));
//        spnPriority.setEditor(txtPriority);

        spnClosedDate.setModel(closedDateModel);
        JFormattedTextField closedDateEditor = ((JSpinner.DateEditor)spnClosedDate.getEditor()).getTextField();
        closedDateEditor.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(sdf)));
//        spnClosedDate.setPreferredSize(new Dimension(20, 10));
//        spnClosedDate.setEditor(txtClosed);

        spnDueDate.setModel(dueDateModel);
        JFormattedTextField dueDateEditor = ((JSpinner.DateEditor)spnDueDate.getEditor()).getTextField();
        dueDateEditor.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(sdf)));
    }

//    private void createUIComponents()
//    {
//        setupSpinners();
//    }

    private void intialiseTaskData()
    {
        Task t = parent.GetTask(original);
        txtTitle.setText(t.title());
        txtDescription.setText(t.description());
        spnPriority.setValue((int)t.priority());
        spnClosedDate.setValue(t.closedDate());
        spnDueDate.setValue(t.dueDate());
        cmbOwner.setSelectedItem(t.owner().fullName());
        switch(t.status())
        {
            case OPEN:
                rbStatusOpen.setEnabled(true);
                break;
            case REVIEW:
                rbStatusReview.setEnabled(true);
            case CLOSED:
                rbStatusClosed.setEnabled(true);
            case NEW:
            default:
                break;
        }
    }
}
