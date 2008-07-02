package org.springframework.osgi.compendium.config;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * @author Hal Hildebrand
 *         Date: Nov 2, 2006
 *         Time: 10:46:51 AM
 */
public class ManagedServiceListener {
    public ArrayList notifications = new ArrayList();


    public void eat(Dictionary props) {
        notifications.add(props);
    }
}
