package org.picketlink.as.subsystem.model.event;


public interface Event<T extends Observer> {
    
    void raise(T observer);
    
}
