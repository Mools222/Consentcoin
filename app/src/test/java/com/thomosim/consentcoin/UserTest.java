package com.thomosim.consentcoin;

import com.thomosim.consentcoin.Persistence.ModelClass.Exceptions.ProfanityException;
import com.thomosim.consentcoin.Persistence.ModelClass.User;

import org.junit.Assert;
import org.junit.Test;


public class UserTest {
    @Test
    public void profanityInOrgName() {
        boolean caught = false;
        String orgName = "asshole";
        User d = new User();
        try {
            d.setOrganizationName(orgName);
        } catch (ProfanityException e) {
            caught = true;
        }
        Assert.assertTrue(caught);
        //To check if the name get set anyway
        Assert.assertTrue(d.getOrganizationName() == null);
    }

    @Test
    public void profanityInOrgName2() {
        boolean caught = false;
        String orgName = "shit";
        User d = new User();
        try {
            d.setOrganizationName(orgName);
        } catch (ProfanityException e) {
            caught = true;
        }
        Assert.assertTrue(caught);
        //To check if the name get set anyway
        Assert.assertTrue(d.getOrganizationName() == null);
    }

    @Test
    public void noProfanityInOrg() {
        boolean caught = false;
        String orgName = "myOrg";
        User d = new User();
        try {
            d.setOrganizationName(orgName);
        } catch (ProfanityException e) {
            caught = true;
        }
        Assert.assertFalse(caught);
        //To check if the name gets set
        Assert.assertTrue(d.getOrganizationName() == orgName);
    }
}