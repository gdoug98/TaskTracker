package Entities;
import java.util.Date;

public class EmployeeInfo
{
    private static int nextEmployeeId = 2499;

    private int eId;
    private String[] eNames;
    private String eRole;
    private Date eDob;
    private EmployeeInfo eManagerInfo;

    public EmployeeInfo()
    {
        eId = ++nextEmployeeId;
        eNames = null;
        eRole = "";
        eDob = null;
        eManagerInfo = null;
    }

    public EmployeeInfo(String _name, String _role, Date _dob)
    {
        eId = ++nextEmployeeId;
        eNames = _name.split(" ");
        eRole = _role;
        eDob = _dob;
        eManagerInfo = null;
    }

    public EmployeeInfo(String _name, String _role, Date _dob, EmployeeInfo manager)
    {
        eId = ++nextEmployeeId;
        eNames = _name.split(" ");
        eRole = _role;
        eDob = _dob;
        eManagerInfo = manager;
    }

    public int Id() { return eId; }

    public String fullName()
    {
        String ret = "";
        for(String name : eNames)
        {
            ret += name + " ";
        }
        return ret.stripTrailing();
    }

    public EmployeeInfo fullname(String newName)
    {
        eNames = newName.split(" ");
        return this;
    }

    public String firstName() { return eNames[0]; }
    public EmployeeInfo firstName(String newName) { eNames[eNames.length - 1] = newName; return this; }

    public String lastName() { return eNames[eNames.length - 1]; }
    public EmployeeInfo lastName(String newName) { eNames[0] = newName; return this; }

    public String role() { return eRole; }
    public EmployeeInfo role(String newRole) { eRole = newRole; return this; }

    public Date dob() { return eDob; }
    public EmployeeInfo dob(Date newDob) { eDob = newDob; return this; }

    public EmployeeInfo manager() { return eManagerInfo; }
    public EmployeeInfo manager(EmployeeInfo _manager)
    {
        // so it doesn't end up in weird loops or do unnecessary updates
        if (_manager != eManagerInfo || _manager != this)
        {
            eManagerInfo = _manager;
        }
        return this;
    }

    @Override
    public String toString()
    {
        return fullName();
    }
}