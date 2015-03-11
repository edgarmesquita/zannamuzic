/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import znn.PlayList;
import znn.PlayListItem;
import znn.ui.components.ImagePanel;
import znn.ui.components.RichLabel;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListRowPanel extends JPanel
{

    private JCheckBox jcbItem;
    private RichLabel jlblTitle;
    private RichLabel jlblArtist;
    private Font vistaSansAltBold;
    private final JLayeredPane jlpRating;
    private final ImagePanel ipRate1;
    private final ImagePanel ipRate2;
    private final ImagePanel ipRate3;
    private final ImagePanel ipRate4;
    private final ImagePanel ipRate5;
    private PlayList playList;
    private PlayListItem playListItem;

    public boolean isSelected()
    {
        return jcbItem.isSelected();
    }

    public void setSelected(boolean selected)
    {
        jcbItem.setSelected(selected);
    }

    public void setRate(int rate)
    {
        showHearts(rate);
        playListItem.setRate(rate);
    }

    public PlayListItem getPlayListItem()
    {
        return this.playListItem;
    }

    @Override
    public void setForeground(Color color)
    {
        if(this.jlblTitle != null)
            this.jlblTitle.setForeground(color);
        
        if(this.jlblArtist != null)
            this.jlblArtist.setForeground(color);
    }
    
    public void resetForeground()
    {
        if(this.jlblTitle != null)
            this.jlblTitle.setForeground(new Color(52, 52, 51));
        
        if(this.jlblArtist != null)
            this.jlblArtist.setForeground(new Color(95, 96, 92));
    }
    
    
    public PlayListRowPanel(PlayListItem playListItem)
    {
        this.playListItem = playListItem;

        try
        {
            vistaSansAltBold = Font.createFont(Font.TRUETYPE_FONT, MainFrame.class.getResourceAsStream("/znn/resources/fonts/VistaSanAltBol.ttf"));
        }
        catch (FontFormatException | IOException ex)
        {
            vistaSansAltBold = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
            Logger.getLogger(PlayListRowPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        playList = MainFrame.getInstance().getPlayList();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        layout.columnWidths = new int[]
        {
            18, 232, 120, 76
        };
        c.fill = GridBagConstraints.HORIZONTAL;


        this.setLayout(layout);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setOpaque(false);

        this.jcbItem = new JCheckBox();
        ImageIcon normal = new ImageIcon(getClass().getResource("/znn/resources/check02.png"));
        // ImageIcon rollover = new ImageIcon("rollover.gif");
        ImageIcon selected = new ImageIcon(getClass().getResource("/znn/resources/check01.png"));
        this.jcbItem.setRolloverIcon(normal);
        this.jcbItem.setSelectedIcon(selected);
        this.jcbItem.setIcon(normal);
        this.setSelected(true);
        c.gridx = 0;
        c.gridy = 0;
        this.add(this.jcbItem, c);

        c.insets = new Insets(2, 2, 2, 2);

        this.jlblTitle = new RichLabel();
        this.jlblTitle.setFont(vistaSansAltBold.deriveFont(Font.PLAIN, 13));
        this.jlblTitle.setForeground(new Color(52, 52, 51));
        this.jlblTitle.setRightShadow(1, 1, Color.WHITE);
        this.jlblTitle.setText(this.playListItem.getTitle());
        c.gridx = 1;
        c.gridy = 0;
        this.add(this.jlblTitle, c);

        this.jlblArtist = new RichLabel();
        this.jlblArtist.setFont(vistaSansAltBold.deriveFont(Font.PLAIN, 13));
        this.jlblArtist.setForeground(new Color(95, 96, 92));
        this.jlblArtist.setRightShadow(1, 1, Color.WHITE);
        String artista = this.playListItem.getArtist().length() >= 15 ? this.playListItem.getArtist().substring(0,14).concat("...") : this.playListItem.getArtist();
        this.jlblArtist.setText(artista);
        c.gridx = 2;
        c.gridy = 0;
        this.add(this.jlblArtist, c);


        jlpRating = new JLayeredPane();
        jlpRating.setPreferredSize(new java.awt.Dimension(76, 18));

        ipRate1 = new ImagePanel("znn/resources/heartDisabled.png");
        ipRate1.setName("ipRate1");
        ipRate2 = new ImagePanel("znn/resources/heartDisabled.png");
        ipRate2.setName("ipRate2");
        ipRate3 = new ImagePanel("znn/resources/heartDisabled.png");
        ipRate3.setName("ipRate3");
        ipRate4 = new ImagePanel("znn/resources/heartDisabled.png");
        ipRate4.setName("ipRate4");
        ipRate5 = new ImagePanel("znn/resources/heartDisabled.png");
        ipRate5.setName("ipRate5");

        addRate(ipRate5, 60);
        addRate(ipRate4, 45);
        addRate(ipRate3, 30);
        addRate(ipRate2, 15);
        addRate(ipRate1, 0);

        c.gridx = 3;
        c.gridy = 0;
        this.add(this.jlpRating, c);
        
        showHearts(playListItem.getRate());
    }

    private void addRate(ImagePanel imagePanel, int x)
    {
        Dimension d = new Dimension(18, 17);
        imagePanel.setPreferredSize(d);
        imagePanel.setSize(d);
        imagePanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jpRateMouseClicked(evt);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                jpRateMouseEntered(evt);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                jpRateMouseExited(evt);
            }
        });

        GroupLayout glRate = new GroupLayout(imagePanel);
        imagePanel.setLayout(glRate);
        glRate.setHorizontalGroup(glRate.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 18, Short.MAX_VALUE));
        glRate.setVerticalGroup(glRate.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 17, Short.MAX_VALUE));
        imagePanel.setBounds(x, 0, 18, 17);

        jlpRating.add(imagePanel, JLayeredPane.DEFAULT_LAYER);
    }

    private void jpRateMouseClicked(java.awt.event.MouseEvent evt)
    {
        ImagePanel imagePanel = (ImagePanel) evt.getSource();
        //PlayListModel.getInstance().get
        setRate(1);
    }

    private void jpRateMouseExited(java.awt.event.MouseEvent evt)
    {
        ImagePanel imagePanel = (ImagePanel) evt.getSource();
        hideHearts();
    }

    private void jpRateMouseEntered(java.awt.event.MouseEvent evt)
    {
        ImagePanel imagePanel = (ImagePanel) evt.getSource();
        showHearts(Integer.parseInt(imagePanel.getName().replace("ipRate", "")));
    }

    public void showHearts(int i)
    {
        for (int j = 1; j <= 5; j++)
        {
            ImagePanel jpRate = (ImagePanel) getComponentByName(this.jlpRating, "ipRate" + j);
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

    public void hideHearts()
    {
        int rate = 0;
        rate = playListItem.getRate();
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

    public String toString()
    {
        return this.playListItem.getTitle();
    }
}
