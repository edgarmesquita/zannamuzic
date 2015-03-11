/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.domain.services;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import org.tempuri.AjaxServicesJava;
import org.tempuri.AjaxServicesJavaSoap;
import org.tempuri.ArrayOfMusica;
import org.tempuri.ArrayOfPlaylistMood;
import org.tempuri.ArrayOfVinheta;
import org.tempuri.Musica;
import org.tempuri.Playlist;
import org.tempuri.PlaylistMood;
import org.tempuri.Vinheta;
import znn.ApplicationContext;
import znn.models.Advertising;
import znn.models.Music;
import znn.models.PlayerCalibragem;
import znn.models.PlayerDetails;
import znn.ui.MainFrame;

/**
 *
 * @author Edgar
 */
public class PlayListService
{

    private String storeUri = "http://zannamuzicplay.net/";

    private String getFileName(int fileId)
    {
        return fileId + ".zm";
    }

    private String getFullFileName(int fileId)
    {
        String root = System.getProperty("user.dir");
        return root + "/data/musics/" + getFileName(fileId);
    }

    private String getFullAdvertisingFileName(int advertisingId)
    {
        String root = System.getProperty("user.dir");
        return root + "/data/advertising/" + getFileName(advertisingId);
    }

    private void downloadAdvertisingFile(int advertisingId) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        String fileName = getFileName(advertisingId);
        String fullFileName = getFullAdvertisingFileName(advertisingId);
        downloadFile(storeUri + "vinhetas/" + advertisingId + ".mp3", fullFileName);
    }

    private void downloadFile(int fileId) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        String fileName = getFileName(fileId);
        String fullFileName = getFullFileName(fileId);
        downloadFile(storeUri + "musicas/" + fileId + ".mp3", fullFileName);
    }

    private void downloadFile(String url, String destinationPath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        File file = new File(destinationPath);
        if (!file.exists())
        {

            URL link = new URL(url); //The file that you want to download
            
            //Code to download
            InputStream in = new BufferedInputStream(link.openStream());           
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream(destinationPath);
            fos.write(response);
            fos.close();
        }
    }

    public void updateMusics()
    {
        try
        {
            AjaxServicesJava service = new AjaxServicesJava();
            AjaxServicesJavaSoap soap = service.getAjaxServicesJavaSoap();
            final int clientId = ApplicationContext.getCurrent().getClientId();
            int playListId = soap.listarUltimaPlaylist(clientId);

            ArrayOfMusica musicCollection = soap.musicaListarPorPlaylist(playListId);
            final ArrayList<Musica> musics = (ArrayList<Musica>) musicCollection.getMusica();

            if (musics.size() > 0)
            {
                ArrayList<String> fileNamesToRemove = new ArrayList<String>();
                ObjectSet<Music> dbMusics = listAllMusics();
                //Buscar no banco músicas fora do intervalo do serviço
                for (Music dbMusic : dbMusics)
                {
                    boolean remove = true;
                    String fileName = dbMusic.getFileName();
                    for (Musica music : musics)
                    {
                        String fn = music.getMusicaID() + ".zm";
                        if (fn == dbMusic.getFileName())
                        {
                            remove = false;
                            break;
                        }
                    }
                    if (remove)
                    {
                        fileNamesToRemove.add(fileName);
                    }
                }

                //Removendo arquivos da base;
                for (String fileName : fileNamesToRemove)
                {
                    removeMusicByFileName(fileName);
                }

                //Adicionar músicas ao banco
                for (Musica music : musics)
                {

                    int fileId = music.getMusicaID();
                    String fileName = fileId + ".zm";
                    ObjectSet result = getContainer().queryByExample(new Music(fileName));

                    try
                    {
                        Music m = null;
                        if (result != null && result.hasNext())
                        {
                            m = (Music) result.next();
                        }
                        else
                        {
                            downloadFile(fileId);
                            m = new Music(fileName);
                        }
                        m.setMoodID(music.getMoodID());
                        m.setArtist(music.getArtista());
                        m.setMusicaID(Integer.toString(music.getMusicaID()));
                        m.setTitle(music.getTitulo());

                        getContainer().store(m);
                        getContainer().commit();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                ArrayOfPlaylistMood mood = soap.listarPorPlaylist(playListId);
                ArrayList<PlaylistMood> moodList = (ArrayList<PlaylistMood>) mood.getPlaylistMood();
                ObjectSet<PlayerCalibragem> dbCalibragens = getContainer().query(new Predicate<PlayerCalibragem>()
                {

                    @Override
                    public boolean match(PlayerCalibragem et)
                    {
                        return et.getMoodID() > 0;
                    }
                });
                for (PlayerCalibragem pc : dbCalibragens)
                {
                    getContainer().delete(pc);
                }
                getContainer().commit();

                if (moodList.size() > 0)
                {

                    for (PlaylistMood md : moodList)
                    {
                        SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                        PlayerCalibragem pc = new PlayerCalibragem();
                        pc.setMoodID(md.getMood().getMoodID());
                        pc.sethorainicio(fm.format(md.getHoraInicio().toGregorianCalendar().getTime()));
                        pc.sethorafim(fm.format(md.getHoraFim().toGregorianCalendar().getTime()));

                        getContainer().store(pc);
                    }
                    getContainer().commit();
                }
            }
        }
        catch (Exception ex)
        {
            System.out.printf(ex.getMessage());
        }
    }

    public void updateAdvertisings()
    {
        try
        {
            AjaxServicesJava service = new AjaxServicesJava();
            AjaxServicesJavaSoap soap = service.getAjaxServicesJavaSoap();
            final int clientId = ApplicationContext.getCurrent().getClientId();
            int playListId = soap.listarUltimaPlaylist(clientId);

            Playlist pl = soap.playlistObter(playListId);

            if (pl != null)
            {

                ArrayOfVinheta vinhetaCollection = pl.getVinhetas();
                ArrayList<Vinheta> vinhetas = (ArrayList<Vinheta>) vinhetaCollection.getVinheta();

                if (vinhetas.size() > 0)
                {

                    ArrayList<String> fileNamesToRemove = new ArrayList<String>();
                    ObjectSet<Advertising> dbMusics = listAllAdvertisings();

                    //Buscar no banco músicas fora do intervalo do serviço
                    for (Advertising dbMusic : dbMusics)
                    {
                        boolean remove = true;
                        String fileName = dbMusic.getFileName();
                        for (Vinheta music : vinhetas)
                        {
                            String fn = music.getVinhetaID() + ".zm";
                            if (fn == dbMusic.getFileName())
                            {
                                remove = false;
                                break;
                            }
                        }
                        if (remove)
                        {
                            fileNamesToRemove.add(fileName);
                        }
                    }

                    //Removendo arquivos da base;
                    for (String fileName : fileNamesToRemove)
                    {
                        removeAdvertisingByFileName(fileName);
                    }

                    //Adicionar vinhetas ao banco
                    for (Vinheta vinheta : vinhetas)
                    {

                        int advertisingId = vinheta.getVinhetaID();
                        String fileName = advertisingId + ".zm";
                        try
                        {
                            downloadAdvertisingFile(advertisingId);

                            Advertising a = new Advertising(fileName);
                            a.setSleepTime(vinheta.getIntervalo());

                            getContainer().store(a);
                            getContainer().commit();

                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public boolean updateExpirationDate()
    {
        AjaxServicesJava service = new AjaxServicesJava();
        AjaxServicesJavaSoap soap = service.getAjaxServicesJavaSoap();
        final int clientId = ApplicationContext.getCurrent().getClientId();
        int playListId = soap.listarUltimaPlaylist(clientId);

        ObjectContainer container = ApplicationContext.getCurrent().getContainer();
        ObjectSet<PlayerDetails> dbPlayerDetails = container.query(new Predicate<PlayerDetails>()
        {

            @Override
            public boolean match(PlayerDetails et)
            {
                return et.getClienteID().equals(Integer.toString(clientId));
            }
        });

        if (dbPlayerDetails.size() > 0)
        {
            PlayerDetails detail = dbPlayerDetails.get(0);

            if (detail != null)
            {
                SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
                Playlist pl = soap.playlistObter(playListId);

                detail.setDataEncerramento(fm.format(pl.getDataEncerramento().toGregorianCalendar().getTime()));
                container.store(detail);
                container.commit();
                return true;
            }
        }
        return false;
    }

    public ObjectSet<Music> listAllMusics()
    {
        ObjectSet<Music> list = getContainer().query(new Predicate<Music>()
        {

            @Override
            public boolean match(Music et)
            {
                return !et.getFileName().isEmpty();
            }
        });

        return list;
    }

    public ObjectSet<Advertising> listAllAdvertisings()
    {
        ObjectSet<Advertising> list = getContainer().query(new Predicate<Advertising>()
        {

            @Override
            public boolean match(Advertising et)
            {
                return !et.getFileName().isEmpty();
            }
        });

        return list;
    }

    public void removeMusicByFileName(String fileName)
    {
        ObjectSet result = getContainer().queryByExample(new Music(fileName));

        if (result != null && result.hasNext())
        {
            Music m = (Music) result.next();
            getContainer().delete(m);
            getContainer().commit();
        }
    }

    public void removeAdvertisingByFileName(String fileName)
    {
        ObjectSet result = getContainer().queryByExample(new Advertising(fileName));

        if (result != null && result.hasNext())
        {
            Advertising m = (Advertising) result.next();
            getContainer().delete(m);
            getContainer().commit();
        }
    }

    private ObjectContainer getContainer()
    {
        return ApplicationContext.getCurrent().getContainer();
    }
}
