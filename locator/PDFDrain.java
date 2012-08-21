package locator;

import pdf.PDF;

public interface PDFDrain {
    public void callback(PDF pdf, int handle);
}

