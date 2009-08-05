/*******************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.core.util;


/**
 * Converts from milliseconds to a String with days minutes seconds.
 * 
 * @author jonalv
 *
 */
public class TimeCalculater {

    public final static long SECOND = 1000;
    public final static long MINUTE = SECOND * 60;
    public final static long HOUR = MINUTE * 60;
    public final static long DAY = HOUR * 24;
    
    /**
     * @return a String of the form xd xh xm xs
     */
    public static String millisecsToString(long time) {
        StringBuilder result = new StringBuilder();
        long timeleft = time;
        if ( timeleft > DAY ) {
            long days = (time / DAY);
            result.append( days );
            result.append( "d " );
            timeleft = timeleft - (days * DAY);
        }
        if ( timeleft > HOUR ) {
            long hours = (timeleft / HOUR);
            result.append( hours );
            result.append( "h " );
            timeleft = timeleft - (hours * HOUR); 
        }
        if ( timeleft > MINUTE ) {
            long minutes = (timeleft / MINUTE);
            result.append( minutes );
            result.append( "m " );
            timeleft = timeleft - (minutes * MINUTE);
        }
        if ( timeleft > SECOND ) {
            long seconds = (timeleft / SECOND);
            result.append( seconds );
            result.append( "s" );
        }
        return result.toString();
    }
}
