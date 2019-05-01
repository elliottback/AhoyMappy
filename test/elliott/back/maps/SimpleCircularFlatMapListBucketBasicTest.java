package elliott.back.maps;

import elliott.back.common.BasicStringMapTester;

public class SimpleCircularFlatMapListBucketBasicTest extends BasicStringMapTester {

    public SimpleCircularFlatMapListBucketBasicTest() {
        super( new SimpleCircularListBucketFlatMap<>() );
    }
}
