/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Edgar Mesquita
 */
public class Encryption
{
    private static String key = "Ẓἠἡ√2013";
    
    public static String encrypt(String value)
    {
        try
        {
            DESKeySpec keySpec = new DESKeySpec(key.getBytes("UTF8")); 
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey sKey = keyFactory.generateSecret(keySpec);
            BASE64Encoder base64encoder = new BASE64Encoder();
            
            byte[] cleartext = value.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            return base64encoder.encode(cipher.doFinal(cleartext ));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted)
    {
        try
        {
            DESKeySpec keySpec = new DESKeySpec(key.getBytes("UTF8")); 
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey sKey = keyFactory.generateSecret(keySpec);
            BASE64Decoder base64decoder = new BASE64Decoder();
            
            byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encrypted);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            byte[] plainTextBytes = (cipher.doFinal(encrypedPwdBytes));
            
            return new String(plainTextBytes, "UTF8");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
