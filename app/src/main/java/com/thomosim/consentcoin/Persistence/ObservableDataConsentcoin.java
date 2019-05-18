package com.thomosim.consentcoin.Persistence;

import android.os.AsyncTask;

import com.thomosim.consentcoin.ObserverPattern.MyObservable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ObservableDataConsentcoin extends MyObservable<Consentcoin> {

    public void setConsentcoinUrl(String storageUrl) {
        try {
            URL url = new URL(storageUrl);
            new DownloadConsentcoins().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // To solve the "leaks might occur" warning: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur/46166223#46166223
    private class DownloadConsentcoins extends AsyncTask<URL, Void, Void> {
        private Consentcoin consentcoin;

        @Override
        protected Void doInBackground(URL... urls) {
            try {
                for (URL url: urls) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(url.openStream()));
                    // TODO (3) Decrypt the Consentcoin object
                    consentcoin = (Consentcoin) objectInputStream.readObject();
                    objectInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setValue(consentcoin);
        }
    }
}
