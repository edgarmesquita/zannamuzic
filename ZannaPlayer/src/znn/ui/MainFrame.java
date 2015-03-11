package znn.ui;

import com.apple.eawt.Application;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javazoom.jl.decoder.JavaLayerException;
import org.apache.commons.lang3.SystemUtils;
import org.tempuri.AjaxServicesJava;
import org.tempuri.AjaxServicesJavaSoap;
import org.tempuri.ArrayOfMusica;
import org.tempuri.ArrayOfPlaylistMood;
import org.tempuri.ArrayOfVinheta;
import org.tempuri.Musica;
import org.tempuri.Playlist;
import org.tempuri.PlaylistMood;
import org.tempuri.Vinheta;
import znn.AdvertisingList;
import znn.ApplicationContext;
import znn.MediaList;
import znn.PlayList;
import znn.PlayListItem;
import znn.domain.services.PlayListService;
import znn.models.Advertising;
import znn.models.Music;
import znn.models.PlayerCalibragem;
import znn.models.PlayerDetails;
import znn.security.*;

import znn.ui.components.*;
//import ding.view.DingCanvas;

/**
 *
 * @author Edgar Mesquita
 */
public class MainFrame extends javax.swing.JFrame
{

    private Font globalFont;
    private Font vistaSansLight;
    private Font vistaSansAltLight;
    private Font vistaSansAltBold;
    private Font vistaSansAltMedIta;

    private final Timer animationTimer = new Timer(1, null);
    private final Timer timer = new Timer(1, null);
    private final Timer updateTimer = new Timer(1000 * 60 * 60 * 3, null);

    private int coefficient;
    private static MainFrame instance;
    private PlayList playList;
    private PlayerDetails playerDetails;
    private PlayerCalibragem PlayerCalibragem;
    private AdvertisingList _advertisingList;
    private int _advertisingDuration = 0;
    private final SecureRandom random = new SecureRandom();

    static Point mouseDownCompCoords;
    private String password;
    private String token;
    private final PlayListService playListService;
    
    public static MainFrame getInstance()
    {
        return instance;
    }

    public PlayList getPlayList()
    {
        return this.playList;
    }

    public PlayerDetails getPlayerDetails()
    {
        return this.playerDetails;
    }

    public PlayerCalibragem getPlayListMood()
    {
        return this.PlayerCalibragem;
    }

    /**
     * Creates new form MainJFrame
     * @param service
     */
    public MainFrame(PlayListService service)
    {
        playListService = service;
        
        beforeInitComponents();
        initComponents();
        afterInitComponents();
    }

    public class MyScrollbarUI extends MetalScrollBarUI
    {

        private Image imageThumb, imageTrack;

        MyScrollbarUI()
        {
            try
            {

                imageThumb = ImageIO.read(getClass().getResource("/znn/resources/scroll.png"));
                imageTrack = ImageIO.read(getClass().getResource("/znn/resources/barra.png"));
            }
            catch (IOException ex)
            {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return createButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return createButton();
        }

        private JButton createButton()
        {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            g.translate(thumbBounds.x, thumbBounds.y);
            //g.setColor( Color.decode("ffffff") );
            ////g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );
            AffineTransform transform = AffineTransform.getScaleInstance((double) thumbBounds.width / imageThumb.getWidth(null), (double) thumbBounds.height / imageThumb.getHeight(null));
            ((Graphics2D) g).drawImage(imageThumb, transform, null);
            g.translate(-thumbBounds.x, -thumbBounds.y);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
        {
            g.translate(trackBounds.x, trackBounds.y);
            c.setBackground(new Color(216, 216, 206, 216));

            ((Graphics2D) g).drawImage(imageTrack, AffineTransform.getScaleInstance(1, (double) trackBounds.height / imageTrack.getHeight(null)), null);
            g.translate(-trackBounds.x, -trackBounds.y);
        }

    }
    /*static class MyScrollbarUI extends MetalScrollBarUI {

     private Image imageThumb, imageTrack;
     private JButton b = new JButton() {

     @Override
     public Dimension getPreferredSize() {
     return new Dimension(0, 0);
     }

     };

     MyScrollbarUI() {
     imageThumb = FauxImage.create(32, 32, Color.blue.darker());
     imageTrack = FauxImage.create(32, 32, Color.lightGray);
     }

     @Override
     protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
     g.setColor(Color.blue);
     ((Graphics2D) g).drawImage(imageThumb,
     r.x, r.y, r.width, r.height, null);
     }

     @Override
     protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
     ((Graphics2D) g).drawImage(imageTrack,
     r.x, r.y, r.width, r.height, null);
     }

     @Override
     protected JButton createDecreaseButton(int orientation) {
     return b;
     }

     @Override
     protected JButton createIncreaseButton(int orientation) {
     return b;
     }
     }

     private static class FauxImage {

     static public Image create(int w, int h, Color c) {
     BufferedImage bi = new BufferedImage(
     w, h, BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2d = bi.createGraphics();
     g2d.setPaint(c);
     g2d.fillRect(0, 0, w, h);
     g2d.dispose();
     return bi;
     }
     }*/

    private void beforeInitComponents()
    {
        instance = this;
        try
        {

            if (SystemUtils.IS_OS_MAC_OSX)
            {
                Application application = Application.getApplication();
                Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/znn/resources/docmac.png"));
                application.setDockIconImage(image);
            }
            //jslListContainer.getVerticalScrollBar();
            globalFont = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanReg.ttf"));
            vistaSansLight = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanLig.ttf"));
            vistaSansAltLight = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanAltLig.ttf"));
            vistaSansAltBold = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanAltBol.ttf"));
            vistaSansAltMedIta = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanAltMedIta.ttf"));
        }
        catch (FontFormatException | IOException ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);

            globalFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
        }
        setUndecorated(true);

    }

    private static enum ZOrder
    {

        BACKGROUND_PANE,
        NETWORK_PANE,
        FOREGROUND_PANE;

        int layer()
        {
            if (this == BACKGROUND_PANE)
            {
                return 10;
            }

            if (this == NETWORK_PANE)
            {
                return 20;
            }

            if (this == FOREGROUND_PANE)
            {
                return 30;
            }

            return 0;
        }
    }

