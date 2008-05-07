package net.bioclipse.recording;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jonathan
 *
 */
public class CompositeRecord implements IRecord {

    protected Map<String, String> scripts = new HashMap<String, String>();
    
    public void addScriptRecord( String scriptLanguage, String scriptRecord ) {

        scripts.put(scriptLanguage, scriptRecord);
    }

    public String getScript( String scriptLanguage ) 
                  throws NoSuchScriptLanguageFound {
        
        if( !scripts.containsKey(scriptLanguage) ) {
            throw new NoSuchScriptLanguageFound(scriptLanguage);
        }
        return scripts.get(scriptLanguage);
    }
}
