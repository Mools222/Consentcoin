package com.thomosim.consentcoin.Testing;

import android.content.Context;
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
import com.thomosim.consentcoin.Persistence.Consentcoin;
import com.thomosim.consentcoin.Persistence.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.PermissionRequest;
import com.thomosim.consentcoin.Persistence.User;

import java.io.File;
import java.io.ObjectOutputStream;

public class FirebaseUtilities {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferencePermissionRequests;
    private DatabaseReference databaseReferenceConsentcoinReferences;
    private DatabaseReference databaseReferenceInviteRequests;
    private StorageReference storageReference;

    private static final Object LOCK = new Object();
    private static FirebaseUtilities instance;

    public static FirebaseUtilities getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new FirebaseUtilities();
            }
        }
        return instance;
    }

    public FirebaseUtilities() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferencePermissionRequests = FirebaseDatabase.getInstance().getReference().child("PermissionRequests");
        databaseReferenceConsentcoinReferences = FirebaseDatabase.getInstance().getReference().child("ConsentcoinReferences");
        databaseReferenceInviteRequests = FirebaseDatabase.getInstance().getReference().child("InviteRequests");
        storageReference = FirebaseStorage.getInstance().getReference().child("consentcoins");
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReferenceCurrentUser() {
        return databaseReferenceUsers.child(firebaseAuth.getCurrentUser().getUid());
    }

    public DatabaseReference getDatabaseReferenceUsers() {
        return databaseReferenceUsers;
    }

    public DatabaseReference getDatabaseReferencePermissionRequests() {
        return databaseReferencePermissionRequests;
    }

    public DatabaseReference getDatabaseReferenceConsentcoinReferences() {
        return databaseReferenceConsentcoinReferences;
    }

    public DatabaseReference getDatabaseReferenceInviteRequests() {
        return databaseReferenceInviteRequests;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void signOut(Context context) {
        AuthUI.getInstance().signOut(context);
    }

//    public void addUser(String userType, String uid, String userEmail, String userDisplayName) {
//        User user = new User(uid, userEmail, userType, "FirstName", null, "LastName", null);
//        if (userDisplayName != null) {
//            String[] userNameSplit = userDisplayName.split("\\s");
//            if (userNameSplit.length == 2)
//                user = new User(uid, userEmail, userType, userNameSplit[0], null, userNameSplit[1], null);
//            else if (userNameSplit.length == 3)
//                user = new User(uid, userEmail, userType, userNameSplit[0], userNameSplit[1], userNameSplit[2], null);
//        }
//        databaseReferenceUsers.child(uid).setValue(user);
//    }
//
//    public void updateUser(String uid, User user) {
//        databaseReferenceUsers.child(uid).setValue(user);
//    }
//
//    public void addPermissionRequest(String organizationEmail, String memberEmail, String permissionType) {
//        DatabaseReference databaseReference = databaseReferencePermissionRequests.push(); // Creates blank record in the database
//        String firebaseId = databaseReference.getKey(); // Get the auto generated key
//        PermissionRequest permissionRequest = new PermissionRequest(firebaseId, organizationEmail, memberEmail, permissionType);
//        databaseReference.setValue(permissionRequest);
//    }
//
//    public void removePermissionRequest(String id) {
//        databaseReferencePermissionRequests.child(id).removeValue(); // Remove the permission request from the database
//    }
//
//    /**
//     * This method:
//     * 1) Creates a new instance of the Consentcoin class
//     * 2) Creates a new file in the phone's internal storage called "contract.dat". The getFilesDir method returns the appropriate internal directory for the app
//     * 3) Opens a new ObjectOutputStream object and writes the Consentcoin object to the file. The ObjectOutputStream is then closed.
//     * The openFileOutput method opens a private file associated with this Context's application package for writing. By passing the argument Context.MODE_PRIVATE, the created file can only be accessed by the calling application.
//     * 4) A new StorageReference object is created. The ensures that the file is saved in the "consentcoins" folder in Firebase Storage under the file name "[contractId].dat"
//     * 5) The file is persisted via the putFile method, which requires a URI object. The static fromFile method from the Uri class creates a URI from the File object.
//     * 6) The putFile method returns a UploadTask object. Using the addOnSuccessListener method, an OnSuccessListener is added to the UploadTask object.
//     * This is accomplished by creating an anonymous inner class, which implements the onSuccess method. Since OnSuccessListener is a generic class, the formal generic type
//     * "TResult" is replaced by the inner class TaskSnapshot, which is found in the UploadTask class.
//     * 7) A new Task object is created. The formal generic type "TResult" is replaced by the Uri class. Since TaskSnapshot extends the StorageTask class, it inherits the getStorage method.
//     * The method returns a StorageReference object upon which the getDownloadUrl method is called. This method returns a Task<Uri> object. The getDownloadUrl method is called on the
//     * Task<Uri> object. This method returns a Task<Uri> containing the the download URL for the persisted file.
//     * 8) A while loop with an empty body is created to keep the thread waiting until the getDownloadUrl method is successful.
//     * 9) A new Uri object is created by calling the getResult method on the Task<Uri> object.
//     * 10) A new ConsentcoinReference object is created using the contract ID, member ID and organization ID of the Consentcoin object and the download URL.
//     * 11) The ConsentcoinReference object is persisted to the Firebase Realtime Database using the push and setValue methods.
//     * 12) Finally the contract.dat file is deleted from the storage of the phone.
//     */
//
//    public void addConsentcoin(Context context, String id, String contractType, String organization, String member) {
//        // TODO (2) Encrypt the Consentcoin object
//        final Consentcoin consentcoin = new Consentcoin(id, contractType, member, organization);
//
//        String fileName = "consentcoin";
//        final File file = new File(context.getFilesDir(), fileName);
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
//            objectOutputStream.writeObject(consentcoin);
//            objectOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        StorageReference storageReference = this.storageReference.child(id);
//
//        storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
//                while (!urlTask.isSuccessful()) ;
//                Uri downloadUrl = urlTask.getResult();
//
//                ConsentcoinReference consentcoinReference = new ConsentcoinReference(consentcoin.getContractId(), consentcoin.getMemberId(), consentcoin.getOrganizationId(), downloadUrl.toString());
//                databaseReferenceConsentcoinReferences.push().setValue(consentcoinReference);
//
//                file.delete();
//
//            }
//        });
//    }
//
//    public void addInviteRequest() {
//
//    }
//
//    public void removeInviteRequest(String id) {
//        databaseReferenceInviteRequests.child(id).removeValue();
//    }
}
