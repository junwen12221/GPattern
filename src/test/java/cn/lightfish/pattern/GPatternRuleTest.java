/**
 * Copyright (C) <2019>  <chen junwen>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <http://www.gnu.org/licenses/>.
 */
package cn.lightfish.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class GPatternRuleTest {
    @Test
    public void test() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM {table} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Assert.assertEquals("travelrecord", GPattern.toContextMap(matcher).get("table"));
    }

    @Test
    public void test1() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM {table} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher(" id FROM travelrecord LIMIT 1;");
        Assert.assertFalse(matcher.acceptAll());
    }

    @Test
    public void test2() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM {table} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM `travelrecord` LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Assert.assertEquals("`travelrecord`", GPattern.toContextMap(matcher).get("table"));
    }

    @Test
    public void test3() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT {column} FROM {table} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("id", map.get("column"));
        Assert.assertEquals("travelrecord", map.get("table"));
    }

    @Test
    public void test4() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{type} id FROM {table} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("SELECT", map.get("type"));
        Assert.assertEquals("travelrecord", map.get("table"));
    }

    @Test
    public void test5() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{other}");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select 1;", map.get("other"));
    }

    @Test
    public void test6() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{other} aaaa");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1; aaaa");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        ;
        Assert.assertEquals("select 1;", map.get("other"));
    }

    @Test
    public void test7() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("aaaa {other} ");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("aaaa select 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        ;
        Assert.assertEquals("select 1;", map.get("other"));
    }

    @Test
    public void test8() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {other} aaaa {other1} bbbb");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1; aaaa  select 2; bbbb");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        ;
        Assert.assertEquals("select 1;", map.get("other"));
        Assert.assertEquals("select 2;", map.get("other1"));
    }

    @Test
    public void test9() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {s1};{s2};{s3};");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1; select 2; select 3;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        ;
        Assert.assertEquals("select 1", map.get("s1"));
        Assert.assertEquals("select 2", map.get("s2"));
        Assert.assertEquals("select 3", map.get("s3"));
    }

    @Test
    public void test10() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {s1};;;;;{s2};{s3};");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1;;;;;select 2; select 3;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        ;
        Assert.assertEquals("select 1", map.get("s1"));
        Assert.assertEquals("select 2", map.get("s2"));
        Assert.assertEquals("select 3", map.get("s3"));
    }


    @Test(expected = GPatternException.NameAdjacentException.class)
    public void test11() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {s1}{s2};{s3};");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1 select 2; select 3;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select 1", map.get("s1"));
        Assert.assertEquals("select 2", map.get("s2"));
        Assert.assertEquals("select 3", map.get("s3"));
    }

    @Test(expected = GPatternException.NameLocationAmbiguityException.class)
    public void test12() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM {table} LIMIT 1;");
        int id2 = patternBuilder.addRule("SELECT id FROM {table2} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();
    }

    @Test
    public void test18() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT  id FROM {table} LIMIT 1;");
        int id2 = patternBuilder.addRule("{type} id FROM {table2} LIMIT 1;");
        GPattern GPattern = patternBuilder.createGroupPattern();

        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);

        GPatternMatcher matcher;
        Map<String, String> map;

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals(null, map.get("type"));
        Assert.assertEquals("travelrecord", map.get("table"));


        matcher = GPattern.matcher("select id FROM travelrecord LIMIT 1;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id2, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select", map.get("type"));
        Assert.assertEquals("travelrecord", map.get("table2"));
    }

    @Test
    public void test19() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("LIMIT {count}");
        int id2 = patternBuilder.addRule("LIMIT 1");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);
        Assert.assertEquals(1, matcher.id());
        Assert.assertTrue(GPattern.toContextMap(matcher).isEmpty());
    }

    @Test(expected = GPatternException.NameAmbiguityException.class)
    public void test20() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT  id FROM travelrecord LIMIT {count}");
        int id2 = patternBuilder.addRule("SELECT {count} FROM travelrecord LIMIT 1");
        GPattern GPattern = patternBuilder.createGroupPattern();
    }

    @Test
    public void test21() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT  id FROM travelrecord LIMIT ");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertFalse(matcher.acceptAll());
    }

    @Test
    public void test22() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("  id FROM travelrecord LIMIT ");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertFalse(matcher.acceptAll());
    }

    @Test
    public void test23() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{any} SELECT  id FROM travelrecord LIMIT {any2}");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals(null, map.get("any"));
        Assert.assertEquals("1", map.get("any2"));
    }

    @Test
    public void test24() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{any} FROM travelrecord  {any2}");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Assert.assertEquals(id, matcher.id());
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("SELECT id", map.get("any"));
        Assert.assertEquals("LIMIT 1", map.get("any2"));
    }

    @Test
    public void test25() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{any} FROM travelrecord  {any2}");
        int id2 = patternBuilder.addRule("SELECT id FROM travelrecord  {any3}");
        GPattern GPattern = patternBuilder.createGroupPattern();

        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);


        GPatternMatcher matcher;
        Map<String, String> map;

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals(id2, matcher.id());
        Assert.assertEquals(null, map.get("any"));
        Assert.assertEquals("LIMIT 1", map.get("any3"));

        matcher = GPattern.matcher("select id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals(id, matcher.id());
        Assert.assertEquals("select id", map.get("any"));
        Assert.assertEquals("LIMIT 1", map.get("any2"));
    }

    @Test
    public void test26() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("{any} FROM travelrecord  {any2}");
        int id2 = patternBuilder.addRule("SELECT id FROM travelrecord  {any3}");
        GPattern GPattern = patternBuilder.createGroupPattern();

        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);

        GPatternMatcher matcher;
        Map<String, String> map;

        matcher = GPattern.matcher("select id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select id", map.get("any"));
        Assert.assertEquals("LIMIT 1", map.get("any2"));

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id2, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT 1", map.get("any3"));
    }

    @Test(expected = GPatternException.NameAmbiguityException.class)
    public void test13() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {s1};{s1};{s3};");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1;select 2; select 3;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select 1", map.get("s1"));
        Assert.assertEquals("select 3", map.get("s3"));
    }

    @Test(expected = GPatternException.NameSyntaxException.class)
    public void test14() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {s1 s2};{s3};");
        GPattern GPattern = patternBuilder.createGroupPattern();
        GPatternMatcher matcher = GPattern.matcher("select 1; select 3;");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(0, id);
        Map<String, String> map = GPattern.toContextMap(matcher);
        Assert.assertEquals("select 1", map.get("s1"));
        Assert.assertEquals("select 3", map.get("s3"));
    }

    @Test(expected = GPatternException.NameSyntaxException.class)
    public void test15() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {");
    }

    @Test(expected = GPatternException.NameSyntaxException.class)
    public void test16() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule(" {n");
    }

    @Test
    public void test27() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM travelrecord  {any2} 1");
        int id2 = patternBuilder.addRule("SELECT id FROM travelrecord {any2} 2");
        GPattern GPattern = patternBuilder.createGroupPattern();


        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);

        GPatternMatcher matcher;
        Map<String, String> map;


        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT", map.get("any2"));

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 2");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id2, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT", map.get("any2"));
    }

    @Test
    public void test28() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("SELECT id FROM travelrecord  {any2}");
        int id2 = patternBuilder.addRule("SELECT id FROM travelrecord {any2} 2");
        GPattern GPattern = patternBuilder.createGroupPattern();

        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);

        GPatternMatcher matcher;
        Map<String, String> map;

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT 1", map.get("any2"));

        matcher = GPattern.matcher("SELECT id FROM travelrecord LIMIT 2");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id2, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT", map.get("any2"));
    }

    @Test
    public void test29() {
        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        int id = patternBuilder.addRule("travelrecord  {any2}");
        int id2 = patternBuilder.addRule("travelrecord {any2} 2");
        int id3 = patternBuilder.addRule("travelrecord {any2} 3");
        GPattern GPattern = patternBuilder.createGroupPattern();

        Assert.assertEquals(0, id);
        Assert.assertEquals(1, id2);
        Assert.assertEquals(2, id3);


        GPatternMatcher matcher;
        Map<String, String> map;

        matcher = GPattern.matcher("travelrecord LIMIT 4");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT 4", map.get("any2"));

        matcher = GPattern.matcher("travelrecord LIMIT 2");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id2, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT", map.get("any2"));

        matcher = GPattern.matcher("travelrecord LIMIT 3");
        Assert.assertTrue(matcher.acceptAll());
        Assert.assertEquals(id3, matcher.id());
        map = GPattern.toContextMap(matcher);
        Assert.assertEquals("LIMIT", map.get("any2"));


    }
}