package com.thomosim.consentcoin.ObserverPattern;

public class MyObservable<T> {
    private MyObserver<T> myObserver; // Only one MyObserver object per instance of MyObservable is necessary.

    public void setValue(T value) {
        myObserver.onChanged(value);
    }

    public void observe(MyObserver<T> myObserver) {
        this.myObserver = myObserver;
    }
}