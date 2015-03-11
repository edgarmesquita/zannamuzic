/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import javazoom.jl.decoder.JavaLayerException;
import znn.models.*;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayList extends MediaList
{

    private ArrayList<PlayListItem> _list;
    protected int current = 0;

    private Thread playerThread;
    private Thread playerThreadAux;

    private PlayListThread playerRunnable;
    private PlayListThread playerRunnableAux;

    private float volume = 0.9f;
    private ArrayList<PlayListItem> _listFinal;

    private PlayListItem currentItem;

    public PlayListItem getCurrentItem()
    {

        if (currentItem != null)
        {
            return currentItem;
        }

        if (_list.size() > 0)
        {
            return _list.get(current);
        }
        return null;
    }

    public ArrayList<PlayListItem> getItems()
    {
        return this._list;
    }

    public ArrayList<PlayListItem> getItemsFinal()
    {
        return this._listFinal;
    }

    public PlayListThread getCurrentRunnable()
    {
        String currentFileName = this.getCurrentItem().getFile().getName();
        if (this.playerRunnable != null && currentFileName.equals(this.playerRunnable.getItem().getFile().getName()))
        {
            return this.playerRunnable;
        }
        else if (this.playerRunnableAux != null && currentFileName.equals(this.playerRunnableAux.getItem().getFile().getName()))
        {
            return this.playerRunnableAux;
        }
        return null;
    }

    public PlayListThread getLatestRunnable()
    {
        String currentFileName = this.getCurrentItem().getFile().getName();
        if (this.playerRunnable != null && !currentFileName.equals(this.playerRunnable.getItem().getFile().getName()))
        {
            return this.playerRunnable;
        }
        else if (this.playerRunnableAux != null && !currentFileName.equals(this.playerRunnableAux.getItem().getFile().getName()))
        {
            return this.playerRunnableAux;
        }
        return null;
    }

    public void sortItems()
    {
        //Collections.sort(_list, new PlayListItemComparator());
        Collections.shuffle(_list);
        _listFinal = _list;
    }

    public void sortItems(int mood)
    {

        //Collections.sort(_list, new PlayListItemComparator());
        Collections.shuffle(_list);
        _listFinal = _list;
    }

    public String getCurrentTime()
    {

        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        return df.format(this.getCurrentRunnable().getCurrentDuration());
    }

    public double getCurrentPercent()
    {
        PlayListThread runnable = this.getCurrentRunnable();
        if (runnable != null)
        {
            double duration = runnable.getItem().getDuration();
            return ((double) runnable.getCurrentDuration() * 100) / duration;
        }
        return 0;
    }

    public double getLatestPercent()
    {
        PlayListThread runnable = this.getLatestRunnable();
        if (runnable != null)
        {
            double duration = runnable.getItem().getDuration();
            return ((double) runnable.getCurrentDuration() * 100) / duration;
        }
        return 0;
    }

    public int getTotal()
    {
        return _list.size();
    }

    public int getStatus()
    {

        int status = getCurrentRunnable().getStatus();
        return status;
    }

    /**
     *
     */
    public PlayList(ArrayList<PlayerCalibragem> listCalibre) throws JavaLayerException, FileNotFoundException
    {

        _list = new ArrayList<PlayListItem>();
        if (listCalibre != null)
        {
            readLocalList(listCalibre);
            sortItems();
        }
        this.playerRunnable = new PlayListThread(getCurrentItem());
    }

    private void readLocalList(ArrayList<PlayerCalibragem> listCalibre)
    {
        String root = System.getProperty("user.dir");
        File folder = new File(root + "/data/musics/");
        File[] files = folder.listFiles();
        ObjectContainer container = ApplicationContext.getCurrent().getContainer();

        if (files != null)
        {
            for (final File fileEntry : folder.listFiles())
            {
                if (fileEntry.isFile())
                {
                    String fileName = fileEntry.getName();
                    String extension = "";

                    int i = fileName.lastIndexOf('.');
                    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

                    if (i > p)
                    {
                        extension = fileName.substring(i + 1).toLowerCase();
                    }

                    if ("zm".equals(extension))
                    {
                        System.out.println("fileName: " + fileName);
                        Music music = new Music(fileName);
                        PlayListItem playListItem = null;

                        if (container != null)
                        {

                            ObjectSet result = container.queryByExample(music);

                            if (result != null && result.hasNext())
                            {
                                //System.out.println("Tenho na base");
                                music = (Music) result.next();
                                PlayerCalibragem plc = new PlayerCalibragem();

                                if (listCalibre.size() > 0)
                                {
                                    boolean calibrev = false;
                                    if (listCalibre.size() > 0)
                                    {
                                        for (int x = 0; x < listCalibre.size(); x++)
                                        {
                                            if (listCalibre.get(x).getMoodID() == music.getMoodID())
                                            {
                                                calibrev = true;
                                            }
                                        }
                                    }
                                    if (calibrev)
                                    {
                                        playListItem = new PlayListItem(this, fileEntry);

                                        // Obtendo avaliação
                                        String r = playListItem.setRate(music.getRate());
                                        playListItem.setPosition(music.getPosition());

                                        music.setArtist(playListItem.getArtist());
                                        music.setTitle(playListItem.getTitle());
                                        music.setDuration(playListItem.getDuration());
                                    }
                                }
                                else
                                {
                                    playListItem = new PlayListItem(this, fileEntry);

                                    // Obtendo avaliação
                                    String r = playListItem.setRate(music.getRate());
                                    playListItem.setPosition(music.getPosition());

                                    music.setArtist(playListItem.getArtist());
                                    music.setTitle(playListItem.getTitle());
                                    music.setDuration(playListItem.getDuration());
                                }
                                //System.out.println("Music finded");
                                //System.out.println(music);
                            }
                            else
                            {
                                if ("zm".equals(extension))
                                {
                                    fileEntry.delete();
                                }
                                // System.out.println("nao tenho na base");
                                //container.store(music);
                                // System.out.println("Music stored");
                                System.out.println(music);
                                continue;
                            }
                        }
                        if (playListItem != null)
                        {
                            _list.add(playListItem);
                        }
                    }
                }
            }
        }
    }

    public float getVolume()
    {
        return this.volume;
    }

    public void setVolume(float i)
    {
        this.getCurrentRunnable().getAudioDevice().setLineGain(i);
        this.volume = i;
    }

    @Override
    public void play()
    {
        synchronized (this.getCurrentRunnable().getPlayerLock())
        {
            switch (this.getCurrentRunnable().getStatus())
            {
                case MediaList.NOTSTARTED:
                    if (_list.size() >= current + 1)
                    {

                        Thread thread = null;
                        if (this.playerRunnableAux != null)
                        {
                            this.playerThreadAux = new Thread(this.getCurrentRunnable());
                            thread = this.playerThreadAux;
                        }
                        else
                        {
                            this.playerThread = new Thread(this.getCurrentRunnable());
                            thread = this.playerThread;
                        }

                        thread.setDaemon(true);
                        thread.setPriority(Thread.MAX_PRIORITY);
                        this.getCurrentRunnable().setStatus(MediaList.PLAYING);
                        thread.start();
                    }
                    break;
                case MediaList.PAUSED:
                    resume();
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause()
    {
        synchronized (this.getCurrentRunnable().getPlayerLock())
        {
            if (this.getCurrentRunnable().getStatus() == MediaList.PLAYING)
            {
                this.getCurrentRunnable().setStatus(MediaList.PAUSED);
            }
            return this.getCurrentRunnable().getStatus() == MediaList.PAUSED;
        }
    }

    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    @Override
    public boolean resume()
    {
        synchronized (this.getCurrentRunnable().getPlayerLock())
        {
            if (this.getCurrentRunnable().getStatus() == MediaList.PAUSED)
            {
                this.getCurrentRunnable().setStatus(MediaList.PLAYING);
                this.getCurrentRunnable().getPlayerLock().notifyAll();
            }
            return this.getCurrentRunnable().getStatus() == MediaList.PLAYING;
        }
    }

    private void reset() throws FileNotFoundException, JavaLayerException
    {
        if (this.playerRunnableAux != null)
        {
            this.playerThreadAux.interrupt();
            this.playerRunnableAux.close();
            this.playerThreadAux = null;
            this.playerRunnableAux = null;
        }
        if (this.playerRunnable != null)
        {
            this.playerThread.interrupt();
            this.playerRunnable.close();
            this.playerThread = null;
            this.playerRunnable = new PlayListThread((getCurrentItem()));
        }
    }

    public void next() throws FileNotFoundException, JavaLayerException
    {

        int status = this.getCurrentRunnable().getStatus();

        this.current++;
        if (this.current > this._list.size() - 1)
        {
            this.current = 0;
        }

        if (status != MediaList.NOTSTARTED)
        {
            if (status == MediaList.FINISHING)
            {
                if (this.playerRunnableAux == null)
                {
                    this.playerRunnableAux = new PlayListThread(getCurrentItem());
                }
                else
                {
                    this.playerRunnable = new PlayListThread((getCurrentItem()));
                }
            }
            else
            {
                reset();
            }
        }
        play();
    }

    public void finalizeFinishedThread()
    {
        if (this.playerRunnable != null && this.playerRunnable.getStatus() == MediaList.FINISHED)
        {
            this.playerThread.interrupt();
            this.playerRunnable.close();
            this.playerThread = null;
            this.playerRunnable = null;
        }
        else if (this.playerRunnableAux != null && this.playerRunnableAux.getStatus() == MediaList.FINISHED)
        {
            this.playerThreadAux.interrupt();
            this.playerRunnableAux.close();
            this.playerThreadAux = null;
            this.playerRunnableAux = null;
        }
    }

    public void prev() throws FileNotFoundException, JavaLayerException
    {

        int status = this.getCurrentRunnable().getStatus();
        this.current--;

        if (this.current < 0)
        {
            this.current = this._list.size() - 1;
        }

        if (status != MediaList.NOTSTARTED)
        {

            if (status == MediaList.FINISHING)
            {
                if (this.playerRunnableAux == null)
                {
                    this.playerRunnableAux = new PlayListThread(getCurrentItem());
                }
                else
                {
                    this.playerRunnable = new PlayListThread((getCurrentItem()));
                }
            }
            else
            {
                reset();
            }
        }

        play();
    }

    public void stop()
    {
        synchronized (this.getCurrentRunnable().getPlayerLock())
        {
            this.getCurrentRunnable().setStatus(MediaList.FINISHED);
            this.getCurrentRunnable().getPlayerLock().notifyAll();
        }
    }

    @Override
    public void close()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
