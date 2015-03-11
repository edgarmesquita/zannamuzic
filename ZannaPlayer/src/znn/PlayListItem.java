/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import znn.models.Music;
import znn.models.PlayerDetails;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListItem
{

    private PlayerDetails playerDetails;
    private File _file;
    private AudioFile _audioFile;
    private int _rate;
    private ObjectContainer _container;
    private int _position;
    private Boolean _Checked;
    private int _mood;
    private PlayList list;

    public PlayListItem(PlayList playList, File file)
    {
        list = playList;

        _container = ApplicationContext.getCurrent().getContainer();
        try
        {
            this._audioFile = new MP3File(file, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, true);
            this._file = file;
        }
        catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex)
        {
            Logger.getLogger(PlayListItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlayList getPlayList()
    {
        return this.list;
    }

    public File getFile()
    {
        return this._file;
    }

    public String getTitle()
    {
        String title = "";
        if (_audioFile.getTag().getFirst(FieldKey.TITLE).length() > 23)
        {
            title = _audioFile.getTag().getFirst(FieldKey.TITLE).substring(0, 23) + " ...";
        }
        else
        {
            title = _audioFile.getTag().getFirst(FieldKey.TITLE);
        }

        return title;
    }

    public String getArtist()
    {

        String Artist = "";
        if (_audioFile.getTag().getFirst(FieldKey.ARTIST).length() > 12)
        {
            Artist = _audioFile.getTag().getFirst(FieldKey.ARTIST).substring(0, 12) + " ...";
        }
        else
        {
            Artist = _audioFile.getTag().getFirst(FieldKey.ARTIST);
        }

        return Artist;
    }

    public int getDuration()
    {
        return _audioFile.getAudioHeader().getTrackLength() * 1000;
    }

    public int getRate()
    {
        return _rate;
    }

    public int getMood()
    {
        Music music = new Music(_file.getName());

        if (_container != null)
        {
            //Music music = new Music(_file.getName());
            ObjectSet result = _container.queryByExample(music);
            if (result != null && result.hasNext())
            {
                music = (Music) result.next();

                return music.getMoodID();
            }
            else
            {
                return 0;
            }
        }

        return _mood;
    }

    public String setRate(int rate)
    {
        Music music = new Music(_file.getName());
        if (_container != null)
        {
            //Music music = new Music(_file.getName());
            ObjectSet result = _container.queryByExample(music);

            if (result != null && result.hasNext())
            {
                music = (Music) result.next();

                if (music.getRate() != rate)
                {
                    music.setRate(rate);

                    _container.store(music);
                    System.out.println("Music '" + music.getFileName() + "' rate updated");
                }
            }
        }
        _rate = rate;
        return music.getMusicaID();
    }

    public int getPosition()
    {
        return _position;
    }

    public void setPosition(int position)
    {
        if (_container != null)
        {
            Music music = new Music(_file.getName());
            ObjectSet result = _container.queryByExample(music);

            if (result != null && result.hasNext())
            {
                music = (Music) result.next();

                if (music.getPosition() != position)
                {
                    music.setPosition(position);

                    _container.store(music);
                    System.out.println("Music '" + music.getFileName() + "' position updated");
                }
            }
        }
        _position = position;
    }

    public int getIndex()
    {
        return list.getItems().indexOf(this);
    }
}
