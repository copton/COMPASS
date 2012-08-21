package services;

import java.util.Vector;

public interface ServiceUser {
    public void setServices(Vector services);
    public Vector getServices();

    public Vector getRequiredServices();
}
