package com.thomosim.consentcoin.ObserverPattern;

public interface MyObserver<T> {
    void onChanged(T t);
}
