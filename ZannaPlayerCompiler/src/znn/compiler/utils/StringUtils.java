/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.compiler.utils;

/**
 *
 * @author Edgar Mesquita
 */
public class StringUtils
{
    public static String join(String join, String... strings)
    {
        if (strings == null || strings.length == 0)
        {
            return "";
        }
        else if (strings.length == 1)
        {
            return strings[0];
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(strings[0]);
            for (int i = 1; i < strings.length; i++)
            {
                sb.append(join).append(strings[i]);
            }
            return sb.toString();
        }
    }
}
