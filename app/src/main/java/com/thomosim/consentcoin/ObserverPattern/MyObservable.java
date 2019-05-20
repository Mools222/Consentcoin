package com.thomosim.consentcoin.ObserverPattern;

public class MyObservable<T> {
    private MyObserver<T> myObserver; // Only one observer is necessary for this program.

    public void setValue(T value) {
        myObserver.onChanged(value);
    }

    public void observe(MyObserver<T> myObserver) {
        if (myObserver == null)
            throw new NullPointerException();
        this.myObserver = myObserver;
    }
}