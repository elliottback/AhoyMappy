package elliott.back.maps;

import elliott.back.common.BasicStringMapTester;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

public class EclipseHashMapBasicTest extends BasicStringMapTester {

    public EclipseHashMapBasicTest() {
        super( new ConcurrentHashMap<>() );
    }
}