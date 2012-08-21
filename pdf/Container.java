package pdf;

import java.util.Vector;
import java.util.Iterator;

import utils.index.Index;
import coordinates.area.AreaException;
import coordinates.area.Area;
import coordinates.Cartesian;

/**
 * A container of PDFs.
 * This is to allow local exceptions for PDFs.
 * There is one master pdfs an arbitraty amount of exception PDFS. The exception PDFs may not overlap.
 */
class Container extends PDF {
    /**
     * Standart constructor.
     * @param master the master PDF
     * @param a vector of exception PDFs
     */
    public Container(PDF master, Vector exclusions) throws AreaException
    {
        super(master.getArea());

        this.master = master;
        index = Index.newIndex(master.getArea().getOrigin());

        for (Iterator i = exclusions.iterator(); i.hasNext(); ) {
            addPdf((PDF) i.next());
        }
    }

    public Probability getProbability(Cartesian coordinates) throws AreaException 
    {
        checkArea(coordinates);

        IndexUnit unit = (IndexUnit) index.get(coordinates);

        if (unit == null) {
            return master.getProbability(coordinates);
        } else {
            Probability prob = unit.pdf.getProbability(coordinates);
            return prob.scale(unit.condProb);
        }
    }

    /**
     * add a PDF to the container.
     * @throws AreaException if the area of the PDF overlaps with an other PDF's area
     */
    private void addPdf(PDF pdf) throws AreaException
    {
        if (! master.getArea().isSupersetOf(pdf.getArea())) {
            throw new AreaException();
        }

        Area diff;
        diff = master.getArea().intersect(pdf.getArea());

        Probability conditionalProb = new Probability(0);
        for (Iterator i = diff.iterator(); i.hasNext(); ) {
            Cartesian pos = (Cartesian) i.next();
            conditionalProb.add(master.getProbability(pos));
        }
        
        // the exclusions may not overlap
        for (Index.Iterator i = index.iterator(); i.hasNext(); ) {
            IndexUnit unit = (IndexUnit) i.next();
            diff = pdf.getArea().intersect(unit.area);
            if (! diff.isEmpty()) {
                throw new AreaException();
            }
        }

        index.add((Area) pdf.getArea().clone(), new IndexUnit(pdf.getArea(), conditionalProb, pdf));
    }

    private class IndexUnit {
        public IndexUnit(Area area, Probability condProb, PDF pdf)
        {
            this.area = area;
            this.condProb = condProb;
            this.pdf = pdf;
        }
        
        public Area area;
        public Probability condProb;
        public PDF pdf;
    };
    
    private Index index;
    private PDF master;
    
}
