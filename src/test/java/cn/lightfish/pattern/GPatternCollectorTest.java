package cn.lightfish.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GPatternCollectorTest {
    @Test
    public void test22222() {
        Map<String, Collection<String>> infos = new HashMap<>();
        addTable(infos, "db1", "user");
        addTable(infos, "db1", "info");
        addTable(infos, "db1", "app");
        addTable(infos, "db1", "333333");

        GPatternBuilder patternBuilder = new GPatternBuilder(0);
        String message = "select tmp.uname,tmp.appname as appname \n" +
                "from  \n" +
                "( select user.name as uname ,app.name as appname ,info.app_id as app_id \n" +
                "  from user,info,app \n" +
                "  where user.uid =info.uid and info.app_id = app.id) as  tmp,333333 \n" +
                " \n" +
                "where tmp.app_id = info.app_id;";
        int id = patternBuilder.addRule(message);

        GPatternIdRecorder recorder = patternBuilder.geIdRecorder();
        TableCollector gPatternTokenCollector = new TableCollector(recorder, infos);
        GPattern gPattern = patternBuilder.createGroupPattern(gPatternTokenCollector);

        gPatternTokenCollector.useSchema("db1");
        gPatternTokenCollector.onCollectStart();
        GPatternMatcher matcher = gPattern.matcher(message);
        Assert.assertTrue(matcher.acceptAll());
        Map<String, Collection<String>> map = gPatternTokenCollector.geTableMap();
        Assert.assertEquals(infos, map);
    }

    private void addTable(Map<String, Collection<String>> infos, String schemaName, String tableName) {
        Collection<String> set = infos.computeIfAbsent(schemaName, (s) -> new HashSet<>());
        set.add(tableName);
    }


}