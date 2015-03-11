package znn.models;

/**
 *
 * @author Edgar Mesquita
 */
public class Advertising
{
    private String fileName;
    private int sleepTime;
    
    public Advertising(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public int getSleepTime()
    {
        return sleepTime;
    }
    public void setSleepTime(int sleepTime)
    {
        this.sleepTime = sleepTime;
    }
}