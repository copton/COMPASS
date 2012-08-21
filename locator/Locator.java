package locator;

import plugins.Plugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import logger.Logger;
import pdf.PDF;
import config.Config;

/**
 * The locator is responsible for requesting current PDFs from all registered plugins
 * and calculate the compound PDF out of it.
 */
public class Locator implements PDFDrain {
    private HashMap plugins;

    public Locator() {
        counter = -1;
        plugins = new HashMap();
        pdfs = new Vector();
    }
   
    /**
     * load a new plugin.
     * If it is loaded already, unload it first.
     * @param name the name of the plugin class
     */
    public void loadPlugin(String name)
    {
        if (plugins.containsKey(name)) {
            unloadPlugin(name);
        }

        Class pluginClass;
        try {
            pluginClass = Class.forName("plugins." + name);
        } catch (ClassNotFoundException e) {
            Logger.error("Locator", "loadPlugin", "ClassNotFoundException: " + e.getMessage());
            return;
        }

        Plugin plugin;
        try {
            plugin = (Plugin) pluginClass.newInstance();
        } catch (Exception e) {
            Logger.error("Locator", "loadPlugin", "Exception caught while trying to instanciate the plugin: " + e.getMessage());
            return;
        }

        plugins.put(name, plugin);
        plugin.load();
    }

    /**
     * unload a plugin.
     * If it is not loaded nothing happens
     * @param name the name of the plugin class
     */
    public void unloadPlugin(String name)
    {
        Plugin plugin = (Plugin) plugins.remove(name);
        if (plugin == null) {
            return;
        }

        plugin.unload();
    }


    /**** PDFDrain ****/

    /**
     * implementation of the callback handler.
     * Accepts a PDF from a plugin only if the handle matches. This is to block late deliveries of PDFs.
     * @param pdf the plugins PDF
     * @param handle the handle which was passed to the plugin when triggered to generate a new PDF
     */
    public void callback(PDF pdf, int handle)
    {
        synchronized (pdfs) {
            if (handle != counter) {
                return;
            }
            pdfs.add(pdf);
            if (pdfs.size() == numberOfPlugins) {
                pdfs.notify();
            }
        }
    }

    /**
     * triggers the plugins to create a current PDF and returns the compound PDF.
     * After triggering all plugins the function waits to give the plugins enough time.
     * As soon as all plugins returned a PDF through the callback or after the timeout exceeded the function
     * starts to calculate the compound PDF
     * @return   the compound PDF over all delivered PDFs
     */
    public CompoundPdf getCompoundPdf()
    {
        synchronized (pdfs) {
            numberOfPlugins = plugins.size();
        }

        Iterator i = plugins.entrySet().iterator(); 
        long timeout = Config.locator.Locator.getTimeout();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Plugin plugin = (Plugin) entry.getValue();
            plugin.trigger(this, System.currentTimeMillis() + timeout, counter);
        }

        CompoundPdf compoundPdf;
        synchronized (pdfs) {
            try {
                pdfs.wait(timeout, 0);
            } catch (InterruptedException e) {
                ;
            }

            counter += 1; // exclude all plugins which are late
            if (pdfs.size() == 0) {
                return null;
            }
            compoundPdf = new CompoundPdf(pdfs);
            pdfs = new Vector();
        }

        return compoundPdf;
    }
 
    private Vector pdfs;
    private int counter;
    private int numberOfPlugins;
}
