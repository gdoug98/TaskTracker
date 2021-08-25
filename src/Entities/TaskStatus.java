package Entities;

public enum TaskStatus
{
    NEW("New"),
    OPEN("Open"),
    REVIEW("In Review"),
    CLOSED("Closed");

    private final String statusText;

    TaskStatus(String status)
    {
        statusText = status;
    }

    public String status() {return statusText; }
}