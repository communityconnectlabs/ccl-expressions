package io.rapidpro.expressions.functions;

import io.rapidpro.expressions.EvaluationContext;
import io.rapidpro.expressions.dates.DateStyle;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.*;
import org.threeten.bp.temporal.Temporal;

import java.math.BigDecimal;
import java.util.HashMap;

import static io.rapidpro.expressions.functions.ExcelFunctions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ExcelFunctions}
 */
public class ExcelFunctionsTest {

    private EvaluationContext m_context;

    @Before
    public void setup() {
        Instant now = Instant.from(ZonedDateTime.of(2015, 8, 14, 10, 38, 30, 123456789, ZoneId.of("Africa/Kigali")));

        m_context = new EvaluationContext(new HashMap<String, Object>(), ZoneId.of("Africa/Kigali"), DateStyle.DAY_FIRST, now);
    }

    @Test
    public void constructor() {
        new ExcelFunctions();
    }

    /************************************************************************************
     * Text Functions
     ************************************************************************************/

    @Test
    public void test_char() {
        assertThat(_char(m_context, 9), is("\t"));
        assertThat(_char(m_context, 10), is("\n"));
        assertThat(_char(m_context, 13), is("\r"));
        assertThat(_char(m_context, 32), is(" "));
        assertThat(_char(m_context, 65), is("A"));
    }

    @Test
    public void test_clean() {
        assertThat(clean(m_context, "Hello \nwo\trl\rd"), is("Hello world"));
    }

    @Test
    public void test_code() {
        assertThat(code(m_context, "\t"), is(9));
        assertThat(code(m_context, "\n"), is(10));
    }

    @Test
    public void test_concatenate() {
        assertThat(concatenate(m_context, "Hello", 4, "\n"), is("Hello4\n"));
        assertThat(concatenate(m_context, "واحد", " ", "اثنان", " ", "ثلاثة"), is("واحد اثنان ثلاثة"));
    }

    @Test
    public void test_fixed() {
        assertThat(fixed(m_context, "1234.5678", 1, false), is("1,234.6"));
        assertThat(fixed(m_context, "1234.5678", 2, false), is("1,234.57"));
        assertThat(fixed(m_context, "1234.5678", 3, false), is("1,234.568"));
        assertThat(fixed(m_context, "1234.5678", 4, false), is("1,234.5678"));
        assertThat(fixed(m_context, "1234.5678", 0, false), is("1,235"));
        assertThat(fixed(m_context, "1234.5678", -1, false), is("1,230"));
        assertThat(fixed(m_context, "1234.5678", -2, false), is("1,200"));
        assertThat(fixed(m_context, "1234.5678", -3, false), is("1,000"));
        assertThat(fixed(m_context, "1234.5678", -4, false), is("0"));

        assertThat(fixed(m_context, "1234.1234", 2, false), is("1,234.12"));
        assertThat(fixed(m_context, "1234.5678", 3, true), is("1234.568"));
    }

    @Test
    public void test_left() {
        assertThat(left(m_context, "abcdef", 0), is(""));
        assertThat(left(m_context, "abcdef", 2), is("ab"));
        assertThat(left(m_context, "واحد", 2), is("وا"));
    }

    @Test(expected = RuntimeException.class)
    public void test_left_negativeChars() {
        left(m_context, "abcd", -1);
    }

    @Test
    public void test_len() {
        assertThat(len(m_context, ""), is(0));
        assertThat(len(m_context, " "), is(1));
        assertThat(len(m_context, "qwérty"), is(6));
        assertThat(len(m_context, "سلام"), is(4));
    }

    @Test
    public void test_lower() {
        assertThat(lower(m_context, ""), is(""));
        assertThat(lower(m_context, "aBcD"), is("abcd"));
        assertThat(lower(m_context, "A واحد"), is("a واحد"));
    }

    @Test
    public void test_proper() {
        assertThat(proper(m_context, ""), is(""));
        assertThat(proper(m_context, "f1rst  sécOND-third_fourth"), is("F1Rst  Sécond-Third_Fourth"));
        assertThat(proper(m_context, "واحد abc ثلاثة"), is("واحد Abc ثلاثة"));
    }

    @Test
    public void test_rept() {
        assertThat(rept(m_context, "abc", 3), is("abcabcabc"));
        assertThat(rept(m_context, "واحد", 3), is("واحدواحدواحد"));
    }

    @Test(expected = RuntimeException.class)
    public void test_rept_negativeTimes() {
        rept(m_context, "abc", -1);
    }

    @Test
    public void test_right() {
        assertThat(right(m_context, "abcdef", 0), is(""));
        assertThat(right(m_context, "abcdef", 2), is("ef"));
        assertThat(right(m_context, "واحد", 2), is("حد"));
    }

