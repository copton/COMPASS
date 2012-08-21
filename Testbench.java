
import locator.Locator;
import locator.CompoundPdf;

public class Testbench {

    public static void main(String[] args)
    {
        Locator loc = new Locator(); 
        loc.loadPlugin("GPSPlugin");
        CompoundPdf pdf = loc.getCompoundPdf();

        pdf.print("x");

        loc.unloadPlugin("GPSPlugin");
    }
}
