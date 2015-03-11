/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListMouseListener extends MouseAdapter
{

    private final JList<PlayListRowPanel> list;

    public PlayListMouseListener(JList<PlayListRowPanel> list)
    {
        this.list = list;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point p = e.getPoint();
        int index = list.locationToIndex(p);
        
        if(index >= 0)
        {
            PlayListRowPanel row = list.getModel().getElementAt(index);
            row.setSelected(true);
        }
    }
}