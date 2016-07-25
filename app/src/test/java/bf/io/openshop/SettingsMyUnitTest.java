package bf.io.openshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.User;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Simple unit tests for {@link SettingsMy} class.
 * <p/>
 * Careful: Because it is testing static class with static methods and static fields.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SettingsMy.class, MsgUtils.class})
public class SettingsMyUnitTest {

    @Mock
    SharedPreferences mockSharedPreferences;

    @Mock
    SharedPreferences.Editor mockEditor;

    @Mock
    Activity mockActivity;

    @Mock
    Context mockContext;

    @Before
    public void preparation() throws Exception {
        MockitoAnnotations.initMocks(this); // for case them used another runner
        PowerMockito.spy(SettingsMy.class);
        PowerMockito.doReturn(mockSharedPreferences).when(SettingsMy.class, "getSettings");

        Whitebox.setInternalState(SettingsMy.class, "actualShop", (Object[]) null);
        Whitebox.setInternalState(SettingsMy.class, "activeUser", (Object[]) null);
    }

    @Test
    public void getActualShopTest() throws Exception {
        // Mock persistence response from shared preferences
        Shop testShop = new Shop();
        testShop.setName("testShop");
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTUAL_SHOP, "")).thenReturn(Utils.getGsonParser().toJson(testShop));

        // Check if value is returned
        assertEquals(SettingsMy.getActualShop(), testShop);
    }

    @Test
    public void getActualShopNullTest() throws Exception {
        // Mock persistence response from shared preferences
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTUAL_SHOP, "")).thenReturn("");

        // Check if value is returned
        assertEquals(SettingsMy.getActualShop(), null);
    }

    @Test
    public void setAndGetActualShopTest() throws Exception {
        // Sample object
        Shop testShop = new Shop();
        testShop.setName("testShop");

        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Set actual shop and check method calls
        SettingsMy.setActualShop(testShop);
        verify(mockEditor, times(1)).putString(SettingsMy.PREF_ACTUAL_SHOP, Utils.getGsonParser().toJson(testShop));
        verify(mockEditor, times(1)).apply();

        // Check returned value and loading that value from filed not a shared preferences.
        assertEquals(SettingsMy.getActualShop(), testShop);
        verify(mockSharedPreferences, never()).getString(anyString(), anyString());
    }

    @Test
    public void setAndGetActualShopNullTest() throws Exception {
        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Set actual shop and check method calls
        SettingsMy.setActualShop(null);
        verify(mockSharedPreferences, times(1)).edit();
        verify(mockEditor, times(1)).putString(SettingsMy.PREF_ACTUAL_SHOP, Utils.getGsonParser().toJson(null));
        verify(mockEditor, times(1)).apply();

        // Check returned value and loading that value from filed not a shared preferences.
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTUAL_SHOP, "")).thenReturn("null");
        assertEquals(SettingsMy.getActualShop(), null);
        verify(mockSharedPreferences, times(1)).getString(anyString(), anyString());
        verifyNoMoreInteractions(mockSharedPreferences);
    }

    @Test
    public void getActualNonNullShopErrorTest() throws Exception {
        // Mock static methods
        PowerMockito.mockStatic(MsgUtils.class);
        // Two line mocking.
        doNothing().when(MsgUtils.class);
        MsgUtils.showToast(eq(mockActivity), anyInt(), anyString(), eq(MsgUtils.ToastLength.LONG));

        // Prepare responses
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTUAL_SHOP, "")).thenReturn("");
        when(mockActivity.getApplicationContext()).thenReturn(mockContext);

        // Check return value
        assertEquals(SettingsMy.getActualNonNullShop(mockActivity), new Shop());

        // Verify calls
        verify(mockSharedPreferences, times(1)).getString(anyString(), anyString());
        verify(mockActivity, times(1)).startActivity((Intent) anyObject());

        // Verify static void methods. Two line verifying.
        verifyStatic(times(1));
        MsgUtils.showToast(mockActivity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
    }

    @Test
    public void getActualNonNullShopOkTest() throws Exception {
        // Sample object
        Shop testShop = new Shop();
        testShop.setName("testShop");

        // Prepare responses
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTUAL_SHOP, "")).thenReturn(Utils.getGsonParser().toJson(testShop));

        // Check return value
        assertEquals(SettingsMy.getActualNonNullShop(mockActivity), testShop);

        // Verify calls
        verify(mockActivity, never()).startActivity((Intent) anyObject());
    }

    @Test
    public void getActiveUserTest() throws Exception {
        // Mock persistence response from shared preferences
        User testUser = new User();
        testUser.setName("testUser");
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTIVE_USER, "")).thenReturn(Utils.getGsonParser().toJson(testUser));

        // Check if value is returned
        assertEquals(SettingsMy.getActiveUser(), testUser);
    }

    @Test
    public void getActiveUserNullTest() throws Exception {
        // Mock persistence response from shared preferences
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTIVE_USER, "")).thenReturn("");

        // Check if value is returned
        assertEquals(SettingsMy.getActiveUser(), null);
    }

    @Test
    public void setAndGetActiveUserTest() throws Exception {
        // Sample object
        User testUser = new User();
        testUser.setName("testUser");

        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Invoke test method and check method calls
        SettingsMy.setActiveUser(testUser);
        verify(mockEditor, times(1)).putString(SettingsMy.PREF_ACTIVE_USER, Utils.getGsonParser().toJson(testUser));
        verify(mockEditor, times(1)).apply();

        // Check returned value and loading that value from filed not a shared preferences.
        assertEquals(SettingsMy.getActiveUser(), testUser);
        verify(mockSharedPreferences, never()).getString(anyString(), anyString());
    }

    @Test
    public void setAndGetActiveUserNullTest() throws Exception {
        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Invoke test method and check method call
        SettingsMy.setActiveUser(null);
        verify(mockSharedPreferences, times(1)).edit();
        verify(mockEditor, times(1)).putString(SettingsMy.PREF_ACTIVE_USER, Utils.getGsonParser().toJson(null));
        verify(mockEditor, times(1)).apply();

        // Check returned value
        when(mockSharedPreferences.getString(SettingsMy.PREF_ACTIVE_USER, "")).thenReturn("null");
        assertEquals(SettingsMy.getActiveUser(), null);
        verify(mockSharedPreferences, times(1)).getString(anyString(), anyString());
        verifyNoMoreInteractions(mockSharedPreferences);
    }

    @Test
    public void getUserEmailHintTest() throws Exception {
        // Mock persistence response from shared preferences
        String testEmailHint = "test.email@gmail.com";
        when(mockSharedPreferences.getString(SettingsMy.PREF_USER_EMAIL, "")).thenReturn(testEmailHint);

        // Check if value is returned
        assertEquals(SettingsMy.getUserEmailHint(), testEmailHint);
    }

    @Test
    public void setUserEmailHintTest() throws Exception {
        // Sample object
        String testEmailHint = "test.email@gmail.com";

        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Invoke test method and check method call
        SettingsMy.setUserEmailHint(testEmailHint);
        verify(mockEditor, times(1)).putString(SettingsMy.PREF_USER_EMAIL, testEmailHint);
        verify(mockEditor, times(1)).commit();
    }

    @Test
    public void getTokenSentToServerTest() throws Exception {
        // Mock persistence response from shared preferences
        when(mockSharedPreferences.getBoolean(SettingsMy.SENT_TOKEN_TO_SERVER, false)).thenReturn(true);

        // Check if value is returned
        assertEquals(SettingsMy.getTokenSentToServer(), true);
    }

    @Test
    public void setTokenSentToServerTest() throws Exception {
        // Mock preferences editor
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Invoke test method and check method call
        SettingsMy.setTokenSentToServer(true);
        verify(mockEditor, times(1)).putBoolean(SettingsMy.SENT_TOKEN_TO_SERVER, true);

        SettingsMy.setTokenSentToServer(false);
        verify(mockEditor, times(1)).putBoolean(SettingsMy.SENT_TOKEN_TO_SERVER, false);
        verify(mockEditor, times(2)).commit();
    }
}
