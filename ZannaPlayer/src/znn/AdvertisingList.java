/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;
import znn.models.Advertising;

/**
 *
 * @author Edgar Mesquita
 */
public class AdvertisingList extends MediaList
{

    private Timer _timer = new Timer(1, null);
    private AudioDevice _audioDevice;
    private Player _player;
    private Thread _playerThread;
    private final Object _playerLock = new Object();
    private ArrayList<AdvertisingListItem> _list;
    private int _current = 0;
    private int _currentDuration = 0;
    private int _status = 0;
    private ArrayList<AdvertisingListItem> _listsort;

    public int getStatus()
    {
        return _status;
    }

    private void ordenaPorSleepTime(ArrayList<AdvertisingListItem> lista)
    {
        Collections.sort(lista, new Comparator<AdvertisingListItem>()
        {
            @Override
            public int compare(AdvertisingListItem o1, AdvertisingListItem o2)
            {
                return o1.getSleepTime() - o2.getSleepTime();
//return o1.getRate().compareTo(o2.getRate());  
                //return new Integer( o1.getSleepTime() ).compareTo( new Integer( o2.getSleepTime() ) );
                //return (o1.getSleepTime()>o2.getSleepTime() ? o1.getSleepTime() : o2.getSleepTime());
            }

        });
    }

    public AdvertisingListItem getCurrentItem()
    {

        //Collections.sort(_list);
        if (_list.size() > 0)
        {
            return _list.get(_current);
        }
        return null;
    }

    public AdvertisingList() throws JavaLayerException, FileNotFoundException
    {
        _list = new ArrayList<AdvertisingListItem>();
        readLocalList();
        ordenaPorSleepTime(_list);
        createPlayer();
    }

    private void createPlayer() throws FileNotFoundException, JavaLayerException
    {
        if (_list.size() > 0)
        {
            InputStream is = new FileInputStream(getCurrentItem().getFile());
            this._audioDevice = FactoryRegistry.systemRegistry().createAudioDevice();
            this._player = new Player(is, this._audioDevice);
            _status = MediaList.NOTSTARTED;
        }
    }

    private void readLocalList()
    {
        String root = System.getProperty("user.dir");
        File folder = new File(root + "/data/advertising/");
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
                        Advertising advertising = new Advertising(fileName);
                        AdvertisingListItem item = new AdvertisingListItem(fileEntry);

                        if (container != null)
                        {
                            ObjectSet result = container.queryByExample(advertising);
                            if (result != null && result.hasNext())
                            {
                                advertising = (Advertising) result.next();
                                item.setSleepTime(advertising.getSleepTime());

                                System.out.println("Advertising finded");
                                System.out.println(advertising);
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
                                System.out.println(advertising);
                                continue;
                            }
                        }
                        _list.add(item);

                    }
                }
            }
            //  Collections.sort(_list.);

        }
    }

    @Override
    public void play()
    {
        synchronized (_playerLock)
        {
            switch (_status)
            {
                case MediaList.NOTSTARTED:
                    if (_list.size() >= _current + 1)
                    {
                        this._playerThread = new Thread(this);
                        this._playerThread.setDaemon(true);
                        this._playerThread.setPriority(Thread.MAX_PRIORITY);

                        _status = MediaList.PLAYING;
                        try
                        {
                            Thread.sleep(8000);
                        }
                        catch (InterruptedException ex)
                        {
                            System.out.println(ex.getMessage());
                        }
                        this._playerThread.start();
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
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    @Override
    public boolean resume()
    {
        synchronized (_playerLock)
        {
            if (_status == MediaList.PAUSED)
            {
                _status = MediaList.PLAYING;
                _playerLock.notifyAll();
            }
            return _status == MediaList.PLAYING;
        }
    }

    /**
     * Closes the player, regardless of current state.
     */
    @Override
    public void close()
    {
        synchronized (_playerLock)
        {
            _status = MediaList.FINISHED;
            _current++;
            if (_current >= _list.size())
            {
                _current = 0;
            }
        }
        try
        {
            _player.close();
        }
        catch (final Exception e)
        {
            // ignore, we are terminating anyway
        }
    }

    @Override
    public void run()
    {
        while (_status != MediaList.FINISHED)
        {
            try
            {
                if (!_player.play(1))
                {
                    break;
                }
                _currentDuration = _player.getPosition();
            }
            catch (final Exception e)
            {
                break;
            }
            // check if paused or terminated
            synchronized (_playerLock)
            {
                while (_status == MediaList.PAUSED)
                {
                    try
                    {
                        _playerLock.wait();
                    }
                    catch (final InterruptedException e)
                    {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
    }

    public void reset() throws FileNotFoundException, JavaLayerException
    {
        this._playerThread.interrupt();
        this._player.close();
        createPlayer();
    }
}
