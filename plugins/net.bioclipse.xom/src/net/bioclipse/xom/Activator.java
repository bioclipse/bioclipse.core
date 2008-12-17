package net.bioclipse.xom;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin {
        //The shared instance.
        private static Activator plugin;
        public final static String PLUGIN_ID="net.bioclipse.xom";
        /**
         * The constructor.
         */
        public Activator() {
                plugin = this;
        }
        /**
         * This method is called upon plug-in activation
         */
        public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
}
