package DisplayCycleTests;

import android.support.test.rule.ActivityTestRule;

import com.team29.cse110.team29dejaphoto.DejaPhoto;
import com.team29.cse110.team29dejaphoto.DisplayCycle;
import com.team29.cse110.team29dejaphoto.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;


/**
 * Created by David Duplantier and Noah Lovato on 5/7/17.
 */

public class TimeTests {

    private Calendar morning = new GregorianCalendar();
    private Calendar noon = new GregorianCalendar();
    private Calendar midnight = new GregorianCalendar();
    private Calendar sundayMorning = new GregorianCalendar();

    private static final int EARLY_YEAR = 2001;
    private static final int THIS_YEAR = 2017;

    private static final int JANUARY = 0;
    private static final int JULY = 6;

    private static final int DATE_12 = 12;
    private static final int DATE_21 = 21;

    private static final int HOUR_16 = 16;
    private static final int HOUR_7 = 7;

    private DejaPhoto photo1;
    private DejaPhoto photo2;
    private DejaPhoto photo3;


    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {

        morning.set(EARLY_YEAR, JULY, DATE_12, HOUR_7, 01);
        noon.set(THIS_YEAR, JANUARY, DATE_21, 13, 30);
        midnight.set(THIS_YEAR,4,7,HOUR_16,30);

        photo1 = new DejaPhoto(null,0,0,0l);
        photo1.setTime(morning);
        photo2 = new DejaPhoto(null,0,0,0l);
        photo2.setTime(noon);
        photo3 = new DejaPhoto(null,0,0,0l);
        photo3.setTime(midnight);

        /*photo1.updateScore(true,true,true);
        photo2.updateScore(true, true, true);
        photo3.updateScore(true, true, true);*/
    }


    /*@Test
    public void testFields() {
        photo3.updateScore(true, true, true);
        assertEquals(2017, photo3.getTime().get(Calendar.YEAR));
    }*/

    @Test
    public void testHour()
    {
        assertEquals("Photo1 Test", 0, photo1.getScore());

        assertEquals("Photo2 Test", 0, photo2.getScore());

        assertEquals("Photo3 Test", 20, photo3.getScore());
    }
}
