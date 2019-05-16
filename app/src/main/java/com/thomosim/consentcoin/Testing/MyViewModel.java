package com.thomosim.consentcoin.Testing;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.thomosim.consentcoin.Persistence.User;
import com.thomosim.consentcoin.Testing.FirebaseLiveDataUser;
import com.thomosim.consentcoin.Testing.FirebaseUtilities;

public class MyViewModel extends AndroidViewModel {

    private final FirebaseLiveDataUser firebaseLiveDataUser;

    public MyViewModel(@NonNull Application application) {
        super(application);
        FirebaseUtilities firebaseUtilities = FirebaseUtilities.getInstance(application);
        firebaseLiveDataUser = new FirebaseLiveDataUser(firebaseUtilities.getDatabaseReferenceCurrentUser());
    }

    public LiveData<User> getLiveDataUser() {
        return firebaseLiveDataUser;
    }
}
