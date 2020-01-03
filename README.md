# GPattern pattern matching

Author:junwen 2019-10-2

Qq:294712221

<https://github.com/junwen12221/GPattern>

中文介绍

https://github.com/junwen12221/GPattern/blob/master/README-CN.md


该库与
https://github.com/JoanZapata/string-format
正好互补

## Quick Start

```java
GPatternBuilder patternBuilder = new GPatternBuilder(0);
Int id = patternBuilder.addRule("SELECT id FROM {table} LIMIT 1;");
GPattern gPattern = patternBuilder.createGroupPattern();
GPatternMatcher matcher = gPattern.matcher("SELECT id FROM travelrecord LIMIT 1;");
Assert.assertTrue(matcher.acceptAll());
Assert.assertEquals(0, id);
Assert.assertEquals("travelrecord", gPattern.toContextMap(matcher).get("table"));
```



## lexical analyzer

#### aims

UTF8 or ASCII byte count components into multiple lexical units according to the following lexical rules



#### Space

Space-separated lexical unit

```
 
\t
\f
```



#### Comments

Comments are used to add hints, notes, suggestions, warnings, etc. to the source code. These information are ignored in lexical analysis.

##### Single line comment (single-line comment)

```
-- Single line comment
// single line comment
#单行注
```

##### Multi-line comment (multiple-line comment)

```
/* Multi-line comments */
/*

Multi-line comment

*/
```



### lexical unit (Token)

##### Direct quantity

Direct quantities only support ASCII characters

##### String Direct Quantity

```
'id'
"id"
```

##### escaped string direct quantity

```
`id`
```



#### Identifier

Identifies the sequence of ASCII direct quantifiers in the pattern.

##### Single character

ASCII characters separated by spaces and length 1

##### Character sequence

A space-separated sequence of characters containing letters or numbers or underscores ("_") or dollar signs ("$") (length greater than 1)



#### Uppercase

By default case is ignored, the size is converted to lowercase, you can turn off this feature



## pattern matching

### Name capture

Capture multiple or one lexical units, save them at the beginning and end of the byte array, and get their range in the byte array by name

{name}, where name must be an ASCII character

The byte array to be matched allows UTF8 characters



##### A lexical unit capture

mode:

```
SELECT id FROM {table} LIMIT 1;
```

Pending string

```
SELECT id FROM travelrecord LIMIT 1;
```

You can get travelrecord according to 'table'



##### Multiple lexical unit capture

Pending string

```
SELECT id FROM travelrecord , travelrecord2 LIMIT 1;
```

Can be obtained by 'table'

Travelrecord ,travelrecord2



##### Direct quantity priority matching

mode:

```
SELECT id FROM {table} LIMIT 1;// Mode 1
SELECT id FROM {table} LIMIT {n};// mode 2
```

Pending string

```
SELECT id FROM travelrecord LIMIT 1;
```

Mode 1 match



## pattern ID

```java
GPatternBuilder patternBuilder = new GPatternBuilder(0);
int id = patternBuilder.addRule("{any} FROM travelrecord {any2}");//the mode corresponds to an id
GPattern gPattern = patternBuilder.createGroupPattern();
GPatternMatcher matcher = gPattern.matcher("SELECT id FROM travelrecord LIMIT 1");
Assert.assertTrue(matcher.acceptAll());
Assert.assertEquals(0, id);
Assert.assertEquals(id, matcher.id());// After the pattern matching is successful, the id corresponding to the mode can be obtained according to the matcher.
Map<String, String> map = gPattern.toContextMap(matcher);
Assert.assertEquals("SELECT id", map.get("any"));
Assert.assertEquals("LIMIT 1", map.get("any2"));
```

## License

GPLv3



![Creative Commons License](https://i.creativecommons.org/l/by-sa/4.0/88x31.png) (http://creativecommons.org/licenses/by-sa/4.0/)

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License] (http://creativecommons.org/licenses/by-sa/4.0/).

------

