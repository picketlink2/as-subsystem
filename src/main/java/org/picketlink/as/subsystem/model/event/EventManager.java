package org.picketlink.as.subsystem.model.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EventManager {

    private Map<String, List<Observer>> observer;
    
    public void addObserver(Class<? extends Event> eventType, Observer observer) {
        List<Observer> observers = getObserver().get(eventType.getName());
        
        if (observers == null) {
            observers = new ArrayList<Observer>();
            getObserver().put(eventType.getName(), observers);            
        } 
        
        observers.add(observer);
    }
    
    public void raise(Event event) {
        if (getObserver().get(event.getClass().getName()) != null) {
            for (Observer observer : getObserver().get(event.getClass().getName())) {
                event.execute(observer);
            }
        }
    }
    
    public Map<String, List<Observer>> getObserver() {
        if (this.observer == null) {
            this.observer = new HashMap<String, List<Observer>>();
        }

        return this.observer;
    }

    public void removeObserver(Observer observerToRemove) {
        for (Map.Entry<String, List<Observer>> entry : this.getObserver().entrySet()) {
            for (Observer observer : new ArrayList<Observer>(entry.getValue())) {
                if (observer == observerToRemove) {
                    entry.getValue().remove(observer);
                }
            }
        }
    }
    
}
