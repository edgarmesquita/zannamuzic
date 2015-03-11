/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package znn.models;

import java.util.Date;

/**
 *
 * @author Administrador
 */
public class PlayerDetails {
    private String dataEncerramento;
    private String cliente;
    private String clienteID;
    public PlayerDetails(String dataEncerramento)
    {
        this.dataEncerramento = dataEncerramento;
    }    
    public String getClienteID()
    {
        return this.clienteID;
    }   
    public void setClienteID(String clienteID)
    {
        this.clienteID = clienteID;
    }     
    public String getCliente()
    {
        return this.cliente;
    }   
        public void setCliente(String cliente)
    {
        this.cliente = cliente;
    }  
    public String getDataEncerramento() {
        return this.dataEncerramento;
    }
    public void setDataEncerramento(String dataEncerramento)
    {
        this.dataEncerramento = dataEncerramento;
    }    
    
}
