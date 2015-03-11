/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.keygen;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import znn.security.Encryption;
import znn.security.Hardware;

/**
 *
 * @author Edgar Mesquita
 */
public class MainApplet extends JApplet
{

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    public void init()
    {
        //this.getParameter("Message");
        
        String password = "ZannaSoundPlayer" + Hardware.getSerialNumber();
        String encPassword = Encryption.encrypt(password);
        JOptionPane.showInputDialog(null, "Seu serial:", encPassword);
    }
}