    private void highlightRow()
    {

        int index = playList.getCurrentItem().getIndex();
        PlayListModel playListModel = PlayListModel.getInstance();
        if (playListModel != null)
        {
            PlayListRowPanel row = playListModel.getRow(index);

            row.setForeground(new Color(54, 143, 94));
            //row.setBackground(new Color(172, 223, 196));
            //row.setOpaque(true);

            int rowsSize = PlayListModel.getInstance().getAllRows().size();
            for (int i = 0; i < rowsSize; i++)
            {
                if (i != index)
                {
                    PlayListRowPanel r = PlayListModel.getInstance().getRow(i);
                    r.resetForeground();
                    //r.setBackground(new Color(0, 0, 0, 0));
                    //r.setOpaque(false);
                }
            }

            playListModel.updateUI();
        }
    }

    private boolean isHighlighted = false;

    private void updateMusicListFromService()
    {
        if (isConexaoFromWEB())
        {
            playListService.updateExpirationDate();
            this.updateExpirationDate();
            
            playListService.updateAdvertisings();
            playListService.updateMusics();
        }
    }

    private void updateExpirationDate()
    {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        jlblDate.setText("Expira em: " + dtf.format(ApplicationContext.getCurrent().getExpirationDate()));
    }
    /**
     * ref to background canvas
     */
//	private backgroundCanvas backgroundCanvas;
    /**
     * ref to network canvas
     */
    //private DingCanvas networkCanvas;
    /**
     * ref to foreground canvas
     */
    //private DingCanvas foregroundCanvas;
    private void afterInitComponents()
    {
        //playList.play();
        //jlpMain.setOpaque(true);
        //jlpMain.setBackground( new Color(0,0,0,0));

        jpListContent.setSize(300, jpListContent.getSize().height);
        this.setSize(317, this.getSize().height);
        this.centralize();

        updateExpirationDate();

        updateTimer.setRepeats(true);
        updateTimer.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {

                Thread t = new Thread(new Runnable()
                {

                    public void run()
                    {
                        updateMusicListFromService();
                    }

                });
                t.start();
            }

        });
        updateTimer.start();
        Thread t = new Thread(new Runnable()
        {

            public void run()
            {
                updateMusicListFromService();
            }

        });
        t.start();

        timer.setRepeats(true);
        timer.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PieChartPanel pieChart = (PieChartPanel) jpTime;
                PieChartPanel pieChartRemaning = (PieChartPanel) jpTime1;

