package plugins;

import java.util.Vector;
import java.lang.Thread;

import services.ServiceUser;
import services.Service;
import locator.PDFDrain;
import pdf.PDF;
import logger.Logger;


/**
 * An abstract plugin.
 * Every plugin has its own thread to enable parallel estimation of the PDFs.
 * The thread blocks until it is triggered by the locator. A PDF is then calculated and returned by using the
 * callback.
 */
public abstract class AbstractPlugin extends Thread implements ServiceUser, Plugin {

    protected AbstractPlugin()
    {
        services = null;
        decouple = new Decouple();
    }

    /*******  Plugin *****/
    /**
     * trigger the caclulation of the current PDF.
     * @param drain the callback object
     * @param timeout the maximum absolut system time until the PDF must be returned
     * @param handle a handle which is to be passed at callback
     */
    public void trigger(PDFDrain drain, long timeout, int handle)
    {
        synchronized(decouple) {
            if (decouple.waiting) {
                decouple.drain = drain;
                decouple.handle = handle;
                decouple.timeout = timeout;
                decouple.notify();
            } else {
                Logger.error("Plugin", "trigger", "Plugin not ready");
            }
        }
    }

    /**
     * load the plugin, start the thread.
     */
    public void load()
    {
        start();
    }

    /**
     * unload the plugin, stop the thread.
     */
    public void unload()
    {
        finish = true;
        interrupt();
    }

    /*****  ServiceUser *****/

    /**
     * assign a list of services to the plugin.
     */
    public void setServices(Vector services)
    {
        this.services = services;
    }
    
    /**
     * check which services are currently assigned to the plugin
     */
    public Vector getServices()
    {
        return services;
    }
    
    /**
     * get a list of service objects of the appropriate type
     */
    public abstract Vector getRequiredServices();
    
    /*** protected ***/

    /**
     * check if all required services where assigned to the plugin.
     * @return true if they were.
     */
    protected abstract boolean needMoreServices();

    /**
     * calculate the current PDF.
     */
    protected abstract PDF createPdf(long timeout);

    /**
     * prepare to get deleted, so free all aquired ressource
     */
    protected abstract void freeRessources();

    protected Vector services;

    /*****  private ****/

    public void run()
    {
        PDFDrain drain;
        long timeout;
        int handle;

        finish = false;
        while (! finish) {
            synchronized(decouple) {
                decouple.waiting = true;
                try {
                    decouple.wait();
                } catch (java.lang.InterruptedException e) {
                    if (! finish) {
                        Logger.error("Plugin", "run", "thread was interrupted");
                        continue;
                    } else {
                        break;
                    }
                }
                drain = decouple.drain;
                handle = decouple.handle;
                timeout = decouple.timeout;
            }
            if (! needMoreServices()) {
                PDF pdf = createPdf(timeout);
                if (pdf != null && System.currentTimeMillis() < timeout) {
                    drain.callback(pdf, handle);
                }
            }
        }

        freeRessources();
    }

    private class Decouple {
        Decouple()
        {
            drain = null;
            timeout = 0;
            handle = 0;
            waiting = false;
        }   

        private boolean finish;
        
        public PDFDrain drain;
        public int handle;
        public long timeout;
        public boolean waiting;
    }

    private Decouple decouple;

    private boolean finish;
}