    @Test(expected = RuntimeException.class)
    public void test_right_negativeChars() {
        right(m_context, "abcd", -1);
    }

    @Test
    public void test_substitute() {
        assertThat(substitute(m_context, "hello Hello world", "hello", "bonjour", -1), is("bonjour Hello world"));  // case-sensitive
        assertThat(substitute(m_context, "hello hello world", "hello", "bonjour", -1), is("bonjour bonjour world"));  // all instances
        assertThat(substitute(m_context, "hello hello world", "hello", "bonjour", 2), is("hello bonjour world"));  // specific instance
        assertThat(substitute(m_context, "واحد اثنين ثلاثة", "واحد", "اثنين", -1), is("اثنين اثنين ثلاثة"));
    }

    @Test
    public void test_unichar() {
        assertThat(unichar(m_context, 65), is("A"));
        assertThat(unichar(m_context, 1575), is("ا"));
    }

    @Test
    public void test_unicode() {
        assertThat(unicode(m_context, "\t"), is(9));
        assertThat(unicode(m_context, "\u04d2"), is(1234));
        assertThat(unicode(m_context, "ا"), is(1575));
    }

    @Test(expected = RuntimeException.class)
    public void test_unicode_emptyString() {
        unicode(m_context, "");
    }

    @Test
    public void test_upper() {
        assertThat(upper(m_context, ""), is(""));
        assertThat(upper(m_context, "aBcD"), is("ABCD"));
        assertThat(upper(m_context, "a واحد"), is("A واحد"));
    }

    /************************************************************************************
     * Date and Time Functions
     ************************************************************************************/

    @Test
    public void test_date() {
        assertThat(date(m_context, 1900, 1, 1), is(LocalDate.of(1900, 1, 1)));
        assertThat(date(m_context, 2012, "3", new BigDecimal(2.0)), is(LocalDate.of(2012, 3, 2)));
    }

    @Test
    public void test_datedif() {
        assertThat(datedif(m_context, "28/5/81", "23-11-15", "y"), is(34));
        assertThat(datedif(m_context, LocalDate.of(2011, 1, 1), LocalDate.of(2012, 12, 31), "y"), is(1));
        assertThat(datedif(m_context, "20/9/14", "23/11/15", "m"), is(14));
        assertThat(datedif(m_context, "1/6/2001", "15/8/2002", "d"), is(440));
        assertThat(datedif(m_context, "1/6/2001", "15/8/2002", "YD"), is(75));
        assertThat(datedif(m_context, "1/6/2001", "15/8/2002", "YM"), is(2));
        assertThat(datedif(m_context, "1/6/2001", "15/8/2002", "mD"), is(14));
        assertThat(datedif(m_context, "16/6/2001", "15/8/2002", "mD"), is(30));
    }

    @Test
    public void test_datevalue() {
        assertThat(datevalue(m_context, "2-3-13"), is(LocalDate.of(2013, 3, 2)));
        assertThat(datevalue(m_context, "Aug 14th 2015"), is(LocalDate.of(2015, 8, 14)));
    }

