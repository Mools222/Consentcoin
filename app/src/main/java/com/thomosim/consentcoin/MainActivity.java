package com.thomosim.consentcoin;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thomosim.consentcoin.Persistens.Contract;
import com.thomosim.consentcoin.Persistens.ContractReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReferenceTest;
    private DatabaseReference databaseReferenceContractReferences;
    private ChildEventListener childEventListenerTest;
    private ChildEventListener childEventListenerContractReferences;
    private TextInputEditText textInputEditText;
    private TextView textView1;
    private TextView textView2;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ArrayList<ContractReference> contractReferences;
    private ArrayList<Contract> contracts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        textInputEditText = findViewById(R.id.textInputEditText);
        textView2 = findViewById(R.id.textView2);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("contracts");

        contractReferences = new ArrayList<>();
        contracts = new ArrayList<>();

        databaseReferenceTest = FirebaseDatabase.getInstance().getReference().child("test");
        databaseReferenceContractReferences = FirebaseDatabase.getInstance().getReference().child("ContractReferences");
        read();
    }

    public void write(View view) {
        String test = textInputEditText.getText().toString();
        databaseReferenceTest.push().setValue(test);
        textInputEditText.setText("");
    }

    public void read() {
        childEventListenerTest = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                textView1.append("Key: " + dataSnapshot.getKey() + "   Value: " + dataSnapshot.getValue() + "\n");
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReferenceTest.addChildEventListener(childEventListenerTest);

        childEventListenerContractReferences = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ContractReference contractReference = dataSnapshot.getValue(ContractReference.class);
                contractReferences.add(contractReference);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReferenceContractReferences.addChildEventListener(childEventListenerContractReferences);
    }

    /**
     * This method:
     * 1) Creates a new instance of the Contract class
     * 2) Creates a new file in the phone's internal storage called "contract.dat". The getFilesDir method returns the appropriate internal directory for the app
     * 3) Open a new ObjectOutputStream object and writes the Contract object to the file. The ObjectOutputStream is then closed.
     * 4) A new StorageReference object is created. The ensures that the file is saved in the "contracts" folder in Firebase Storage under the file name "[contractId].dat"
     * 5) The file is persisted via the putFile method, which requires a URI object. The static fromFile method from the Uri class creates a URI from the File object.
     * 6) The putFile method returns a UploadTask object. Using the addOnSuccessListener method, an OnSuccessListener is added to the UploadTask object.
     * This is accomplished by creating an anonymous inner class, which implements the onSuccess method. Since OnSuccessListener is a generic class, the formal generic type
     * "TResult" is replaced by the inner class TaskSnapshot, which is found in the UploadTask class.
     * 7) A new Task object is created. The formal generic type "TResult" is replaced by the Uri class. Since TaskSnapshot extends the StorageTask class, it inherits the getStorage method.
     * The method returns a StorageReference object upon which the getDownloadUrl method is called. This method returns a Task<Uri> object. The getDownloadUrl method is called on the
     * Task<Uri> object. This method returns a Task<Uri> containing the the download URL for the persisted file.
     * 8) A while loop with an empty body is created to keep the thread waiting until the getDownloadUrl method is successful.
     * 9) A new Uri object is created by calling the getResult method on the Task<Uri> object.
     * 10) A new ContractReference object is created using the contract ID, member ID and organization ID of the Contract object and the download URL.
     * 11) The ContractReference object is persisted to the Firebase Realtime Database using the push and setValue methods.
     */

    public void writeObject(View view) {
        // TODO (2) Encrypt the Contract object
        final Contract contract = new Contract("2", "Type 1", "TestOrg", "TestMember");

        String fileName = "contract.dat";
        File file = new File(getFilesDir(), fileName);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(openFileOutput(fileName, Context.MODE_PRIVATE));
            objectOutputStream.writeObject(contract);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StorageReference storageReference = this.storageReference.child(contract.getContractId() + ".dat");

        storageReference.putFile(Uri.fromFile(file)).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();

                ContractReference contractReference = new ContractReference(contract.getContractId(), contract.getMemberId(), contract.getOrganizationId(), downloadUrl.toString());
                databaseReferenceContractReferences.push().setValue(contractReference);
            }
        });
    }

    /**
     * This method:
     * 1)
     */

    public void readObject(View view) {
        textView2.setText("Loading...");

        if (contractReferences.size() > 0) {
            try {
                URL[] urls = new URL[contractReferences.size()];

                for (int i = 0; i < contractReferences.size(); i++) {
                    urls[i] = new URL(contractReferences.get(i).getStorageUrl());
                }

                new DownloadObjects().execute(urls);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    // To solve the "leaks might occur" warning: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur/46166223#46166223
    private class DownloadObjects extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            try {
                ObjectInputStream objectInputStream = null;
                for (int i = 0; i < urls.length; i++) {
                    objectInputStream = new ObjectInputStream(new BufferedInputStream(urls[i].openStream()));
                    // TODO (3) Decrypt the Contract object
                    Contract contract = (Contract) objectInputStream.readObject();
                    contracts.add(contract);
                }
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            textView2.setText("");
            for (Contract contract: contracts) {
                textView2.append("ID: " + contract.getContractId() + " Type: " + contract.getContractType() + " MemID: " + contract.getMemberId() + " OrgID: " + contract.getOrganizationId() + "\n");
            }
        }
    }


}
