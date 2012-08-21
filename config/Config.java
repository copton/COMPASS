package config;

/**
 * The central configuration class.
 * extend it to read the config from a config file or alike.
 * The configuration is structured like the source directory using package name, class name and a getter function
 */
public class Config {
    public static class plugin {
        public static class GPSPlugin {
            /**
             * returns the name of the serial device where the GPS sensor is connected to.
             * @return name of the GPS port
             */
            public static String getDevice()
            {
                return "/dev/ttyUSB0";
            }

            /**
             * return the deviation of GPS position measurement in meter.
             * The PDF of GPS measurement is a Gauss distribution. This means infinite expanse. 
             * For pracitcal use we need to stop somewhere. This constant controls after which
             * distance we consider the probability to be zero. This usually is the deviation of
             * GPS position estimation itself.
             * 
             * @return the deviation in meter
             */
            public static int getDeviation()
            {
                return 20; 
            }
            
            /**
             * return variance of GPS position estimation.
             * @return the variance in meters
             **/
            public static double getVariance()
            {
                return 20.0;
            }
        }
    }
    public static class coordinates {
        public static class Cartesian {
            /** 
             * return resolution of the grid in centimeters. 
             * The COMPASS system must work with descrete points. The constant controls how many centimeters
             * one grid covers. This is the unit of the Cartesian coordinates.
             * @return the resolution of the grid in centimeters
             * */
            public static int getResolution()
            {
                return 10;
            }
        }
    }
    public static class locator {
        public static class Locator {
            /**
             * returns the maximum timeout in milliseconds.
             * When a plugin is triggered for delivering of a PDF it has a maxium of time until the locator does
             * not consider its PDFs any more. This is controled by this constant.
             * @return the maximum timeout in milliseconds.
             */
            public static long getTimeout()
            {
                return 2000;
            }
        }
    }
    public static class pdf {
        public static class CompoundPdf {
            /**
             * returns the minimum probability.
             * When calculating the compound PDF it is no good when considering the probability to be zero for
             * coordinates a PDF does not cover. Instead the following probability is considered.
             * @return the minimum probability
             */
            public static double getEpsilon()
            {
                return 0.00000001;
            }
        }
    }
}
