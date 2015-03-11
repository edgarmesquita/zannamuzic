package znn;

import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import znn.models.PlayerDetails;
import znn.ui.MainFrame;

/**
 *
 * @author Edgar Mesquita
 */
public class ApplicationContext
{

    private Preferences _preferences;
    private static ApplicationContext _current;
    private ObjectContainer _container;

    private String _containerFileName = "ZannaPlayer.yap";
    public ApplicationContext()
    {
        _current = this;
        this._preferences = Preferences.userNodeForPackage(this.getClass());
        
        if(_container == null)
        {
            File containerFile = new File(_containerFileName);
            if(containerFile.exists())
            {
                //ObjectServer objServer = Db4o.openServer(_containerFileName, 0);
                //objServer.grantAccess("zannasound", "z@nn@$ound");
                //this._container = objServer.openClient();
                this._container = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), _containerFileName);
            }
        }
        else
        {
            this._container.close();
            this._container = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), _containerFileName);
        }
    }

    public static ApplicationContext getCurrent()
    {
        if (_current == null)
        {
            _current = new ApplicationContext();
        }
        return _current;
    }

    public ObjectContainer getContainer()
    {
        return _container;
    }

    public void setKey(String key)
    {
        _preferences.put("Key", key);
    }

    public String getKey()
    {
        return _preferences.get("Key", "");
    }
    
    public int getClientId()
    {
        ObjectSet<PlayerDetails> result = _container.query().execute();
        return Integer.parseInt(result.get(result.size() - 1).getClienteID());
    }
    
    public Date getExpirationDate()
    {
        ObjectSet<PlayerDetails> result = _container.query().execute();
        String dataEncerramento = result.get(result.size() - 1).getDataEncerramento();
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dataEncerramento);
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}