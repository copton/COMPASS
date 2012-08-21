package plugins;

public interface Plugin {
    public void trigger(locator.PDFDrain drain, long timeout, int handle);
    public void load();
    public void unload();
}
