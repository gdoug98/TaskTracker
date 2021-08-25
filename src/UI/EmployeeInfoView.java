package UI;

import javax.swing.*;
import java.util.*;
import Entities.*;

public class EmployeeInfoView
{
    private JLabel lblName;
    private JLabel lblRole;
    private JLabel lblAge;
    private JLabel lblManager;
    private JPanel pnlContainer;
    private JLabel dspName;
    private JLabel dspRole;
    private JLabel dspAge;
    private JLabel dspManager;

    private EmployeeInfo info;

    private String calculateAge()
    {
        long numMilliseconds = new Date().getTime() - info.dob().getTime();

        long numYears = (long)(numMilliseconds / (365.25 * 86400000));
        long remMonths = (long)(numMilliseconds % (365.25 * 86400000));
        long numMonths = (long)(remMonths / (29.6667 * 86400000));

        return (int)numYears + " years, " + (int)numMonths + " months";
    }

    public EmployeeInfoView(EmployeeInfo _info)
    {
        info = _info;

        dspName.setText(info.fullName());
        dspRole.setText(info.role());
        dspAge.setText(calculateAge());
        if(info.manager() == null)
        {
            dspManager.setText("N/A");
        }
        else
        {
            dspManager.setText(info.manager().fullName());
        }


        JFrame main = new JFrame("Details for employee #" + info.Id());
        main.setContentPane(pnlContainer);
        main.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        main.pack();
        main.setVisible(true);
    }

}