                if (_advertisingList != null
                        && _advertisingList.getStatus() == MediaList.FINISHED)
                {
                    try
                    {
                        _advertisingList.reset();
                    }
                    catch (FileNotFoundException | JavaLayerException ex)
                    {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    _advertisingDuration = 0;
                    enableControls(true);
                }
                switch (playList.getStatus())
                {
                    case MediaList.PLAYING:
                        jlblTimer.setText(playList.getCurrentTime());

                        pieChart.setPercent(playList.getCurrentPercent());
                        pieChartRemaning.setPercent(playList.getLatestPercent() == 0 ? 0 : 100 - playList.getLatestPercent());
                        //PlayListModel listModel = (PlayListModel)jslListContainer.getViewport().getView();

                        if (!isHighlighted)
                        {
                            highlightRow();
                            isHighlighted = true;
                        }
                        //jslListContainer.
                        //playList.
                        break;
                    case MediaList.FINISHING:
                        isHighlighted = false;
                        pieChart.setPercent(100);
                        pieChartRemaning.setPercent(0);
                        int total = playList.getTotal() - 1;

                        ObjectContainer container = ApplicationContext.getCurrent().getContainer();
                        int mood = playList.getCurrentItem().getMood();
                        int contmoodFora = 0;
                        boolean foradointervalo = false;
                        PlayerCalibragem plc = new PlayerCalibragem();
                        //new ArrayList<PlayListItem>();
                        ObjectSet result = container.queryByExample(plc);
                        ArrayList<PlayerCalibragem> listCalibre = new ArrayList<PlayerCalibragem>();
                        if (result != null && result.hasNext())
                        {
                            for (int i = 0; i < result.size(); i++)
                            {

                                plc = (PlayerCalibragem) result.get(i);
                                //String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

                                try
                                {
                                    Date datainicio = new SimpleDateFormat("HH:mm").parse(plc.gethorainicio());
                                    Date datafim = new SimpleDateFormat("HH:mm").parse(plc.gethorafim());
                                    //DateFormat df = new SimpleDateFormat("HH:mm:ss");
                                    //Date dt = new Date();

                                    String dthr = new SimpleDateFormat("HH:mm").format(new java.util.Date());
                                    Date dt = new SimpleDateFormat("HH:mm").parse(dthr);

                                    Timestamp timestamp = new Timestamp(datainicio.getTime());
                                    Timestamp timestampfim = new Timestamp(datafim.getTime());
                                    Timestamp timestampatual = new Timestamp(dt.getTime());

                                    if (timestampatual.after(timestamp) && timestampatual.before(timestampfim))
                                    {

                                        //plc.setMoodID(mood);
                                        //plc.sethorafim(token);
                                        if (plc.getMoodID() != mood)
                                        {
                                            contmoodFora += 1;
                                            //initPlayList(null);  
                                        }

                                        listCalibre.add(plc);
                                    }

                                }
                                catch (ParseException ex)
                                {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            if (contmoodFora == listCalibre.size() && listCalibre.size() > 0)
                            {
                                foradointervalo = true;
                            }
                            if (listCalibre.size() > 0 && foradointervalo)
                            {
                                initPlayList(listCalibre);
                            }

                        }
                        if (!foradointervalo)
                        {
                            if (playList.getCurrentItem().getFile().getName().equals(playList.getItemsFinal().get(total).getFile().getName()))
                            {
                                //try
                                //{
                                initPlayList(listCalibre);
                                //updateInfo();
                                //playList.play();
                                //}
                                //catch (ClassNotFoundException ex)
                                //{
                                //Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                //}
                            }
                            else
                            {
                                try
                                {
                                    //Propaganda
                                    if (_advertisingList != null
                                            && _advertisingList.getCurrentItem() != null
                                            && _advertisingDuration >= _advertisingList.getCurrentItem().getSleepTime())
                                    {
                                        _advertisingList.play();
                                        //resetInfo();
                                        updateInfoadvertising();
                                        enableControls(false);
                                    }
                                    else // Próxima Música
                                    {
                                        playList.next();
                                        updateInfo();
                                        _advertisingDuration += playList.getCurrentItem().getDuration();
                                    }
                                }
                                catch (FileNotFoundException | JavaLayerException ex)
                                {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        break;
                }
            }
        });

        animationTimer.setRepeats(true);
        animationTimer.addActionListener(new ActionListener()
        {
            float minValue = 317;
            float maxValue = 820;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //update MainFrame
                instance.setSize(instance.getSize().width + coefficient, instance.getSize().height);
                instance.setLocation(instance.getX() + ((coefficient / 2) * -1), instance.getY());
                //centering MainFrame
                //instance.centralize();

                //update PlayList
                //jpListContent.setSize(jpListContent.getSize().width + coefficient, jpListContent.getSize().height);
                //jpListContent.updateUI();
                jpListContent.setSize(instance.getSize().width + coefficient, instance.getSize().height);
                if ((coefficient < 0 && jpListContent.getSize().width <= minValue) || coefficient > 0 && jpListContent.getSize().width >= maxValue)
                {
                    instance.setSize(instance.getSize().width + coefficient, instance.getSize().height);
                    jpListContent.setSize(instance.getSize().width + coefficient, instance.getSize().height);
                    animationTimer.stop();
                }
            }
        });
        try
        {
            if (validateKey())
            {

                ObjectContainer container = ApplicationContext.getCurrent().getContainer();
                int mood = 0;
                boolean foradointervalo = false;
                PlayerCalibragem plc = new PlayerCalibragem();
                //new ArrayList<PlayListItem>();
                ObjectSet result = container.queryByExample(plc);
                ArrayList<PlayerCalibragem> listCalibre = new ArrayList<PlayerCalibragem>();
                if (result != null && result.hasNext())
                {
                    for (int i = 0; i < result.size(); i++)
                    {

                        plc = (PlayerCalibragem) result.get(i);
                        //String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

                        try
                        {
                            Date datainicio = new SimpleDateFormat("HH:mm").parse(plc.gethorainicio());
                            Date datafim = new SimpleDateFormat("HH:mm").parse(plc.gethorafim());
                            //DateFormat df = new SimpleDateFormat("HH:mm:ss");
                            //Date dt = new Date();
                            GregorianCalendar teste = new GregorianCalendar();
                            SimpleDateFormat teste2 = new SimpleDateFormat("HH:mm");

                            String dthr = new SimpleDateFormat("HH:mm").format(new java.util.Date());
                            Date dt = new SimpleDateFormat("HH:mm").parse(dthr);

                            Timestamp timestamp = new Timestamp(datainicio.getTime());
                            Timestamp timestampfim = new Timestamp(datafim.getTime());
                            Timestamp timestampatual = new Timestamp(dt.getTime());

                            if (timestampatual.after(timestamp) && timestampatual.before(timestampfim))
                            {

                                foradointervalo = true;
                                //initPlayList(plc.getMoodID());
                                listCalibre.add(plc);
                            }

                        }
                        catch (ParseException ex)
                        {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //plc = (PlayerCalibragem)result.next();
                    //String timefim =  plc.gethorafim();
                    // String timeinicio =  plc.gethorainicio();
                }

                if (!foradointervalo)
                {
                    initPlayList(listCalibre);
                }
                else
                {
                    initPlayList(listCalibre);
                }
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String LogAtivacao(String ClienteID)
    {
        AjaxServicesJava service = new AjaxServicesJava();

        //try {
        String result = service.getAjaxServicesJavaSoap().playlistAtivacao(Integer.parseInt(ClienteID), Hardware.getSerialNumber());
        return result;
        //} catch (Exception ex) {
        //    return null;
        //}
    }

    public String MusicaAvaliacao(String MusicaID, String ClienteID, short Rate)
    {
        AjaxServicesJava service = new AjaxServicesJava();
        String result = service.getAjaxServicesJavaSoap().musicaAvaliacaoOffline(Integer.parseInt(MusicaID), Integer.parseInt(ClienteID), Rate);
        return result;
    }

    public String nextSessionId()
    {
        return new BigInteger(130, random).toString(32);
    }

    public boolean isConexaoFromWEB()
    {
        try
        {
            java.net.URL mandarMail = new java.net.URL("http://www.guj.com.br");
            java.net.URLConnection conn = mandarMail.openConnection();

            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) conn;
            httpConn.connect();
            int x = httpConn.getResponseCode();
            return true;

        }
        catch (Exception e)
        {
            return false;
        }
    }

    private boolean validateKey()
    {

        String num = nextSessionId();
        if (num.length() > 10)
        {
            num = num.substring(0, 9);
        }

        password = "ZannaMuzicPlayerHay" + Hardware.getSerialNumber();

        token = Hardware.getSerialNumber();
        String encPassword = Encryption.encrypt(password);

        //busca da de encerramento
        ObjectContainer container = ApplicationContext.getCurrent().getContainer();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String dataAtual = dateFormat.format(date);
        ObjectSet<PlayerDetails> result = container.query().execute();
        getPlayerDetails();
        String dataEncerramento = result.get(result.size() - 1).getDataEncerramento();
        String Cliente = result.get(result.size() - 1).getCliente();
        jLabel1.setText(" " + Cliente);

        String ClienteID = result.get(result.size() - 1).getClienteID();
        if (isConexaoFromWEB())
        {
            LogAtivacao(ClienteID);
        }

        // Bloqueia Data de Encerramento
        Date dateencerr = null;
        Date dateini = null;
        try
        {
            dateini = new SimpleDateFormat("dd/MM/yyyy").parse(dataAtual);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            dateencerr = new SimpleDateFormat("dd/MM/yyyy").parse(dataEncerramento);
        }
        catch (ParseException ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (dateini.after(dateencerr))
        {
            enableControls(false);
            resetInfo();
            JOptionPane.showMessageDialog(this, "Esta cópia expirou. Por favor baixe a nova versão no site.");
            return false;
        }

        if (ApplicationContext.getCurrent().getKey().isEmpty())
        {
            System.out.println(token);
            //criarArquivoComChave(encPassword);

            String encrypted = (String) JOptionPane.showInputDialog(this, "Esta cópia não foi validada.\nInforme o Token:" + token + " para a Zanna Muzic que forneceremos sua chave.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);

            String pass = encrypted;

            if (pass.equals(Hash.MD5.diggest(password)))
            {
                ApplicationContext.getCurrent().setKey(Hash.MD5.diggest(password));
            }
            else
            {
                return false;
            }
        }
        else if (!ApplicationContext.getCurrent().getKey().equals(Hash.MD5.diggest(password)))
        {
            String chave = Hash.MD5.diggest(password);

            String encrypted = (String) JOptionPane.showInputDialog(this, "Esta cópia já foi validada em outra máquina.\nInforme o Token:" + token + " para a Zanna Muzic que forneceremos sua chave.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);

            String pass = encrypted;

            if (pass.equals(Hash.MD5.diggest(password)))
            {
                ApplicationContext.getCurrent().setKey(Hash.MD5.diggest(password));
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public enum Hash
    {

        MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256");

        private String algo;

        private Hash(String algo)
        {
            this.algo = algo;
        }

        public byte[] diggest(byte[] dados)
        {
            try
            {
                MessageDigest algorithm = MessageDigest.getInstance(algo);
                return algorithm.digest(dados);
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new RuntimeException("Unable to generate password!", e);
            }
        }

        public String diggest(String valor)
        {
            return byteArrayToString(diggest(stringToByteArray(valor)));
        }

        private String byteArrayToString(byte[] byteArray)
        {
            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray)
            {
                int value = b & 0xFF;
                if (value < 16)
                {
                    sb.append("0");
                }
                sb.append(Integer.toString(value, 16));
            }
            return sb.toString();
        }

        private byte[] stringToByteArray(String valor)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (DataOutputStream dos = new DataOutputStream(bos))
            {
                dos.writeUTF(valor);
            }
            catch (Exception e)
            {
                assert (false); // Nunca ocorre com ByteArrays  
            }
            return bos.toByteArray();
        }
    }

    private void criarArquivoComChave(String chave)
    {
        try
        {
            // Gravando no arquivo  
            File arquivo;

            arquivo = new File("chave.txt");
            FileOutputStream fos = new FileOutputStream(arquivo);
            String texto = "Sua chave de Acesso: " + chave;
            fos.write(texto.getBytes());
            fos.close();
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    private void initPlayList(ArrayList<PlayerCalibragem> listCalibre)
    {
        try
        {
            playList = new PlayList(listCalibre);

            _advertisingList = new AdvertisingList();

            playList.play();
            updateInfo();

            _advertisingDuration += playList.getCurrentItem().getDuration();
            timer.start();
            //playList.play();
            // updateInfo();
            //  _advertisingDuration += playList.getCurrentItem().getDuration();

        }
        catch (JavaLayerException | FileNotFoundException ex)
        {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (playList != null)
        {

            PlayListModel viewportContent = new PlayListModel();

            JViewport viewport = new JViewport();
            viewport.setView(viewportContent);
            viewport.setOpaque(false);
            viewport.setBorder(null);
            viewport.getInsets().set(0, 0, 0, 0);
            jslListContainer.setViewportBorder(null);
            jslListContainer.setViewport(viewport);
            jslListContainer.getViewport().setOpaque(false);
            jslListContainer.setOpaque(false);

            int i = 0;
            for (PlayListItem item : playList.getItems())
            {
                PlayListRowPanel row = new PlayListRowPanel(item);
                viewportContent.addRow(row);
                i++;
            }

            JScrollPane after = jslListContainer;
            JScrollBar sb = after.getVerticalScrollBar();

            try
            {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
            catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex)
            {

            }

            UIManager.put("ScrollBarUI", MyScrollbarUI.class.getName());
            sb.setPreferredSize(new Dimension(13, Integer.MAX_VALUE));
            sb.setUI(new MyScrollbarUI()
            {
            });

            //add(after);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jlpMain = new javax.swing.JLayeredPane();
        jpPlayer = new ImagePanel("znn/resources/background.png");
        jlpLogoContent = new javax.swing.JLayeredPane();
        jpLogo = new ImagePanel("znn/resources/logo.png");
        jpTime = new PieChartPanel(0, new Color(54, 143, 94));
        jpTime1 = new PieChartPanel(0, new Color(255, 218, 47), true);
        jpLogoBackground = new ImagePanel("znn/resources/logoBackground.png");
        jlpControlsContent = new javax.swing.JLayeredPane();
        jlpRating = new javax.swing.JLayeredPane();
        jpRate5 = new ImagePanel("znn/resources/heartDisabled.png");
        jpRate4 = new ImagePanel("znn/resources/heartDisabled.png");
        jpRate3 = new ImagePanel("znn/resources/heartDisabled.png");
        jpRate2 = new ImagePanel("znn/resources/heartDisabled.png");
        jpRate1 = new ImagePanel("znn/resources/heartDisabled.png");
        jlblTitle = new javax.swing.JLabel();
        jlblTimer = new javax.swing.JLabel();
        jlblArtirst = new javax.swing.JLabel();
        jbClose = new javax.swing.JButton();
        jbClose1 = new javax.swing.JButton();
        jbtPlayList = new javax.swing.JButton();
        jbtHight = new javax.swing.JButton();
        jbtLower = new javax.swing.JButton();
        jbtFoward = new javax.swing.JButton();
        jbtBack = new javax.swing.JButton();
        jbtPlay = new javax.swing.JButton();
        jpControlsBackground = new ImagePanel("znn/resources/controlsBackground.png");
        jSlider1 = new javax.swing.JSlider();
        jlblDate = new javax.swing.JLabel();
        jpListContent = new javax.swing.JPanel();
        jpList = new ImagePanel("znn/resources/listBackground.png");
        jLabel1 = new znn.ui.components.RichLabel();
        ((znn.ui.components.RichLabel)jLabel1).setRightShadow(1, 1, Color.WHITE);
        jslListContainer = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zanna Sound Player");
        setBackground(new Color(0, 255, 0, 0));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/znn/resources/face.png")));
        addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                formMouseReleased(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentMoved(java.awt.event.ComponentEvent evt)
            {
                formComponentMoved(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseDragged(java.awt.event.MouseEvent evt)
            {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                formMouseMoved(evt);
            }
        });

        jlpMain.setBackground(new Color(0, 255, 0, 0));
        jlpMain.setName(""); // NOI18N

        jpPlayer.setBackground(new java.awt.Color(255, 255, 255));
        jpPlayer.setMaximumSize(new java.awt.Dimension(317, 407));
        jpPlayer.setMinimumSize(new java.awt.Dimension(317, 407));
        jpPlayer.setPreferredSize(new java.awt.Dimension(317, 407));

        jlpLogoContent.setBackground(new Color(0, 255,0,0));
        jlpLogoContent.setPreferredSize(new java.awt.Dimension(190, 176));

        jpLogo.setBackground(new java.awt.Color(153, 255, 153));
        jpLogo.setPreferredSize(new java.awt.Dimension(167, 150));

        javax.swing.GroupLayout jpLogoLayout = new javax.swing.GroupLayout(jpLogo);
        jpLogo.setLayout(jpLogoLayout);
        jpLogoLayout.setHorizontalGroup(
            jpLogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 167, Short.MAX_VALUE)
        );
        jpLogoLayout.setVerticalGroup(
            jpLogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        jlpLogoContent.add(jpLogo);
        jpLogo.setBounds(13, 12, 167, 150);

        jpTime.setPreferredSize(new java.awt.Dimension(172, 172));

        javax.swing.GroupLayout jpTimeLayout = new javax.swing.GroupLayout(jpTime);
        jpTime.setLayout(jpTimeLayout);
        jpTimeLayout.setHorizontalGroup(
            jpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        jpTimeLayout.setVerticalGroup(
            jpTimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );

        jlpLogoContent.add(jpTime);
        jpTime.setBounds(2, 0, 172, 172);

        jpTime1.setPreferredSize(new java.awt.Dimension(172, 172));

        javax.swing.GroupLayout jpTime1Layout = new javax.swing.GroupLayout(jpTime1);
        jpTime1.setLayout(jpTime1Layout);
        jpTime1Layout.setHorizontalGroup(
            jpTime1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        jpTime1Layout.setVerticalGroup(
            jpTime1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );

        jlpLogoContent.add(jpTime1);
        jpTime1.setBounds(2, 0, 172, 172);

        jpLogoBackground.setBackground(new java.awt.Color(204, 255, 204));
        jpLogoBackground.setPreferredSize(new java.awt.Dimension(176, 176));

        javax.swing.GroupLayout jpLogoBackgroundLayout = new javax.swing.GroupLayout(jpLogoBackground);
        jpLogoBackground.setLayout(jpLogoBackgroundLayout);
        jpLogoBackgroundLayout.setHorizontalGroup(
            jpLogoBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );
        jpLogoBackgroundLayout.setVerticalGroup(
            jpLogoBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );

        jlpLogoContent.add(jpLogoBackground);
        jpLogoBackground.setBounds(0, 0, 176, 176);

        jlpControlsContent.setPreferredSize(new java.awt.Dimension(277, 47));

        jlpRating.setPreferredSize(new java.awt.Dimension(18, 100));

        jpRate5.setBackground(new java.awt.Color(255, 0, 0));
        jpRate5.setName("jpRate5"); // NOI18N
        jpRate5.setPreferredSize(new java.awt.Dimension(18, 17));
        jpRate5.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRate5MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRate5MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRate5MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jpRate5Layout = new javax.swing.GroupLayout(jpRate5);
        jpRate5.setLayout(jpRate5Layout);
        jpRate5Layout.setHorizontalGroup(
            jpRate5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        jpRate5Layout.setVerticalGroup(
            jpRate5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        jlpRating.add(jpRate5);
        jpRate5.setBounds(0, 56, 18, 17);

        jpRate4.setBackground(new java.awt.Color(255, 51, 51));
        jpRate4.setName("jpRate4"); // NOI18N
        jpRate4.setPreferredSize(new java.awt.Dimension(18, 17));
        jpRate4.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRate4MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRate4MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRate4MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jpRate4Layout = new javax.swing.GroupLayout(jpRate4);
        jpRate4.setLayout(jpRate4Layout);
        jpRate4Layout.setHorizontalGroup(
            jpRate4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        jpRate4Layout.setVerticalGroup(
            jpRate4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        jlpRating.add(jpRate4);
        jpRate4.setBounds(0, 42, 18, 17);

        jpRate3.setBackground(new java.awt.Color(255, 102, 102));
        jpRate3.setName("jpRate3"); // NOI18N
        jpRate3.setPreferredSize(new java.awt.Dimension(18, 17));
        jpRate3.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRate3MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRate3MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRate3MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jpRate3Layout = new javax.swing.GroupLayout(jpRate3);
        jpRate3.setLayout(jpRate3Layout);
        jpRate3Layout.setHorizontalGroup(
            jpRate3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        jpRate3Layout.setVerticalGroup(
            jpRate3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        jlpRating.add(jpRate3);
        jpRate3.setBounds(0, 28, 18, 17);

        jpRate2.setBackground(new java.awt.Color(255, 153, 153));
        jpRate2.setName("jpRate2"); // NOI18N
        jpRate2.setPreferredSize(new java.awt.Dimension(18, 17));
        jpRate2.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRate2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRate2MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRate2MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jpRate2Layout = new javax.swing.GroupLayout(jpRate2);
        jpRate2.setLayout(jpRate2Layout);
        jpRate2Layout.setHorizontalGroup(
            jpRate2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        jpRate2Layout.setVerticalGroup(
            jpRate2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        jlpRating.add(jpRate2);
        jpRate2.setBounds(0, 14, 18, 17);

        jpRate1.setBackground(new java.awt.Color(255, 204, 204));
        jpRate1.setName("jpRate1"); // NOI18N
        jpRate1.setPreferredSize(new java.awt.Dimension(18, 17));
        jpRate1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRate1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRate1MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRate1MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jpRate1Layout = new javax.swing.GroupLayout(jpRate1);
        jpRate1.setLayout(jpRate1Layout);
        jpRate1Layout.setHorizontalGroup(
            jpRate1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        jpRate1Layout.setVerticalGroup(
            jpRate1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        jlpRating.add(jpRate1);
        jpRate1.setBounds(0, 0, 18, 17);

        jlblTitle.setFont(vistaSansAltBold.deriveFont(Font.PLAIN, 15));
        jlblTitle.setForeground(new java.awt.Color(52, 52, 51));
        jlblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblTitle.setText("--");

        jlblTimer.setFont(vistaSansAltLight.deriveFont(Font.PLAIN, 40));
        jlblTimer.setForeground(new java.awt.Color(52, 52, 51));
        jlblTimer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblTimer.setText("00:00");
        jlblTimer.setMaximumSize(new java.awt.Dimension(277, 65));
        jlblTimer.setPreferredSize(new java.awt.Dimension(277, 65));

        jlblArtirst.setFont(vistaSansAltMedIta.deriveFont(Font.PLAIN, 13));
        jlblArtirst.setForeground(new java.awt.Color(95, 96, 92));
        jlblArtirst.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblArtirst.setText("--");

        jbClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/cross.png"))); // NOI18N
        jbClose.setBorderPainted(false);
        jbClose.setContentAreaFilled(false);
        jbClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbClose.setMaximumSize(new java.awt.Dimension(24, 24));
        jbClose.setMinimumSize(new java.awt.Dimension(24, 24));
        jbClose.setPreferredSize(new java.awt.Dimension(24, 24));
        jbClose.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbCloseActionPerformed(evt);
            }
        });

        jbClose1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/minimize.png"))); // NOI18N
        jbClose1.setBorderPainted(false);
        jbClose1.setContentAreaFilled(false);
        jbClose1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbClose1.setMaximumSize(new java.awt.Dimension(24, 24));
        jbClose1.setMinimumSize(new java.awt.Dimension(24, 24));
        jbClose1.setPreferredSize(new java.awt.Dimension(24, 24));
        jbClose1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbClose1ActionPerformed(evt);
            }
        });

        jbtPlayList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/manageDisabled.png"))); // NOI18N
        jbtPlayList.setBorder(null);
        jbtPlayList.setBorderPainted(false);
        jbtPlayList.setContentAreaFilled(false);
        jbtPlayList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtPlayList.setFocusPainted(false);
        jbtPlayList.setPreferredSize(new java.awt.Dimension(17, 18));
        jbtPlayList.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtPlayListActionPerformed(evt);
            }
        });

        jbtHight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/hight.png"))); // NOI18N
        jbtHight.setBorder(null);
        jbtHight.setBorderPainted(false);
        jbtHight.setContentAreaFilled(false);
        jbtHight.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtHight.setFocusPainted(false);
        jbtHight.setPreferredSize(new java.awt.Dimension(17, 18));
        jbtHight.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtHightActionPerformed(evt);
            }
        });

        jbtLower.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/low.png"))); // NOI18N
        jbtLower.setBorder(null);
        jbtLower.setBorderPainted(false);
        jbtLower.setContentAreaFilled(false);
        jbtLower.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtLower.setFocusPainted(false);
        jbtLower.setPreferredSize(new java.awt.Dimension(17, 18));
        jbtLower.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtLowerActionPerformed(evt);
            }
        });

        jbtFoward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/foward.png"))); // NOI18N
        jbtFoward.setBorder(null);
        jbtFoward.setBorderPainted(false);
        jbtFoward.setContentAreaFilled(false);
        jbtFoward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtFoward.setFocusPainted(false);
        jbtFoward.setPreferredSize(new java.awt.Dimension(22, 23));
        jbtFoward.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtFowardActionPerformed(evt);
            }
        });

        jbtBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/back.png"))); // NOI18N
        jbtBack.setBorder(null);
        jbtBack.setBorderPainted(false);
        jbtBack.setContentAreaFilled(false);
        jbtBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtBack.setFocusPainted(false);
        jbtBack.setPreferredSize(new java.awt.Dimension(22, 23));
        jbtBack.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtBackActionPerformed(evt);
            }
        });

        jbtPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/play.png"))); // NOI18N
        jbtPlay.setBorder(null);
        jbtPlay.setBorderPainted(false);
        jbtPlay.setContentAreaFilled(false);
        jbtPlay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtPlay.setFocusPainted(false);
        jbtPlay.setPreferredSize(new java.awt.Dimension(26, 27));
        jbtPlay.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jbtPlayActionPerformed(evt);
            }
        });

        jpControlsBackground.setPreferredSize(new java.awt.Dimension(277, 47));

        jSlider1.setMajorTickSpacing(100);
        jSlider1.setValue(90);
        jSlider1.setAutoscrolls(true);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jSlider1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jpControlsBackgroundLayout = new javax.swing.GroupLayout(jpControlsBackground);
        jpControlsBackground.setLayout(jpControlsBackgroundLayout);
        jpControlsBackgroundLayout.setHorizontalGroup(
            jpControlsBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpControlsBackgroundLayout.createSequentialGroup()
                .addContainerGap(133, Short.MAX_VALUE)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );
        jpControlsBackgroundLayout.setVerticalGroup(
            jpControlsBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpControlsBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jlblDate.setFont(vistaSansAltMedIta.deriveFont(Font.PLAIN, 12));
        jlblDate.setForeground(new java.awt.Color(95, 96, 92));
        jlblDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jpPlayerLayout = new javax.swing.GroupLayout(jpPlayer);
        jpPlayer.setLayout(jpPlayerLayout);
        jpPlayerLayout.setHorizontalGroup(
            jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPlayerLayout.createSequentialGroup()
                .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPlayerLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jbClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlblArtirst, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jpPlayerLayout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addComponent(jlpLogoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPlayerLayout.createSequentialGroup()
                                    .addComponent(jbClose, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jlblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(29, 29, 29))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlpRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPlayerLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpPlayerLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jbtBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jbtPlay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jbtFoward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(jbtLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(jbtHight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(jbtPlayList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jpControlsBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlpControlsContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPlayerLayout.setVerticalGroup(
            jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPlayerLayout.createSequentialGroup()
                .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPlayerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbClose, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(jlpLogoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPlayerLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jlpRating, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jlblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jlblArtirst, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlpControlsContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(jbtBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(9, 9, 9)
                            .addComponent(jbtPlay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(jbtFoward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addComponent(jbtLower, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addComponent(jbtHight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jpPlayerLayout.createSequentialGroup()
                            .addGap(13, 13, 13)
                            .addComponent(jbtPlayList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jpControlsBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(60, 60, 60))
        );

        jlblDate.getAccessibleContext().setAccessibleName("jlblDate");

        jlpMain.add(jpPlayer);
        jpPlayer.setBounds(0, 0, 317, 407);

        jpListContent.setMaximumSize(new java.awt.Dimension(810, 363));
        jpListContent.setMinimumSize(new java.awt.Dimension(300, 363));
        jpListContent.setOpaque(false);
        jpListContent.setPreferredSize(new java.awt.Dimension(810, 363));
        jpListContent.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpList.setBackground(new java.awt.Color(255, 255, 204));
        jpList.setMaximumSize(new java.awt.Dimension(516, 363));
        jpList.setMinimumSize(new java.awt.Dimension(516, 363));
        jpList.setPreferredSize(new java.awt.Dimension(516, 363));

        jLabel1.setFont(vistaSansLight.deriveFont(Font.PLAIN, 24));
        jLabel1.setForeground(new java.awt.Color(52, 52, 51));
        jLabel1.setText("CONFIGURE SUA PLAYLIST");

        jslListContainer.setBorder(null);
        jslListContainer.setOpaque(false);

        javax.swing.GroupLayout jpListLayout = new javax.swing.GroupLayout(jpList);
        jpList.setLayout(jpListLayout);
        jpListLayout.setHorizontalGroup(
            jpListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpListLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jslListContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jpListLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpListLayout.setVerticalGroup(
            jpListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpListLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jslListContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jpListContent.add(jpList, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 520, -1));

        jlpMain.add(jpListContent);
        jpListContent.setBounds(-2, 6, 820, 363);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jlpMain, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlpMain, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public final void centralize()
    {
        //this.setLocationRelativeTo(null);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void startTweenPanel(int coefficient)
    {
        animationTimer.stop();
        this.coefficient = coefficient;
        animationTimer.start();
    }

    private void updateInfo()
    {
        if (playList != null && playList.getCurrentItem() != null)
        {
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            String time = df.format(playList.getCurrentItem().getDuration());

            jlblTimer.setText(time);
            jlblTitle.setText(playList.getCurrentItem().getTitle());
            jlblArtirst.setText(playList.getCurrentItem().getArtist());

            showHearts(playList.getCurrentItem().getRate());
            highlightRow();
        }
    }

    private void updateInfoadvertising()
    {

        if (_advertisingList != null && _advertisingList.getCurrentItem() != null)
        {
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            String time = df.format(_advertisingList.getCurrentItem().getDuration());

            jlblTimer.setText("Aguarde ...");
            jlblTitle.setText("Vinheta");
            jlblArtirst.setText("");

            showHearts(_advertisingList.getCurrentItem().getRate());
        }
    }

    private void resetInfo()
    {
        jlblTimer.setText("");
        jlblTitle.setText("");
        jlblArtirst.setText("");
        showHearts(0);
    }

    private void enableControls(boolean enable)
    {
        jbtPlay.setEnabled(enable);
        jbtBack.setEnabled(enable);
        jbtFoward.setEnabled(enable);
        jlpRating.setEnabled(enable);
        jslListContainer.setEnabled(enable);
    }

    private void formMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMousePressed
    {//GEN-HEADEREND:event_formMousePressed
        mouseDownCompCoords = evt.getPoint();
    }//GEN-LAST:event_formMousePressed

    private void formComponentMoved(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentMoved
    {//GEN-HEADEREND:event_formComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentMoved

    private void formMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseReleased
    {//GEN-HEADEREND:event_formMouseReleased
        mouseDownCompCoords = null;
    }//GEN-LAST:event_formMouseReleased

    private void formMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseDragged
    {//GEN-HEADEREND:event_formMouseDragged
        Point currCoords = evt.getLocationOnScreen();
        this.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
    }//GEN-LAST:event_formMouseDragged

    private void formMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseMoved
    {//GEN-HEADEREND:event_formMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseMoved

    private void jbCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCloseActionPerformed
        ApplicationContext.getCurrent().getContainer().close();
        WindowEvent closingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
    }//GEN-LAST:event_jbCloseActionPerformed

    private void jpRate1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate1MouseExited
        if (playList.getCurrentItem() != null)
        {
            hideHearts();
        }
    }//GEN-LAST:event_jpRate1MouseExited

    private void jpRate1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate1MouseEntered
        if (playList.getCurrentItem() != null)
        {
            showHearts(1);
        }
    }//GEN-LAST:event_jpRate1MouseEntered

    private void jpRate1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate1MouseClicked
        if (playList.getCurrentItem() != null)
        {
            String MusicaID = playList.getCurrentItem().setRate(1);
            ObjectContainer container = ApplicationContext.getCurrent().getContainer();

            ObjectSet<PlayerDetails> result = container.query().execute();

            String ClienteID = result.get(result.size() - 1).getClienteID();
            MusicaAvaliacao(MusicaID, ClienteID, (short) 4);
        }
    }//GEN-LAST:event_jpRate1MouseClicked

    private void jpRate2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate2MouseExited
        if (playList.getCurrentItem() != null)
        {
            hideHearts();
        }
    }//GEN-LAST:event_jpRate2MouseExited

    private void jpRate2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate2MouseEntered
        if (playList.getCurrentItem() != null)
        {
            showHearts(2);
        }
    }//GEN-LAST:event_jpRate2MouseEntered

    private void jpRate2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate2MouseClicked
        if (playList.getCurrentItem() != null)
        {
            String MusicaID = playList.getCurrentItem().setRate(2);
            ObjectContainer container = ApplicationContext.getCurrent().getContainer();

            ObjectSet<PlayerDetails> result = container.query().execute();

            String ClienteID = result.get(result.size() - 1).getClienteID();
            if (isConexaoFromWEB())
            {
                MusicaAvaliacao(MusicaID, ClienteID, (short) 2);
            }
        }
    }//GEN-LAST:event_jpRate2MouseClicked

    private void jpRate3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate3MouseExited
        if (playList.getCurrentItem() != null)
        {
            hideHearts();
        }
    }//GEN-LAST:event_jpRate3MouseExited

    private void jpRate3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate3MouseEntered
        if (playList.getCurrentItem() != null)
        {
            showHearts(3);
        }
    }//GEN-LAST:event_jpRate3MouseEntered

    private void jpRate3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate3MouseClicked
        if (playList.getCurrentItem() != null)
        {
            String MusicaID = playList.getCurrentItem().setRate(3);
            ObjectContainer container = ApplicationContext.getCurrent().getContainer();

            ObjectSet<PlayerDetails> result = container.query().execute();

            String ClienteID = result.get(result.size() - 1).getClienteID();
            if (isConexaoFromWEB())
            {
                MusicaAvaliacao(MusicaID, ClienteID, (short) 3);
            }
        }
    }//GEN-LAST:event_jpRate3MouseClicked

    private void jpRate4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate4MouseExited
        if (playList.getCurrentItem() != null)
        {
            hideHearts();
        }
    }//GEN-LAST:event_jpRate4MouseExited

    private void jpRate4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate4MouseEntered
        if (playList.getCurrentItem() != null)
        {
            showHearts(4);
        }
    }//GEN-LAST:event_jpRate4MouseEntered

    private void jpRate4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate4MouseClicked
        if (playList.getCurrentItem() != null)
        {
            String MusicaID = playList.getCurrentItem().setRate(4);
            ObjectContainer container = ApplicationContext.getCurrent().getContainer();

            ObjectSet<PlayerDetails> result = container.query().execute();

            String ClienteID = result.get(result.size() - 1).getClienteID();
            if (isConexaoFromWEB())
            {
                MusicaAvaliacao(MusicaID, ClienteID, (short) 4);
            }
        }
    }//GEN-LAST:event_jpRate4MouseClicked

    private void jpRate5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate5MouseExited
        if (playList.getCurrentItem() != null)
        {
            hideHearts();
        }
    }//GEN-LAST:event_jpRate5MouseExited

    private void jpRate5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate5MouseEntered
        if (playList.getCurrentItem() != null)
        {
            showHearts(5);
        }
    }//GEN-LAST:event_jpRate5MouseEntered

    private void jpRate5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jpRate5MouseClicked
        if (playList.getCurrentItem() != null)
        {
            String MusicaID = playList.getCurrentItem().setRate(5);
            ObjectContainer container = ApplicationContext.getCurrent().getContainer();

            ObjectSet<PlayerDetails> result = container.query().execute();

            String ClienteID = result.get(result.size() - 1).getClienteID();
            if (isConexaoFromWEB())
            {
                MusicaAvaliacao(MusicaID, ClienteID, (short) 5);
            }
        }
    }//GEN-LAST:event_jpRate5MouseClicked

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        JSlider source = (JSlider) evt.getSource();
        //if (!source.getValueIsAdjusting())
        //{
        float volume = (float) source.getValue();
        playList.setVolume(volume / 100);
        //}
    }//GEN-LAST:event_jSlider1StateChanged

    private void jbtPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPlayActionPerformed
        if (playList != null)
        {
            if (playList.getStatus() == MediaList.PLAYING)
            {
                playList.pause();
                timer.stop();
                jbtPlay.setIcon(new ImageIcon(getClass().getResource("/znn/resources/play.png")));
            }
            else
            {
                playList.play();
                updateInfo();
                System.out.println(playList.getCurrentItem().getDuration());
                _advertisingDuration += playList.getCurrentItem().getDuration();
                timer.start();
                jbtPlay.setIcon(new ImageIcon(getClass().getResource("/znn/resources/pause.png")));

            }
        }
    }//GEN-LAST:event_jbtPlayActionPerformed

    private void jbtBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBackActionPerformed
        if (playList != null)
        {
            try
            {
                playList.prev();
                updateInfo();

                _advertisingDuration += playList.getCurrentItem().getDuration();
                timer.start();
                jbtPlay.setIcon(new ImageIcon(getClass().getResource("/znn/resources/pause.png")));
            }
            catch (FileNotFoundException | JavaLayerException ex)
            {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbtBackActionPerformed

    private void jbtFowardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtFowardActionPerformed
        if (playList != null)
        {
            try
            {
                playList.next();
                updateInfo();

                _advertisingDuration += playList.getCurrentItem().getDuration();
                timer.start();
                jbtPlay.setIcon(new ImageIcon(getClass().getResource("/znn/resources/pause.png")));
            }
            catch (FileNotFoundException | JavaLayerException ex)
            {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbtFowardActionPerformed

    private void jbtLowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLowerActionPerformed
        if (playList != null)
        {
            float volume = this.playList.getVolume();
            if (volume > 0)
            {
                float newVolume = volume - 0.02f;
                this.playList.setVolume(newVolume);
                jSlider1.setValue((int) (newVolume * 100));
            }
        }
    }//GEN-LAST:event_jbtLowerActionPerformed

    private void jbtHightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtHightActionPerformed
        if (playList != null)
        {
            float volume = this.playList.getVolume();
            if (volume < 1)
            {
                float newVolume = volume + 0.02f;
                this.playList.setVolume(newVolume);
                jSlider1.setValue((int) (newVolume * 100));
            }
        }
    }//GEN-LAST:event_jbtHightActionPerformed

    private void jbtPlayListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPlayListActionPerformed
        if (playList != null)
        {
            float minValue = 317;
            if (jpListContent.getSize().width > minValue)
            {
                jbtPlayList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/manageDisabled.png")));
                startTweenPanel(-20);
            }
            else
            {
                jbtPlayList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/znn/resources/manage.png")));
                startTweenPanel(20);
            }
        }
    }//GEN-LAST:event_jbtPlayListActionPerformed

    private void jbClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbClose1ActionPerformed
        //ApplicationContext.getCurrent().getContainer().();

        this.setExtendedState(Frame.ICONIFIED);
        WindowEvent closingEvent = new WindowEvent(this, WindowEvent.WINDOW_ICONIFIED);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
        //Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);    }//GEN-LAST:event_jbClose1ActionPerformed
    }

    private void showHearts(int i)
    {
        for (int j = 1; j <= 5; j++)
        {
            ImagePanel jpRate = (ImagePanel) getComponentByName(jlpRating, "jpRate" + j);
            if (jpRate != null)
            {
                if (j <= i)
                {
                    jpRate.setImage("znn/resources/heart.png");
                }
                else
                {
                    jpRate.setImage("znn/resources/heartDisabled.png");
                }
            }
        }
    }

    private void hideHearts()
    {
        int rate = 0;
        if (playList.getCurrentItem() != null)
        {
            rate = playList.getCurrentItem().getRate();
        }
        showHearts(rate);
    }

    private Component getComponentByName(JComponent scope, String name)
    {
        for (int i = 0; i < scope.getComponentCount(); i++)
        {
            if (scope.getComponent(i).getName().equals(name))
            {
                return scope.getComponent(i);
            }
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainFrame(new PlayListService()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JButton jbClose;
    private javax.swing.JButton jbClose1;
    private javax.swing.JButton jbtBack;
    private javax.swing.JButton jbtFoward;
    private javax.swing.JButton jbtHight;
    private javax.swing.JButton jbtLower;
    private javax.swing.JButton jbtPlay;
    private javax.swing.JButton jbtPlayList;
    private javax.swing.JLabel jlblArtirst;
    private javax.swing.JLabel jlblDate;
    private javax.swing.JLabel jlblTimer;
    private javax.swing.JLabel jlblTitle;
    private javax.swing.JLayeredPane jlpControlsContent;
    private javax.swing.JLayeredPane jlpLogoContent;
    private javax.swing.JLayeredPane jlpMain;
    private javax.swing.JLayeredPane jlpRating;
    private javax.swing.JPanel jpControlsBackground;
    private javax.swing.JPanel jpList;
    private javax.swing.JPanel jpListContent;
    private javax.swing.JPanel jpLogo;
    private javax.swing.JPanel jpLogoBackground;
    private javax.swing.JPanel jpPlayer;
    private javax.swing.JPanel jpRate1;
    private javax.swing.JPanel jpRate2;
    private javax.swing.JPanel jpRate3;
    private javax.swing.JPanel jpRate4;
    private javax.swing.JPanel jpRate5;
    private javax.swing.JPanel jpTime;
    private javax.swing.JPanel jpTime1;
    private javax.swing.JScrollPane jslListContainer;
    // End of variables declaration//GEN-END:variables
}
