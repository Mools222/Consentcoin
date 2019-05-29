package com.thomosim.consentcoin.Persistence;

import android.content.Context;
import android.content.Intent;

import com.thomosim.consentcoin.ObserverPattern.MyObservable;
import com.thomosim.consentcoin.Persistence.ModelClass.Consentcoin;
import com.thomosim.consentcoin.Persistence.ModelClass.ConsentcoinReference;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractScopeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.ContractTypeEnum;
import com.thomosim.consentcoin.Persistence.ModelClass.InviteRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;
import com.thomosim.consentcoin.Persistence.ModelClass.User;

import java.util.ArrayList;
import java.util.Date;
/**
 * The DAOinterface is made to make it easy to change from one Database to another,
 * and to ensure all databases have same methods
 */
public interface DAOInterface {

    /**
     * This method creates an AuthStateListener on the database
     */
    void addAuthStateListener();

    /**
     * This method removes an AuthStateListener from the database
     */
    void removeAuthStateListener();

    /**
     * This method sets the Reference to the CurrentUser of the database
     */
    void setDatabaseReferenceCurrentUser();

    /**
     * This method adds a Database listener to the User Reference
     */
    void addDatabaseListenerUser();

    /**
     * This method creates a listener on the database
     */
    void addDatabaseListener();

    /**
     * This method removes a listener on the database
     */
    void removeDatabaseListener();

    /**
     * This returns the signIn Intent
     */
    Intent getSignInIntent();

    /**
     * This method handles signOut from the database
     */
    void signOut(Context context);

    /**
     * This method adds an Authentication to database
     */
    void addAuthentication();

    /**
     * This method returns a authenticationObject (Generic)
     */
    <T extends MyObservable> T getAuthentication(); // Firebase authentication comes in a FirebaseAuth object. When switching DAO, we'd probably use a different object. Therefore the generic type T and raw type MyObservable is used to accommodate all possibilities.

    /**
     * This method updates the authenticationObject in the database (Generic)
     */
    <T> void updateAuthentication(String id, T t);

    /**
     * This method removes an authenticationObject from the database
     */
    <T> void removeAuthentication(T t);

    /**
     * This method adds a User to the database
     */
    void addUser(String userType, String uid, String userEmail, String userDisplayName, String organizationName);

    /**
     * This method returns a User
     */
    MyObservable<User> getUser();

    /**
     * This method returns an Arraylist of User objects
     */
    MyObservable<ArrayList<User>> getUsers();

    /**
     * This method update an existing user
     */
    void updateUser(String uid, User user);

    /**
     * This method removes a User
     */
    void removeUser(User user);

    /**
     * This method creates a PermissionRequest and store it in the database
     */
    void addPermissionRequest(String organizationName, String organizationUid, String memberName, String memberUid, ContractTypeEnum permissionType, Date creationDate, Date permissionStartDate, Date permissionEndDate, ContractScopeEnum personsIncluded);

    /**
     * This method returns an Arraylist of PermissionRequests.
     */
    MyObservable<ArrayList<PermissionRequest>> getPermissionRequests();

    /**
     * This method updates a existing PermissionRequest
     */
    void updatePermissionRequest(String id, PermissionRequest permissionRequest);

    /**
     * This method removes an existing PermissionRequest from the database
     */
    void removePermissionRequest(String id);

    /**
     * This method returns an Arraylist of ConsentcinReferences
     */
    MyObservable<ArrayList<ConsentcoinReference>> getConsentcoinReferences();

    /**
     * This method creates a ConsentcoinReference and stores it in the database
     */
    void addConsentcoinReference(String contractId, String memberUid, String organizationUid, String storageUrl);

    /**
     * This method updates existing ConsentcoinReference
     */
    void updateConsentcoinReference(String id, ConsentcoinReference consentcoinReference);

    /**
     * This method removes a consentcoinReference from the database
     */
    void removeConsentcoinReference(ConsentcoinReference consentcoinReference);

    /**
     * This method creates a Consentcoin object and stores it in the database
     */
    void addConsentcoin(Context context, String contractId, ContractTypeEnum permissionType, String organizationUid, String memberUid, Date creationDate, Date permissionStartDate, Date permissionEndDate, String personsIncluded);

    /**
     * This method sets the storageURL for the consentcoin
     */
    void setConsentcoinUrl(String storageUrl);

    /**
     * This method returns a consentcoin
     */
    MyObservable<Consentcoin> getConsentcoin();

    /**
     * This method returns an Arraylist of consentcoin objects
     */
    MyObservable<ArrayList<Consentcoin>> getConsentcoins();

    /**
     * This method removes a consentcoin from the database
     */
    void removeConsentcoin(Consentcoin consentcoin);

    /**
     * This method returns an Arraylist of InviteRequest objects
     */
    MyObservable<ArrayList<InviteRequest>> getInviteRequests();

    /**
     * This method creates an InviteRequest and stores it in the database
     */
    void addInviteRequest(ArrayList<String> members, final String organization);

    /**
     * This method removes a InviteRequest from the database.
     */
    void removeInviteRequest(String id);



























































































}
