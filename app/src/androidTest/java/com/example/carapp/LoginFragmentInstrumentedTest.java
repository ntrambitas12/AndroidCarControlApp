package com.example.carapp;

import static android.app.PendingIntent.getActivity;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;


import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.carapp.Fragments.LoginFragment;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)

public class LoginFragmentInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityRule
            = new ActivityScenarioRule<>(LoginActivity.class);


    @Before

    public void launchFragment() {

        // Grant the permissions
        onView(ViewMatchers.withId(R.id.requestPermissions)).perform(ViewActions.click());
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
            while (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                }
            }
        }
    }

    @Test
    public void testLoginEmpty() {

            // Click the login button
            onView(ViewMatchers.withId(R.id.login)).perform(ViewActions.click());

            //Check for toast
            onView(withText(R.string.errEmptyEmailPass))
    .check(matches(isDisplayed()));

    }

    @Test
    public void testEmailTextType() {
        // Type wrong credentials
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("ERR_EMAIL"));
//        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("ERR"));

        // Check Wrong email was typed
        onView(ViewMatchers.withId(R.id.email)).check(matches(withText("ERR_EMAIL")));


    }

    @Test
    public void testSignUpButtonVisible() {
        // Check that the sign up button is visible
        onView(withId(R.id.SignUp)).check(matches(isDisplayed()));
    }

}