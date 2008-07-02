package org.springframework.osgi.compendium.config;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * @author Hal Hildebrand
 *         Date: Nov 2, 2006
 *         Time: 10:47:18 AM
 */
public class ManagedServiceFactoryListener {
    public ArrayList notifications = new ArrayList();


    public void eat(String pid, Dictionary props) {
        notifications.add(new Object[]{pid, props});
    }


    public void deleteInstance(String pid) {
        notifications.add(pid);
    }
}
