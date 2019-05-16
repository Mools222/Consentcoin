package com.thomosim.consentcoin.Testing;

import java.util.ArrayList;

public class MyObservable<T> {
    private ArrayList<MyObserver<? super T>> myObservers = new ArrayList<>();

    void setValue(T value) {
        for (MyObserver<? super T> myObserver : myObservers) {
            myObserver.onChanged(value);
        }
    }

    void observe(MyObserver<? super T> myObserver) {
        if (myObserver == null)
            throw new NullPointerException();
        if (!myObservers.contains(myObserver)) {
            myObservers.add(myObserver);
        }
    }
}