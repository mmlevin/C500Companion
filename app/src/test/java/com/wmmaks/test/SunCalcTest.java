package com.wmmaks.test;

import com.wmmaks.utils.SunCalc;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by mmlevin on 10.04.2017.
 */

public class SunCalcTest {
    @Test
    public void test() {
        Calendar date = Calendar.getInstance();
        SunCalc calc = new SunCalc();
        SunCalc.Time time = calc.getTimes(date.getTimeInMillis(), 51.685323, 39.172993);
        date.setTimeInMillis((long)calc.times[1].riseTime);
        String timestr = date.getTime().toString();
    }
}
