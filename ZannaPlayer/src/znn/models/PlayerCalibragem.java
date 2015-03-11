/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package znn.models;

/**
 *
 * @author marcello
 */
public class PlayerCalibragem {
    private int mood;
    private String horainicio;
    private String horafim;
    
    public PlayerCalibragem(int mood)
    {
        this.mood = mood;
    }
    public PlayerCalibragem()
    {
        //this.mood = mood;
    }
    public int getMoodID()
    {
        return this.mood;
    }   
    public void setMoodID(int mood)
    {
        this.mood = mood;
    } 
    public String gethorainicio()
    {
        return this.horainicio;
    }   
    public void sethorainicio(String horainicio)
    {
        this.horainicio = horainicio;
    }
    
    public String gethorafim()
    {
        return this.horafim;
    }   
    public void sethorafim(String horafim)
    {
        this.horafim = horafim;
    }
    
}
