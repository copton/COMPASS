package plugins;


/* 
import gnu.io.*;
import javax.comm.*;
import java.io.*;
*/
import java.util.*;  

import logger.Logger;
import config.Config;
import coordinates.area.Area;
import coordinates.Cartesian;
import pdf.GaussDistribution;
import services.Service;
import services.GPSService;
import pdf.PDF;
import coordinates.WGS84;


import java.lang.Math;
import java.lang.Thread;

 
public class GPSPlugin extends AbstractPlugin {

    public GPSPlugin()
    {
        calcParams();
    }

    /****** Plugin ******/

    public Vector getRequiredServices()
    {
        return null;
    }

    protected boolean needMoreServices()
    {
        return false;
    }

    protected PDF createPdf(long timeout) 
    {
        WGS84 position;
        PDF newPDF;

        position = scanForPosition(timeout);
        if (position == null) {
            return null;
        }

        newPDF = calcPDF(position);

        return newPDF;
    }

    protected void freeRessources()
    {
    }

    
    /***** private *****/

    private WGS84 scanForPosition(long timeout) 
    {
        return new WGS84(2.3, 4.2, 1.0); 
    }

    private PDF calcPDF(WGS84 position)
    {
        Vector params = new Vector();
        params.add(lowerLeftFront);
        params.add(upperRightBack);
        Area area = Area.newArea(position, params);
        return new GaussDistribution(area, Config.plugin.GPSPlugin.getVariance());
    }

    private void calcParams()
    {
        int deviation = Config.plugin.GPSPlugin.getDeviation();
        int resolution = Config.coordinates.Cartesian.getResolution();
        
        int l = (int) Math.sqrt(2 * deviation * 100 / resolution);
        lowerLeftFront = new Cartesian(-l, -l, -l);
        upperRightBack = new Cartesian(l, l, l);
    }

    private Cartesian lowerLeftFront;
    private Cartesian upperRightBack;
}


