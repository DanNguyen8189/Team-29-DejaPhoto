package com.team29.cse110.team29dejaphoto;

import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.models.DisplayCycleMediator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by RobertChance on 6/9/17.
 */
public class ReleaseSingleUserTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    PhotoLoader photoLoader;
    private DisplayCycleMediator displayCycleMediator;
    private SharedPreferences sp;
    DejaPhoto[] gallery;

    @Before
    public void setUp() {
        displayCycleMediator = new DisplayCycleMediator();
        sp = main.getActivity().getSharedPreferences("Deja_Preferences",0);

        displayCycleMediator.addToCycle(gallery);
    }

    // There are no released photos yet
    @Test
    public void releasePhoto() throws Exception {

        assertNull(displayCycleMediator.getNextPhoto());
    }

}