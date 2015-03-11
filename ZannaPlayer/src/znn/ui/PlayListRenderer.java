/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListRenderer implements ListCellRenderer
{
    public void PlayListRenderer()
    {
        
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        PlayListRowPanel playListRow = (PlayListRowPanel)value;
        playListRow.setOpaque(isSelected);
        
        JCheckBox jsbox = new JCheckBox();
        jsbox.setBackground(Color.red);
        
        list.add(jsbox);
        
        return playListRow;
    }
}