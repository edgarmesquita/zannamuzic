/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Edgar Mesquita
 */
public class ImagePanel extends JPanel {

    private Image _img;

    public ImagePanel(String source){//, LayoutManager layout) {
        setOpaque(false);
        setImage(source);
        //super.setLayout(layout);
    }

    public void setImage(String source)
    {
        this._img = new ImageIcon(getClass().getResource("/"+source)).getImage();
        Dimension size = new Dimension(_img.getWidth(null), _img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        //setLayout(null);
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(_img, 0, 0, null);
    }
}