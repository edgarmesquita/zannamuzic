/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.models;

/**
 *
 * @author Edgar Mesquita
 */
public class Music
{
    private String fileName;
    private String title;
    private String artist;
    private String musicaID;
    private int duration;
    private int rate;
    private boolean checked;
    private int position;
    private int mood;
    public int getMoodID()
    {
        return mood;
    }
    public void setMoodID(int mood)
    {
        this.mood = mood;
    }
    public String getMusicaID()
    {
        return musicaID;
    }
    public void setMusicaID(String musicaID)
    {
        this.musicaID = musicaID;
    }
    public Music(String fileName)
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
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getArtist()
    {
        return artist;
    }
    public void setArtist(String artist)
    {
        this.artist = artist;
    }
    
    public int getDuration()
    {
        return duration;
    }
    public void setDuration(int duration)
    {
        this.duration = duration;
    }
    
    public int getRate()
    {
        return rate;
    }
    public void setRate(int rate)
    {
        this.rate = rate;
    }
    
    public boolean isChecked()
    {
        return checked;
    }
    
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
    
    public int getPosition()
    {
        return position;
    }
    
    public void setPosition(int position)
    {
        this.position = position;
    }
}
