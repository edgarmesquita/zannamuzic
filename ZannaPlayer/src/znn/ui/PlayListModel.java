/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputAdapter;
import znn.PlayList;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListModel extends JList<PlayListRowPanel>
{
    private DefaultListModel<PlayListRowPanel> listModel;
    private static PlayListModel singleton;
    
    public static PlayListModel getInstance()
    {
        return singleton;
    }
    
    public void addRow(PlayListRowPanel row)
    {
        
        this.listModel.addElement(row);
    }
    
    public PlayListRowPanel getRow(int index)
    {
        return this.listModel.getElementAt(index);
    }
    
    public DefaultListModel<PlayListRowPanel> getAllRows()
    {
        return this.listModel;
    }
    
    private void updatePositions()
    {
        PlayList playList = MainFrame.getInstance().getPlayList();
        
        for (int i = 0; i < listModel.size(); i++)
        {
            listModel.get(i).getPlayListItem().setPosition(i);
        }
        playList.sortItems();
    }
    public PlayListModel()
    {
        singleton = this;
        listModel = new DefaultListModel<PlayListRowPanel>();
        
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setLayoutOrientation(JList.VERTICAL);
        this.setCellRenderer(new PlayListRenderer());
        
        /*this.setCellRenderer(new PlayListRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected, boolean CellHasHocus)
            {
                JCheckBox jsbox = new JCheckBox();
                jsbox.setBackground(new Color(0,0,0,0));
                //jsbox.setIcon(new ImageIcon(getClass().getResource("/znn/resources/scroll.png")));
                return jsbox;
            }
        }
        );*/
        final MouseInputAdapter adapter = new MouseInputAdapter()
        {
            private boolean mouseDragging = false;
            private int dragSourceIndex;

            @Override
            public void mousePressed(MouseEvent e)
            {
                dragSourceIndex = singleton.getSelectedIndex();
                //int dragTargetIndex = singleton.getSelectedIndex();
                //singleton.setSelectedIndex(dragTargetIndex);
                //singleton.updateUI();
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (mouseDragging && dragSourceIndex >= 0)
                {
                    int dragTargetIndex = singleton.getSelectedIndex();
                    PlayListRowPanel dragElement = listModel.get(dragSourceIndex);
                    listModel.remove(dragSourceIndex);
                    listModel.add(dragTargetIndex, dragElement);

                    singleton.setSelectedIndex(dragTargetIndex);
                    singleton.updateUI();
                    
                    updatePositions();
                }
                mouseDragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                mouseDragging = true;
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                mouseDragging = false;

                Point p = e.getPoint();
                int index = singleton.locationToIndex(p);

                if (index >= 0)
                {
                    PlayListRowPanel row = listModel.getElementAt(index);
                    Point np = new Point(p.x, p.y - (22 * index));

                    for (int i = 1; i <= 5; i++)
                    {
                        if (p.x >= 370 && p.x <= 370 + (15 * i))
                        {
                            row.setRate(i);
                            singleton.updateUI();
                            break;
                        }
                    }


                    for (int i = 0; i < row.getComponentCount(); i++)
                    {
                        Component component = row.getComponent(i);
                        Point location = component.getLocation();

                        int x0 = location.x;
                        int x1 = component.getWidth() + x0;
                        int y0 = location.y;
                        int y1 = component.getHeight() + y0;

                        
                        if (np.x >= x0 && np.x <= x1 && np.y >= y0 && np.y <= y1)
                        {
                            if (component instanceof JCheckBox)
                            {
                                
                                row.setSelected(!row.isSelected());
                                singleton.updateUI();
                            }
                            break;
                        }
                    }
                }

            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                Point p = e.getPoint();
                PlayListRowPanel row = getRowByPoint(p);

                for (int i = 0; i < singleton.getModel().getSize(); i++)
                {
                    singleton.getModel().getElementAt(i).hideHearts();
                }

                if (row != null)
                {
                    int index = singleton.locationToIndex(p);
                    Point np = new Point(p.x, p.y - (22 * index));

                    if (np.x < 370 || np.y == 0 || np.y >= 21)
                    {
                        row.hideHearts();
                        singleton.updateUI();
                    }
                    else
                    {
                        for (int i = 1; i <= 5; i++)
                        {
                            if (p.x >= 370 && p.x <= 370 + (15 * i))
                            {
                                row.showHearts(i);
                                singleton.updateUI();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                
                
                
            }

            private PlayListRowPanel getRowByPoint(Point p)
            {
                int index = singleton.locationToIndex(p);
                if (index >= 0)
                {
                    return listModel.getElementAt(index);
                }
                return null;
            }
        };
        this.setModel(listModel);

        this.addMouseListener(adapter);
        this.addMouseMotionListener(adapter);
        this.setFocusable(true);
        this.setOpaque(false);
        this.setBorder(null);
        this.getInsets().set(0, 0, 0, 0);
    }
}
