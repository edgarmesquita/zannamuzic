/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.ui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PieChartPanel extends JPanel
{
    
    enum Type
    {
        
        STANDARD, SIMPLE_INDICATOR, GRADED_INDICATOR
    }
    private Type type = null; //the type of pie chart
    private ArrayList values;
    private ArrayList colors;
    private ArrayList gradingValues;
    private ArrayList gradingColors;
    double percent = 0; //percent is used for simple indicator and graded indicator
    private Color _mainColor = new Color(54, 143, 94);
    private boolean _ccw = false;
    public void setPercent(double percent)
    {
        this.percent = percent;
        this.repaint();
    }
    
    public PieChartPanel(double percent, Color color, boolean ccw)
    {
        this(percent, color);
        this._ccw = ccw;
    }
    
    public PieChartPanel(double percent, Color color)
    {
        this._mainColor = color;
        this.setOpaque(false);
        type = Type.SIMPLE_INDICATOR;
        this.percent = percent;
    }
    
    public PieChartPanel(ArrayList values, ArrayList colors)
    {
        this.setOpaque(false);
        type = Type.STANDARD;
        
        this.values = values;
        this.colors = colors;
    }
    
    public PieChartPanel(double percent, ArrayList gradingValues, ArrayList gradingColors)
    {
        this.setOpaque(false);
        type = Type.GRADED_INDICATOR;
        
        this.gradingValues = gradingValues;
        this.gradingColors = gradingColors;
        this.percent = percent;
        
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        int width = getSize().width;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
            RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (type == Type.SIMPLE_INDICATOR)
        {
            //colours used for simple indicator
            Color backgroundColor = new Color(0, 255, 0, 0);
            
            g2d.setColor(backgroundColor);
            g2d.fillOval(0, 0, width, width);
            g2d.setColor(_mainColor);
            Double angle = (percent / 100) * 360;
            
            //Anti horário
            if(_ccw) g2d.fillArc(0, 0, width, width, 90, angle.intValue());
            //Horário
            else g2d.fillArc(0, 0, width, width, -270, -angle.intValue());
            
        }
        else if (type == Type.STANDARD)
        {
            
            int lastPoint = -270;
            
            for (int i = 0; i < values.size(); i++)
            {
                g2d.setColor((Color) colors.get(i));
                
                Double val = (Double) values.get(i);
                Double angle = (val / 100) * 360;
                
                g2d.fillArc(0, 0, width, width, lastPoint, -angle.intValue());
                System.out.println("fill arc " + lastPoint + " "
                        + -angle.intValue());
                
                lastPoint = lastPoint + -angle.intValue();
            }
        }
        else if (type == Type.GRADED_INDICATOR)
        {
            
            int lastPoint = -270;
            
            double gradingAccum = 0;
            
            for (int i = 0; i < gradingValues.size(); i++)
            {
                g2d.setColor((Color) gradingColors.get(i));
                Double val = (Double) gradingValues.get(i);
                
                gradingAccum = gradingAccum + val;
                Double angle = null;
                /**
                 * * If the sum of the gradings is greater than the percent,
                 * then we want to recalculate * the last wedge, and break out
                 * of drawing.
                 */
                if (gradingAccum > percent)
                {
                    
                    System.out.println("gradingAccum > percent");

                    //get the previous accumulated segments. Segments minus last one
                    double gradingAccumMinusOneSegment = gradingAccum - val;

                    //make an adjusted calculation of the last wedge
                    angle = ((percent - gradingAccumMinusOneSegment) / 100) * 360;
                    
                    g2d.fillArc(0, 0, width, width, lastPoint, -angle.intValue());
                    
                    lastPoint = lastPoint + -angle.intValue();
                    
                    break;
                    
                }
                else
                {
                    
                    System.out.println("normal");
                    angle = (val / 100) * 360;
                    
                    g2d.fillArc(0, 0, width, width, lastPoint, -angle.intValue());
                    
                    System.out.println("fill arc " + lastPoint + " "
                            + -angle.intValue());
                    
                    lastPoint = lastPoint + -angle.intValue();
                }
            }
        }
    }
}