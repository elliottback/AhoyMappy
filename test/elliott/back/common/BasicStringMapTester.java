package elliott.back.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract  class BasicStringMapTester {
    private Map<String,String> mapToSanityCheck;

    public BasicStringMapTester(Map<String,String> map ) {
        this.mapToSanityCheck = map;
    }

    @Test
    @DisplayName("test null keys -- there can only be 1")
    public void testNullKeys(){
        // insert/remove 1 value
        mapToSanityCheck.put(null, "abc");
        assertEquals( "abc", mapToSanityCheck.get(null) );
        assertEquals(1, mapToSanityCheck.size());

        String value = mapToSanityCheck.remove( null );
        assertEquals("abc", value);
        assertEquals(0, mapToSanityCheck.size());

        // insert 2 / remove 1 value
        mapToSanityCheck.put(null, "abc");
        value = mapToSanityCheck.put(null, "def");
        assertEquals("abc", value);
        assertEquals( "def", mapToSanityCheck.get(null) );
        assertEquals(1, mapToSanityCheck.size());

        value = mapToSanityCheck.remove( null );
        assertEquals("def", value);
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("test null values -- they really shouldn't matter")
    public void testNullValues() {
        // insert/remove 1 value
        mapToSanityCheck.put("abc", null);
        assertEquals( null, mapToSanityCheck.get("abc") );
        assertEquals(1, mapToSanityCheck.size());

        String value = mapToSanityCheck.remove( "abc" );
        assertEquals(null, value);
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("test more than initial capacity (32)")
    public void test127Entries(){
        for(int i = 0; i < 127; i++)
            mapToSanityCheck.put(""+i, "v: " + i);

        assertEquals(127, mapToSanityCheck.size());

        // verify the entries
        for(int i = 0; i < 127; i++)
            assertEquals( "v: " + i, mapToSanityCheck.get(""+i) );

        mapToSanityCheck.clear();
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("duplicate string keys")
    public void testDupeStringKeys() {
        mapToSanityCheck.put("abc", "abc");
        String value = mapToSanityCheck.put("abc", "def");
        assertEquals("abc", value);
        assertEquals( "def", mapToSanityCheck.get("abc") );
        assertEquals(1, mapToSanityCheck.size());

        value = mapToSanityCheck.remove( "abc" );
        assertEquals("def", value);
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("missing key")
    public void testMissingKey() {
        assertEquals( null, mapToSanityCheck.get("abc") );
        assertEquals( false, mapToSanityCheck.containsKey("abc") );
        assertEquals( false, mapToSanityCheck.containsValue("def") );

        String value = mapToSanityCheck.put("abc", "def");
        assertEquals(null, value);

        assertEquals( "def", mapToSanityCheck.get("abc") );
        assertEquals( true, mapToSanityCheck.containsKey("abc") );
        assertEquals( true, mapToSanityCheck.containsValue("def") );

        mapToSanityCheck.clear();
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("a million entries")
    public void testAMillionElements(){
        for(int i = 0; i < 1000000; i++)
            mapToSanityCheck.put(""+i, "v: " + i);

        assertEquals(1000000, mapToSanityCheck.size());

        // verify the entries
        for(int i = 0; i < 1000000; i++)
            assertEquals( "v: " + i, mapToSanityCheck.get(""+i) );

        mapToSanityCheck.clear();
        assertEquals(0, mapToSanityCheck.size());
    }

    @Test
    @DisplayName("map protocols - keySet")
    public void testMapContractKeySet() {
        for(int i = 0; i < 127; i++)
            mapToSanityCheck.put(""+i, "v: " + i);

        assertEquals(127, mapToSanityCheck.size());

        Set<String> keys = mapToSanityCheck.keySet();
        for(int i = 0; i < 127; i++)
            keys.contains(""+i);

        mapToSanityCheck.clear();
    }

    @Test
    @DisplayName("map protocols - entrySet")
    public void testMapContractEntrySet() {
        for(int i = 0; i < 127; i++)
            mapToSanityCheck.put(""+i, "v: " + i);

        assertEquals(127, mapToSanityCheck.size());

        Set<Map.Entry<String, String>> keys = mapToSanityCheck.entrySet();
        for(int i = 0; i < 127; i++)
            keys.contains(new Tuple(""+i, "v: " + i));

        mapToSanityCheck.clear();
    }

    @Test
    @DisplayName("map protocols - values")
    public void testMapContractValueSet() {
        for(int i = 0; i < 127; i++)
            mapToSanityCheck.put(""+i, "v: " + i);

        assertEquals(127, mapToSanityCheck.size());

        Collection<String> values = mapToSanityCheck.values();
        for(int i = 0; i < 127; i++)
            values.contains("v: " + i);

        mapToSanityCheck.clear();
    }
}