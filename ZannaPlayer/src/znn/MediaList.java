/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import javazoom.jl.player.advanced.PlaybackListener;

/**
 *
 * @author Edgar Mesquita
 */
public abstract class MediaList extends PlaybackListener implements Runnable
{
    public final static int NOTSTARTED = 0;
    public final static int PLAYING = 1;
    public final static int PAUSED = 2;
    public final static int FINISHED = 3;
    public final static int FINISHING = 4;
    
    public abstract void play();
    public abstract boolean resume();
    public abstract void close();
}