package elliott.back.maps;

import elliott.back.common.BasicStringMapTester;

import java.util.concurrent.ConcurrentHashMap;

public class JavaConcurrentHashMapBasicTest extends BasicStringMapTester {

    public JavaConcurrentHashMapBasicTest() {
        super( new ConcurrentHashMap<>() );
    }
}