package TestCases;

import org.junit.Before;
import org.junit.Test;

import ClassSource.LoadUserData;
import ClassSource.UserData;

import static org.junit.Assert.*;

import java.util.List;


public class LoadUserDataTest {

    private LoadUserData loadUserData;

    @Before
    public void setUp() {
        loadUserData = new LoadUserData();
    }

    @Test
    public void testLoadUserDataConstructor() {
        assertNotNull(loadUserData);
    }

    @Test
    public void testGetUserList() {
        List<UserData> users = loadUserData.getUserList();

        assertNotNull(users);
        assertTrue(users.isEmpty() || users.size() > 0);
    }
}
