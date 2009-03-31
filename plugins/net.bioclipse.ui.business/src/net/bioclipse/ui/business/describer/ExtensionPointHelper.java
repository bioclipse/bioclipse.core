package net.bioclipse.ui.business.describer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class ExtensionPointHelper {

    public static List<IBioObjectDescriber> getAvailableDescribersFromEP() throws BioclipseException{

        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if ( registry == null ) throw new BioclipseException(
        "Eclipse registry=null");
        // it likely means that the Eclipse workbench has not
        // started, for example when running tests

        List<IBioObjectDescriber> describers = new ArrayList<IBioObjectDescriber>();

        IExtensionPoint serviceObjectExtensionPoint = registry
        .getExtensionPoint("net.bioclipse.ui.bioobjectDescriber");

        IExtension[] serviceObjectExtensions
        = serviceObjectExtensionPoint.getExtensions();

        for(IExtension extension : serviceObjectExtensions) {
            for( IConfigurationElement element
                    : extension.getConfigurationElements() ) {

                if (element.getName().equals("BioObject")){

                    //                    String id=element.getAttribute("id");
                    //                    String rname=element.getAttribute("name");

                    Object obj;
                    try {
                        obj = element.createExecutableExtension("describer");
                        IBioObjectDescriber describer = (IBioObjectDescriber) obj;
                        describers.add( describer );
                    } catch ( CoreException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return describers;

    }

    public static Map<String, String> getAvailableAliasesFromEP() throws BioclipseException {

        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if ( registry == null ) throw new BioclipseException(
        "Eclipse registry=null");
        // it likely means that the Eclipse workbench has not
        // started, for example when running tests

        Map<String, String> aliases=new HashMap<String, String>();

        IExtensionPoint serviceObjectExtensionPoint = registry
        .getExtensionPoint("net.bioclipse.ui.business.editorShortname");

        IExtension[] serviceObjectExtensions
        = serviceObjectExtensionPoint.getExtensions();

        for(IExtension extension : serviceObjectExtensions) {
            for( IConfigurationElement element
                    : extension.getConfigurationElements() ) {

                if (element.getName().equals("shortname")){

                    String alias=element.getAttribute("alias");
                    String editorID=element.getAttribute("editorID");

                    aliases.put( alias, editorID );

                }
            }
        }

        return aliases;    
    }

}
