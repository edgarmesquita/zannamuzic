package znn.security;

import org.apache.commons.lang3.SystemUtils;
import znn.security.impl.*;

public class Hardware implements IHardware
{
    /**
     * Return computer serial number.
     *
     * @return Computer's SN
     */
    public static final String getSerialNumber()
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            return Hardware4Win.getSerialNumber();
        }
        if (SystemUtils.IS_OS_LINUX)
        {
            return Hardware4Nix.getSerialNumber();
        }
        if (SystemUtils.IS_OS_MAC_OSX)
        {
            return Hardware4Mac.getSerialNumber();
        }
        return null;
    }
}