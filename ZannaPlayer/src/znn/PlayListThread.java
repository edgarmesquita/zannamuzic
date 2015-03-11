/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author Edgar
 */
public class PlayListThread implements Runnable {

    private final PlayListItem _item;
    private final PlayerAudioDevice audioDevice;
    private final Player player;
    private final Object playerLock = new Object();
    private int playerStatus = MediaList.NOTSTARTED;
    private int currentDuration;
    public PlayListThread(PlayListItem item) throws FileNotFoundException, JavaLayerException {
        this._item = item;
        InputStream is = new FileInputStream(item.getFile());
        this.audioDevice = new PlayerAudioDevice();
        //this.audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
        this.player = new Player(is, this.audioDevice);

        playerStatus = MediaList.NOTSTARTED;
    }

    public Object getPlayerLock()
    {
        return this.playerLock;
    }
    
    public PlayerAudioDevice getAudioDevice()
    {
        return this.audioDevice;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    public int getStatus() {
        return this.playerStatus;
    }

    public void setStatus(int status) {
        this.playerStatus = status;
    }
    
    public int getCurrentDuration()
    {
        return this.currentDuration;
    }
    
    public PlayListItem getItem()
    {
        return this._item;
    }

    private boolean isFaded = false;

    @Override
    public void run() {
        while (playerStatus != MediaList.FINISHED) {
            try {
                float duration = (float) _item.getDuration();
                float fadeOut = (duration - 15000);

                if (!player.play(1)) {
                    break;
                }
                currentDuration = player.getPosition();

                if (currentDuration == 0) {
                    isFaded = false;
                    this.audioDevice.setLineGain(0);
                }
                if (currentDuration > 0 && currentDuration < fadeOut && !isFaded) {
                    this.audioDevice.fadeIn(0, _item.getPlayList().getVolume(), 5);
                    isFaded = true;
                }

                if (currentDuration >= fadeOut && isFaded) {
                    this.audioDevice.fadeIn(_item.getPlayList().getVolume(), 0, 15);
                    this.playerStatus = MediaList.FINISHING;
                    isFaded = false;
                }

            } catch (final Exception e) {
                break;
            }
            // check if paused or terminated
            synchronized (playerLock) {
                while (playerStatus == MediaList.PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
    }

    public void close() {
        synchronized (playerLock) {
            playerStatus = MediaList.FINISHED;
            
        }
        try {
            player.close();
            this.getItem().getPlayList().finalizeFinishedThread();
        } catch (final Exception e) {
            // ignore, we are terminating anyway
        } catch (Throwable ex) {
            
        }
    }
}
