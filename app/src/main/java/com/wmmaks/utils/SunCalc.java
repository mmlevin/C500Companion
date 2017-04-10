package com.wmmaks.utils;

/*
 (c) 2011-2015, Vladimir Agafonkin
 SunCalc is a JavaScript library for calculating sun/moon position and light phases.
 https://github.com/mourner/suncalc
*/

import java.util.Calendar;

public class SunCalc {

    private static double rad = Math.PI / 180;
    private static double e = Math.PI / 180 * 23.4397;

    // sun calculations are based on http://aa.quae.nl/en/reken/zonpositie.html formulas
    // date/time constants and conversions
    private static final int dayMs = 1000 * 60 * 60 * 24;
    private static final int J1970 = 2440588;
    private static final int J2000 = 2451545;

    double toJulian(double date) { return date / dayMs - 0.5 + J1970; };
    double fromJulian(double j)  { return (j + 0.5 - J1970) * dayMs; };
    double toDays(double date)   { return toJulian(date) - J2000; };

    private static double rightAscension(double l, double b) {
        return Math.atan2(Math.sin(l) * Math.cos(e) - Math.tan(b) * Math.sin(e), Math.cos(l));
    };

    private static double declination(double l, double b) {
        return Math.asin(Math.sin(b) * Math.cos(e) + Math.cos(b) * Math.sin(e) * Math.sin(l));
    } ;

    private static double azimuth(double H, double phi, double dec) {
        return Math.atan2(Math.sin(H), Math.cos(H) * Math.sin(phi) - Math.tan(dec) * Math.cos(phi));
    };

    private static double altitude(double H, double phi, double dec) {
        return Math.asin(Math.sin(phi) * Math.sin(dec) + Math.cos(phi) * Math.cos(dec) * Math.cos(H));
    };

    private static double siderealTime(double d, double lw) {
        return rad * (280.16 + 360.9856235 * d) - lw;
    };

    private static double astroRefraction(double h) {
        if (h < 0) // the following formula works for positive altitudes only.
            h = 0; // if h = -0.08901179 a div/0 would occur.

        // formula 16.4 of "Astronomical Algorithms" 2nd edition by Jean Meeus (Willmann-Bell, Richmond) 1998.
        // 1.02 / tan(h + 10.26 / (h + 5.10)) h in degrees, result in arc minutes -> converted to rad:
        return 0.0002967 / Math.tan(h + 0.00312536 / (h + 0.08901179));
    };

    // general sun calculations
    private static double solarMeanAnomaly(double d) {
        return rad * (357.5291 + 0.98560028 * d);
    };

    private static double eclipticLongitude(double M) {

        double C = rad * (1.9148 * Math.sin(M) + 0.02 * Math.sin(2 * M) + 0.0003 * Math.sin(3 * M)), // equation of center
                P = rad * 102.9372; // perihelion of the Earth

        return M + C + P + Math.PI;
    };

    private class SunCoords {
        public double dec;
        public double ra;
    }

    private SunCoords sunCoords(double d) {
        double M = solarMeanAnomaly(d);
        double L = eclipticLongitude(M);
        SunCoords res = new SunCoords();
        res.dec = declination(L, 0);
        res.ra = rightAscension(L, 0);
        return res;
    };

    private class SunPosition {
        public double azimuth;
        public double altitude;
    };

    // calculates sun position for a given date and latitude/longitude
    private SunPosition getPosition(double date, double lat, double lng) {
        double lw = rad * -lng;
        double phi = rad * lat;
        double d = toDays(date);
        SunCoords c = sunCoords(d);
        double H = siderealTime(d, lw) - c.ra;

        SunPosition res = new SunPosition();
        res.azimuth = azimuth(H, phi, c.dec);
        res.altitude = altitude(H, phi, c.dec);
        return res;
    };

    // sun times configuration (angle, morning name, evening name)
    public class TimeConfiguration {
        public double angle;
        public double riseTime;
        public String riseName;
        public String riseTimeStr;
        public double setTime;
        public String setName;
        public String setTimeStr;
        public TimeConfiguration (double a, String r, String s) {
            angle = a; riseName = r; setName = s;
        }
    };

    public enum SUNCALC_TIME {
        SUNCALC_SUNRISE_SUNSET,
        SUNCALC_SUNRISE_END_SUNSET_START,
        SUNCALC_DAWN_DUSK,
        SUNCALC_NIGHT,
        SUNCALC_GOLDEN_HOUR
    }
    public TimeConfiguration times[] = {
            new TimeConfiguration(-0.833, "sunrise","sunset"), // Начало рассвета, конец заката
            new TimeConfiguration(0.3,"sunriseEnd","sunsetStart"), // Конец рассвета, начало заката
            new TimeConfiguration(-6,"dawn","dusk"), // Начало утренней зари, конец ночной зари. Гражданские сумерки.
            new TimeConfiguration(-12,"nauticalDawn","nauticalDusk"), // Навигационные сумерки.
            new TimeConfiguration(-18,"nightEnd","night"), // Конец/начало ночи
            new TimeConfiguration(6,"goldenHourEnd","goldenHour") // Золотой час
    };

    public TimeConfiguration getTime (SUNCALC_TIME index) {
        return times[index.ordinal()];
    }

    // calculations for sun times
    private static double J0 = 0.0009;

    private static double julianCycle(double d, double lw) { return Math.round(d - J0 - lw / (2 * Math.PI)); }

    private static double approxTransit(double Ht, double lw, double n) { return J0 + (Ht + lw) / (2 * Math.PI) + n; }
    private static double solarTransitJ(double ds, double M, double L)  { return J2000 + ds + 0.0053 * Math.sin(M) - 0.0069 * Math.sin(2 * L); }

    private static double hourAngle(double h, double phi, double d) { return Math.acos((Math.sin(h) - Math.sin(phi) * Math.sin(d)) / (Math.cos(phi) * Math.cos(d))); }

    // returns set time for the given sun altitude
    private static double getSetJ(double h, double lw, double phi, double dec, double n, double M, double L) {
        double w = hourAngle(h, phi, dec);
        double a = approxTransit(w, lw, n);
        return solarTransitJ(a, M, L);
    }

    // calculates sun times for a given date and latitude/longitude
    public class Time {
        public double solarNoon;
        public double nadir;
    }
    public Time getTimes (double date, double lat, double lng) {

        double lw = rad * -lng;
        double phi = rad * lat;

        double d = toDays(date);
        double n = julianCycle(d, lw);
        double ds = approxTransit(0, lw, n);

        double M = solarMeanAnomaly(ds);
        double L = eclipticLongitude(M);
        double dec = declination(L, 0);

        double Jnoon = solarTransitJ(ds, M, L);

        Time result = new Time();
        result.solarNoon = fromJulian(Jnoon);
        result.nadir = fromJulian(Jnoon - 0.5);

        Calendar cal = Calendar.getInstance();

        for (int i = 0, len = times.length; i < len; i += 1) {
            TimeConfiguration time = times[i];

            double Jset = getSetJ(time.angle * rad, lw, phi, dec, n, M, L);
            double Jrise = Jnoon - (Jset - Jnoon);

            time.riseTime = fromJulian(Jrise);
            cal.setTimeInMillis((long)time.riseTime);
            time.riseTimeStr = cal.getTime().toString();

            time.setTime = fromJulian(Jset);
            cal.setTimeInMillis((long)time.setTime);
            time.setTimeStr = cal.getTime().toString();
        }

        return result;
    };
}
