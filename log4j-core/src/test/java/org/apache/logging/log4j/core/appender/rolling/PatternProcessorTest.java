/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package org.apache.logging.log4j.core.appender.rolling;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the PatternProcessor class.
 */
public class PatternProcessorTest {

    private String format(final long time) {
        final String actualStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(time));
        return actualStr;
    }

    @Test
    public void testGetNextTimeMonthlyReturnsFirstDayOfNextMonth() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 9, 15, 10, 31, 59); // Oct 15th
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // We expect 1st day of next month
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 10, 1, 0, 0, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeMonthlyReturnsFirstDayOfNextMonth2() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 0, 31, 10, 31, 59); // 2014 Jan 31st
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // Expect 1st of next month: 2014 Feb 1st
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 1, 1, 0, 0, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeMonthlyReturnsFirstDayOfNextMonth3() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 11, 31, 10, 31, 59); // 2014 Dec 31st
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // Expect 1st of next month: 2015 Jan 1st
        final Calendar expected = Calendar.getInstance();
        expected.set(2015, 0, 1, 0, 0, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeWeeklyReturnsFirstDayOfNextWeek_US() {
        final Locale old = Locale.getDefault();
        Locale.setDefault(Locale.US); // force 1st day of the week to be Sunday

        try {
            final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-W}.log.gz");
            final Calendar initial = Calendar.getInstance();
            initial.set(2014, 2, 4, 10, 31, 59); // Tue, March 4, 2014
            final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

            // expect Sunday, March 9, 2014
            final Calendar expected = Calendar.getInstance();
            expected.set(2014, 2, 9, 0, 0, 00);
            expected.set(Calendar.MILLISECOND, 0);
            assertEquals(format(expected.getTimeInMillis()), format(actual));
        } finally {
            Locale.setDefault(old);
        }
    }

    @Test
    public void testGetNextTimeWeeklyReturnsFirstDayOfNextWeek_FRANCE() {
        final Locale old = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE); // force 1st day of the week to be Monday

        try {
            final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-W}.log.gz");
            final Calendar initial = Calendar.getInstance();
            initial.set(2014, 2, 4, 10, 31, 59); // Tue, March 4, 2014
            final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

            // expect Monday, March 10, 2014
            final Calendar expected = Calendar.getInstance();
            expected.set(2014, 2, 10, 0, 0, 00);
            expected.set(Calendar.MILLISECOND, 0);
            assertEquals(format(expected.getTimeInMillis()), format(actual));
        } finally {
            Locale.setDefault(old);
        }
    }

    @Test
    public void testGetNextTimeHourlyReturnsFirstMinuteOfNextHour() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-dd-HH}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 2, 4, 10, 31, 59); // Tue, March 4, 2014, 10:31
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // expect Wed, March 4, 2014, 11:00
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 2, 4, 11, 00, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeHourlyReturnsFirstMinuteOfNextHour2() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-dd-HH}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 2, 4, 23, 31, 59); // Tue, March 4, 2014, 23:31
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // expect Wed, March 5, 2014, 00:00
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 2, 5, 00, 00, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeMinutelyReturnsFirstSecondOfNextMinute() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-dd-HH-mm}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 2, 4, 10, 31, 59); // Tue, March 4, 2014, 10:31
        initial.set(Calendar.MILLISECOND, 0);
        assertEquals("2014/03/04 10:31:59.000", format(initial.getTimeInMillis()));
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // expect Tue, March 4, 2014, 10:32
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 2, 4, 10, 32, 00);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeSecondlyReturnsFirstMillisecOfNextSecond() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-dd-HH-mm-ss}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 2, 4, 10, 31, 53); // Tue, March 4, 2014, 10:31:53
        initial.set(Calendar.MILLISECOND, 123);
        assertEquals("2014/03/04 10:31:53.123", format(initial.getTimeInMillis()));
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // expect Tue, March 4, 2014, 10:31:54
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 2, 4, 10, 31, 54);
        expected.set(Calendar.MILLISECOND, 0);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }

    @Test
    public void testGetNextTimeMillisecondlyReturnsNextMillisec() {
        final PatternProcessor pp = new PatternProcessor("logs/app-%d{yyyy-MM-dd-HH-mm-ss.SSS}.log.gz");
        final Calendar initial = Calendar.getInstance();
        initial.set(2014, 2, 4, 10, 31, 53); // Tue, March 4, 2014, 10:31:53.123
        initial.set(Calendar.MILLISECOND, 123);
        assertEquals("2014/03/04 10:31:53.123", format(initial.getTimeInMillis()));
        final long actual = pp.getNextTime(initial.getTimeInMillis(), 1, false);

        // expect Tue, March 4, 2014, 10:31:53.124
        final Calendar expected = Calendar.getInstance();
        expected.set(2014, 2, 4, 10, 31, 53);
        expected.set(Calendar.MILLISECOND, 124);
        assertEquals(format(expected.getTimeInMillis()), format(actual));
    }
}
