package org.picketlink.as.subsystem.model.event;


public interface Event<T> {
    
    T getSource();
    
    void raise(EventManager manager);
}
