package com.thomosim.consentcoin.Persistence;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thomosim.consentcoin.ObserverPattern.MyObservable;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractScopeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractTypeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.User;
import com.thomosim.consentcoin.Persistence.ModelClass.UserActivity;
import com.thomosim.consentcoin.R;

import java.io.File;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DAOFirebase implements DAOInterface {
    private AuthUI authUI;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceConsentcoinReferences;
    private DatabaseReference databaseReferenceInviteRequests;
    private StorageReference storageReference;

    private ObservableDataFirebaseAuth observableDataFirebaseAuth;
    private ObservableDataUser observableDataUser;
    private ObservableDataUsers observableDataUsers;
    private ObservableDataPermissionRequests observableDataPermissionRequests;
    private ObservableDataConsentcoinReferences observableDataConsentcoinReferences;
    private ObservableDataInviteRequests observableDataInviteRequests;
    private ObservableDataConsentcoin observableDataConsentcoin;

    public DAOFirebase() {
        authUI = AuthUI.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceConsentcoinReferences = FirebaseDatabase.getInstance().getReference().child("ConsentcoinReferences");
        databaseReferenceInviteRequests = FirebaseDatabase.getInstance().getReference().child("InviteRequests");
        storageReference = FirebaseStorage.getInstance().getReference().child("consentcoins");

        observableDataFirebaseAuth = new ObservableDataFirebaseAuth(firebaseAuth);
        observableDataUser = new ObservableDataUser();
        observableDataUsers = new ObservableDataUsers(databaseReferenceUsers);
        observableDataPermissionRequests = new ObservableDataPermissionRequests(databaseReferencePermissionRequests);
        observableDataConsentcoinReferences = new ObservableDataConsentcoinReferences(databaseReferenceConsentcoinReferences);
        observableDataInviteRequests = new ObservableDataInviteRequests(databaseReferenceInviteRequests);
        observableDataConsentcoin = new ObservableDataConsentcoin();
    }

    @Override
    public void addAuthStateListener() {
        observableDataFirebaseAuth.addAuthStateListener();
    }

    @Override
    public void removeAuthStateListener() {
        observableDataFirebaseAuth.removeAuthStateListener();
    }

    @Override
    public void setDatabaseReferenceCurrentUser() {
        observableDataUser.setDatabaseReference(databaseReferenceUsers.child(firebaseAuth.getCurrentUser().getUid()));
    }

    @Override
    public void addDatabaseListenerUser() {
        observableDataUser.addDatabaseListener();
    }

    @Override
    public void addDatabaseListener() {
        observableDataUsers.addDatabaseListener();
        observableDataPermissionRequests.addDatabaseListener();
        observableDataConsentcoinReferences.addDatabaseListener();
        observableDataInviteRequests.addDatabaseListener();
    }

    @Override
    public void removeDatabaseListener() {
        observableDataUser.removeDatabaseListener();
        observableDataUsers.removeDatabaseListener();
        observableDataPermissionRequests.removeDatabaseListener();
        observableDataConsentcoinReferences.removeDatabaseListener();
        observableDataInviteRequests.removeDatabaseListener();
    }

    @Override
    public Intent getSignInIntent() {
        return authUI
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(true) // Doesn't seem to do anything
                .setTheme(R.style.LightTheme)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build())) // Additional sign-in providers can be added here. See: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
                .build();
    }

    @Override
    public void signOut(Context context) {
        authUI.signOut(context);
    }

    @Override
    public void addAuthentication() {
    }

    @Override
    public <T extends MyObservable> T getAuthentication() {
        return (T) observableDataFirebaseAuth;
    }

    @Override
    public <T> void updateAuthentication(String id, T t) {
    }

    @Override
    public <T> void removeAuthentication(T t) {
    }

    @Override
    public void addUser(String userType, String uid, String userEmail, String userDisplayName, String organizationName) {
        User user = new User(uid, userEmail, userType, "FirstName", null, "LastName", organizationName.length() == 0 ? null : organizationName, null, null);
        if (userDisplayName != null) {
            String[] userNameSplit = userDisplayName.trim().split("\\s");
            if (userNameSplit.length == 2)
                user = new User(uid, userEmail, userType, userNameSplit[0], null, userNameSplit[1], organizationName.length() == 0 ? null : organizationName, null, null);
            else if (userNameSplit.length == 3)
                user = new User(uid, userEmail, userType, userNameSplit[0], userNameSplit[1], userNameSplit[2], organizationName.length() == 0 ? null : organizationName, null, null);
            else if (userNameSplit.length > 3) {
                String middleNames = "";
                for (int i = 1; i < userNameSplit.length - 1; i++)
                    middleNames += userNameSplit[i] + (i == userNameSplit.length - 2 ? "" : " ");
                user = new User(uid, userEmail, userType, userNameSplit[0], middleNames, userNameSplit[userNameSplit.length - 1], organizationName.length() == 0 ? null : organizationName, null, null);
            }
        }
        ArrayList<UserActivity> userActivities = new ArrayList<>();
        userActivities.add(0, new UserActivity("UC", userDisplayName, organizationName.length() == 0 ? null : organizationName, new Date())); // Create a "UC" (User Created) UserActivity and add it to the new user
        user.setUserActivities(userActivities);
        databaseReferenceUsers.child(uid).setValue(user);
    }

    @Override
    public MyObservable<User> getUser() {
        return observableDataUser;
    }

    @Override
    public MyObservable<ArrayList<User>> getUsers() {
        return observableDataUsers;
    }


    @Override
    public void updateUser(String uid, User user) {
        databaseReferenceUsers.child(uid).setValue(user);
    }

    @Override
    public void removeUser(User user) {
    }

    @Override
    public void addPermissionRequest(String organizationName, String organizationUid, String memberName, String memberUid, ContractTypeEnum permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate, ContractScopeEnum personsIncluded) {
        DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
        String firebaseId = databaseReference.getKey(); // Get the auto generated key
        PermissionRequest permissionRequest = new PermissionRequest(firebaseId, organizationName, organizationUid, memberName, memberUid, permissionType, creationDate, permissionStartDate, permissionEndDate, personsIncluded);
        databaseReference.setValue(permissionRequest);
    }

    @Override
    public MyObservable<ArrayList<PermissionRequest>> getPermissionRequests() {
        return observableDataPermissionRequests;
    }

    @Override
    public void updatePermissionRequest(String id, PermissionRequest permissionRequest) {
    }


    @Override
    public void removePermissionRequest(String id) {
        databaseReferencePermissionRequests.child(id).removeValue(); // Remove the permission request from the database
    }

    @Override
    public MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences() {
        return observableDataConsentcoinReferences;
    }

    @Override
    public void addConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl) {
        DatabaseReference databaseReference = databaseReferenceConsentcoinReferences.push(); // Creates blank record in the database
        String firebaseId = databaseReference.getKey(); // Get the auto generated key
        ConsentcoinReference consentcoinReference = new ConsentcoinReference(firebaseId, contractId, memberUid, organizationUid, storageUrl);
        databaseReference.setValue(consentcoinReference);
    }

    @Override
    public void updateConsentcoinReference(String id, ConsentcoinReference consentcoinReference) {
        databaseReferenceConsentcoinReferences.child(id).setValue(consentcoinReference);
    }

    @Override
    public void removeConsentcoinReference(ConsentcoinReference consentcoinReference) {
    }

    /**
     * This method:
     * 1) Creates a new instance of the Consentcoin class
     * 2) Creates a new File object pointing to the phone's internal storage with the file name "consentcoin". The getFilesDir method returns the appropriate internal directory for the app
     * 3) Opens a new ObjectOutputStream object, creates the file and writes the Consentcoin object to the file. The ObjectOutputStream is then closed.
     * The openFileOutput method opens a private file associated with this Context's application package for writing. By passing the argument Context.MODE_PRIVATE, the created file can only be accessed by the calling application.
     * 4) A new StorageReference object is created. The ensures that the file is saved in the "consentcoins" folder in Firebase Storage under the file name "[contractId]"
     * 5) The file is persisted via the putFile method, which requires a URI object. The static fromFile method from the Uri class creates a URI from the File object.
     * 6) The putFile method returns an UploadTask object. Using the addOnSuccessListener method, an OnSuccessListener is added to the UploadTask object.
     * This is accomplished by creating an anonymous inner class, which implements the onSuccess method. Since OnSuccessListener is a generic class, the formal generic type
     * "TResult" is replaced by the inner class TaskSnapshot, which is found in the UploadTask class.
     * 7) A new Task object is created. The formal generic type "TResult" is replaced by the Uri class. Since TaskSnapshot extends the StorageTask class, it inherits the getStorage method.
     * The getStorage method returns a StorageReference object upon which the getDownloadUrl method is called. This method returns a Task<Uri> containing the the download URL for the persisted file.
     * 8) A while loop with an empty body is created to keep the thread waiting until the getDownloadUrl method is successful.
     * 9) A new Uri object is created by calling the getResult method on the Task<Uri> object.
     * 10) A new ConsentcoinReference object is created using the contract ID, member ID and organization ID of the Consentcoin object and the download URL.
     * 11) The ConsentcoinReference object is persisted to the Firebase Realtime Database via the addConsentcoinReference method.
     * 12) Finally the file is deleted from the storage of the phone.
     */

    @Override
    public void addConsentcoin(Context context, String contractId, ContractTypeEnum permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate, String personsIncluded) {
        // TODO Encrypt the Consentcoin object
        final Consentcoin consentcoin = new Consentcoin(contractId, permissionType, organizationUid, memberUid, creationDate, permissionStartDate, permissionEndDate, personsIncluded);

        String fileName = "consentcoin";
        final File FILE = new File(context.getFilesDir(), fileName);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            objectOutputStream.writeObject(consentcoin);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference storageReferenceChild = storageReference.child(contractId);
        storageReferenceChild.putFile(Uri.fromFile(FILE)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                addConsentcoinReference(consentcoin.getContractId(), consentcoin.getMemberUid(), consentcoin.getOrganizationUid(), downloadUrl.toString());
                FILE.delete();
            }
        });
    }

    @Override
    public void setConsentcoinUrl(String storageUrl) {
        observableDataConsentcoin.setConsentcoinUrl(storageUrl);
    }

    @Override
    public MyObservable<Consentcoin> getConsentcoin() {
        return observableDataConsentcoin;
    }

    @Override
    public MyObservable<ArrayList<Consentcoin>> getConsentcoins() {
        return null;
    }

    @Override
    public void removeConsentcoin(Consentcoin consentcoin) {
        storageReference.child(consentcoin.getContractId()).delete();
    }

    @Override
    public MyObservable<ArrayList<InviteRequest>> getInviteRequests() {
        return observableDataInviteRequests;
    }

    @Override
    public void addInviteRequest(ArrayList<String> members, String organizationName, String organizationUID) {
        for (String email : members) {
            DatabaseReference inviteRequestDatabaseReference = databaseReferenceInviteRequests.push();
            String inviteID = inviteRequestDatabaseReference.getKey();
            InviteRequest inviteRequest = new InviteRequest(inviteID, organizationName, organizationUID, email);
            inviteRequestDatabaseReference.setValue(inviteRequest);
        }
    }

    @Override
    public void removeInviteRequest(String id) {
        databaseReferenceInviteRequests.child(id).removeValue();
    }
}