    @Test
    public void test_day() {
        assertThat(day(m_context, LocalDate.of(2013, 3, 2)), is(2));
        assertThat(day(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(14));
        assertThat(day(m_context, "Aug 14th 2015"), is(14));
    }

    @Test
    public void test_days() {
        assertThat(days(m_context, "15/3/11", "1/2/11"), is(42));
        assertThat(days(m_context, ZonedDateTime.of(2011, 12, 31, 10, 38, 30, 123456, ZoneId.of("Africa/Kigali")), LocalDate.of(2011, 1, 1)), is(364));
    }

    @Test
    public void test_edate() {
        assertThat(edate(m_context, LocalDate.of(2013, 3, 2), 4), is((Temporal) LocalDate.of(2013, 7, 2)));
        assertThat(edate(m_context, LocalDate.of(2013, 3, 2), -4), is((Temporal) LocalDate.of(2012, 11, 2)));
        assertThat(edate(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali")), 4),
                is((Temporal) ZonedDateTime.of(2015, 12, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))));
        assertThat(edate(m_context, "Aug 14th 2015", 3), is((Temporal) LocalDate.of(2015, 11, 14)));
    }

    @Test
    public void test_hour() {
        assertThat(hour(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(10));
        assertThat(hour(m_context, "Aug 14th 2015 10:38 PM"), is(22));
    }

    @Test
    public void test_minute() {
        assertThat(minute(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(27));
        assertThat(minute(m_context, "Aug 14th 2015 10:38 PM"), is(38));
    }

    @Test
    public void test_month() {
        assertThat(month(m_context, LocalDate.of(2013, 3, 2)), is(3));
        assertThat(month(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(8));
        assertThat(month(m_context, "Aug 14th 2015"), is(8));
    }

    @Test
    public void test_now() {
        assertThat(now(m_context), is(ZonedDateTime.of(2015, 8, 14, 10, 38, 30, 123456789, ZoneId.of("Africa/Kigali"))));
    }

    @Test
    public void test_second() {
        assertThat(second(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 30, 0, ZoneId.of("Africa/Kigali"))), is(30));
        assertThat(second(m_context, "Aug 14th 2015 10:38 PM"), is(0));
    }

    @Test
    public void test_time() {
        assertThat(time(m_context, 11, 5, 30), is(OffsetTime.of(11, 5, 30, 0, ZoneOffset.ofHours(2))));
        assertThat(time(m_context, "11", "5", "30"), is(OffsetTime.of(11, 5, 30, 0, ZoneOffset.ofHours(2))));
    }

    @Test
    public void test_timevalue() {
        assertThat(timevalue(m_context, "11:05"), is(OffsetTime.of(11, 5, 0, 0, ZoneOffset.ofHours(2))));
        assertThat(timevalue(m_context, "11:05:30"), is(OffsetTime.of(11, 5, 30, 0, ZoneOffset.ofHours(2))));
    }

    @Test
    public void test_today() {
        assertThat(today(m_context), is(LocalDate.of(2015, 8, 14)));
    }

    @Test
    public void test_weekday() {
        assertThat(weekday(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(6));
        assertThat(weekday(m_context, LocalDate.of(2015, 8, 15)), is(7));
        assertThat(weekday(m_context, "Aug 16th 2015"), is(1));
    }

    @Test
    public void test_year() {
        assertThat(year(m_context, ZonedDateTime.of(2015, 8, 14, 10, 27, 0, 0, ZoneId.of("Africa/Kigali"))), is(2015));
        assertThat(year(m_context, LocalDate.of(2015, 8, 15)), is(2015));
        assertThat(year(m_context, "Aug 16th '15"), is(2015));
    }

    /************************************************************************************
     * Math Functions
     ************************************************************************************/

    @Test
    public void test_abs() {
        assertThat(abs(m_context, 1), is(new BigDecimal(1)));
        assertThat(abs(m_context, new BigDecimal(1)), is(new BigDecimal(1)));
        assertThat(abs(m_context, new BigDecimal(-1)), is(new BigDecimal(1)));
    }

    @Test
    public void test_average() {
        assertThat(average(m_context, 1), is(new BigDecimal(1)));
        assertThat(average(m_context, 1, "2", 3), is(new BigDecimal(2)));
        assertThat(average(m_context, -1, -2), is(new BigDecimal("-1.5")));
    }

    @Test(expected = RuntimeException.class)
    public void test_average_noArgs() {
        average(m_context);
    }

    @Test
    public void test_exp() {
        assertThat(exp(m_context, 1), is(new BigDecimal("2.718281828459045090795598298")));
        assertThat(exp(m_context, "2.0"), is(new BigDecimal("7.389056098930649518763402739")));
    }

    @Test
    public void test_int() {
        assertThat(_int(m_context, "8.9"), is(8));
        assertThat(_int(m_context, "-8.9"), is(-9));
        assertThat(trunc(m_context, "1234.5678"), is(1234));
    }

    @Test
    public void test_max() {
        assertThat(max(m_context, 1), is(new BigDecimal(1)));
        assertThat(max(m_context, 1, 3, 2, -5), is(new BigDecimal(3)));
        assertThat(max(m_context, -2, -5), is(new BigDecimal(-2)));
    }

    @Test(expected = RuntimeException.class)
    public void test_max_noArgs() {
        max(m_context);
    }

    @Test
    public void test_min() {
        assertThat(min(m_context, 1), is(new BigDecimal(1)));
        assertThat(min(m_context, -1, -3, -2, 5), is(new BigDecimal(-3)));
        assertThat(min(m_context, -2, -5), is(new BigDecimal(-5)));
    }

    @Test(expected = RuntimeException.class)
    public void test_min_noArgs() {
        min(m_context);
    }

    @Test
    public void test_mod() {
        assertThat(mod(m_context, "3", 2), is(new BigDecimal(1)));
        assertThat(mod(m_context, -3, 2), is(new BigDecimal(1)));
        assertThat(mod(m_context, 3, -2), is(new BigDecimal(-1)));
        assertThat(mod(m_context, -3, -2), is(new BigDecimal(-1)));
    }

    @Test
    public void test_power() {
        assertThat(power(m_context, "4", 2), is(new BigDecimal(16)));
        assertThat(power(m_context, 4, "0.5"), is(new BigDecimal(2)));
    }

    @Test
    public void test_rand() {
        assertThat(rand(), instanceOf(BigDecimal.class));
        assertThat(rand().compareTo(BigDecimal.ZERO), greaterThan(0));
        assertThat(rand().compareTo(BigDecimal.ONE), lessThan(0));
    }

    @Test
    public void test_randbetween() {
        assertThat(randbetween(m_context, "2", 4), is(both(greaterThanOrEqualTo(2)).and(lessThanOrEqualTo(4))));
    }

    @Test
    public void test_round() {
        assertThat(round(m_context, "2.15", 1), is(new BigDecimal("2.2")));
        assertThat(round(m_context, "2.149", 1), is(new BigDecimal("2.1")));
        assertThat(round(m_context, "-1.475", 2), is(new BigDecimal("-1.48")));
        assertThat(round(m_context, "21.5", "-1"), is(new BigDecimal(20)));
        assertThat(round(m_context, "626.3", "-3"), is(new BigDecimal(1000)));
        assertThat(round(m_context, "1.98", "-1"), is(new BigDecimal(0)));
        assertThat(round(m_context, "-50.55", "-2"), is(new BigDecimal(-100)));
    }

    @Test
    public void test_rounddown() {
        assertThat(rounddown(m_context, "3.2", 0), is(new BigDecimal("3")));
        assertThat(rounddown(m_context, "76.9", 0), is(new BigDecimal("76")));
        assertThat(rounddown(m_context, "3.14159", 3), is(new BigDecimal("3.141")));
        assertThat(rounddown(m_context, "-3.14159", "1"), is(new BigDecimal("-3.1")));
        assertThat(rounddown(m_context, "31415.92654", "-2"), is(new BigDecimal(31400)));
        assertThat(rounddown(m_context, "31499", "-2"), is(new BigDecimal(31400)));
    }

    @Test
    public void test_roundup() {
        assertThat(roundup(m_context, "3.2", 0), is(new BigDecimal("4")));
        assertThat(roundup(m_context, "76.9", 0), is(new BigDecimal("77")));
        assertThat(roundup(m_context, "3.14159", 3), is(new BigDecimal("3.142")));
        assertThat(roundup(m_context, "-3.14159", "1"), is(new BigDecimal("-3.2")));
        assertThat(roundup(m_context, "31415.92654", "-2"), is(new BigDecimal(31500)));
        assertThat(roundup(m_context, "31499", "-2"), is(new BigDecimal(31500)));
    }

    @Test
    public void test_sum() {
        assertThat(sum(m_context, 1), is(new BigDecimal(1)));
        assertThat(sum(m_context, 1, 2), is(new BigDecimal(3)));
        assertThat(sum(m_context, 1, 2, "3"), is(new BigDecimal(6)));
    }

    @Test(expected = RuntimeException.class)
    public void test_sum_noArgs() {
        sum(m_context);
    }

    @Test
    public void test_trunc() {
        assertThat(trunc(m_context, "8.9"), is(8));
        assertThat(trunc(m_context, "-8.9"), is(-8));
        assertThat(trunc(m_context, "0.45"), is(0));
        assertThat(trunc(m_context, "1234.5678"), is(1234));
    }

    /************************************************************************************
     * Logical Functions
     ************************************************************************************/

    @Test
    public void test_and() {
        assertThat(and(m_context, false), is(false));
        assertThat(and(m_context, true), is(true));
        assertThat(and(m_context, 1, true, "true"), is(true));
        assertThat(and(m_context, 1, true, "true", 0), is(false));
    }

    @Test
    public void test_false() {
        assertThat(_false(), is(false));
    }

    @Test
    public void test_if() {
        assertThat(_if(m_context, true, 0, false), is((Object) 0));
        assertThat(_if(m_context, true, "x", "y"), is((Object) "x"));
        assertThat(_if(m_context, "true", "x", "y"), is((Object) "x"));
        assertThat(_if(m_context, false, 0, false), is((Object) false));
        assertThat(_if(m_context, false, "x", "y"), is((Object) "y"));
        assertThat(_if(m_context, 0, "x", "y"), is((Object) "y"));
    }

    @Test
    public void test_or() {
        assertThat(or(m_context, false), is(false));
        assertThat(or(m_context, true), is(true));
        assertThat(or(m_context, 1, false, "false"), is(true));
        assertThat(or(m_context, 0, false, "FALSE"), is(false));
        assertThat(or(m_context, 0, true, "false"), is(true));
    }

    @Test
    public void test_true() {
        assertThat(_true(), is(true));
    }
}
