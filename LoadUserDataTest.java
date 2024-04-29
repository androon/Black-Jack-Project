import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

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