/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

public class AdvertisingListItem
{
    private File _file;
    private AudioFile _audioFile;
    private int _sleepTime;
    private int _rate;
    public File getFile()
    {
        return this._file;
    }
    
    public int getSleepTime()
    {
        return _sleepTime;
    }
    public void setSleepTime(int sleepTime)
    {
        _sleepTime = sleepTime;
    }
    public int getDuration()
    {
        return _audioFile.getAudioHeader().getTrackLength() * 1000;
    }
    public int getRate()
    {
        return _rate;
    }
    public AdvertisingListItem(File file)
    {
        try
        {
            this._audioFile = new MP3File(file, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, true);
            this._file = file;
        }
        catch ( IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex)
        {
            Logger.getLogger(AdvertisingListItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}