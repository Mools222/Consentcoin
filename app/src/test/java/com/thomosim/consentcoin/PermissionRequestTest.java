package com.thomosim.consentcoin;

import com.thomosim.consentcoin.Persistence.ModelClass.Exceptions.ErroneousDatesException;
import com.thomosim.consentcoin.Persistence.ModelClass.PermissionRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.GregorianCalendar;

public class PermissionRequestTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testSetPermissionStartDate() throws ErroneousDatesException {
        GregorianCalendar startDate = new GregorianCalendar(2019, 5, 25);
        GregorianCalendar endDate = new GregorianCalendar(2001, 1, 1);

        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionEndDate(endDate.getTime());
        exception.expect(ErroneousDatesException.class); // An ErroneousDatesException is expected to be thrown by the next method
        permissionRequest.setPermissionStartDate(startDate.getTime());
    }

    @Test(expected = Test.None.class) // No exceptions are expected to be thrown
    public void testSetPermissionStartDate2() throws ErroneousDatesException {
        GregorianCalendar startDate = new GregorianCalendar(2019, 5, 25);
        GregorianCalendar endDate = new GregorianCalendar(2022, 1, 1);

        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionEndDate(endDate.getTime());
        permissionRequest.setPermissionStartDate(startDate.getTime());
    }

    @Test
    public void testSetPermissionEndDate() throws ErroneousDatesException {
        GregorianCalendar startDate = new GregorianCalendar(2019, 5, 25);
        GregorianCalendar endDate = new GregorianCalendar(2001, 1, 1);

        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionStartDate(startDate.getTime());
        exception.expect(ErroneousDatesException.class); // An ErroneousDatesException is expected to be thrown by the next method
        permissionRequest.setPermissionEndDate(endDate.getTime());
    }

    @Test(expected = Test.None.class) // No exceptions are expected to be thrown
    public void testSetPermissionEndDate2() throws ErroneousDatesException {
        GregorianCalendar startDate = new GregorianCalendar(2019, 5, 25);
        GregorianCalendar endDate = new GregorianCalendar(2022, 1, 1);

        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionStartDate(startDate.getTime());
        permissionRequest.setPermissionEndDate(endDate.getTime());
    }
}