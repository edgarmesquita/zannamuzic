package znn;

import java.util.Comparator;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayListItemComparator implements Comparator<PlayListItem> 
{

    @Override
    public int compare(PlayListItem o1, PlayListItem o2)
    {
        Integer p1 = o1.getPosition();
        return p1.compareTo(o2.getPosition());
    }
    
}