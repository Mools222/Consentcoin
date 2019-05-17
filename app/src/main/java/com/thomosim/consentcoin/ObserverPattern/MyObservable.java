package com.thomosim.consentcoin.ObserverPattern;

import java.util.ArrayList;

public class MyObservable<T> {
    private ArrayList<MyObserver<? super T>> myObservers = new ArrayList<>();

    public void setValue(T value) {
        for (MyObserver<? super T> myObserver : myObservers) {
            myObserver.onChanged(value);
        }
    }

    public void observe(MyObserver<? super T> myObserver) {
        if (myObserver == null)
            throw new NullPointerException();
        if (!myObservers.contains(myObserver)) {
            myObservers.add(myObserver);
        }
    }
}