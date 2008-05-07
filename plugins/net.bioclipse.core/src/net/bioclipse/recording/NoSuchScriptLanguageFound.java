package net.bioclipse.recording;

public class NoSuchScriptLanguageFound extends Exception {

    private static final long serialVersionUID = -3593316094806126773L;

    public NoSuchScriptLanguageFound(String scriptLanguage) {
        super(scriptLanguage);
    }
}
