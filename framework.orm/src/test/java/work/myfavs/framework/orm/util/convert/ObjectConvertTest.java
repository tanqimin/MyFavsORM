package work.myfavs.framework.orm.util.convert;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.AbstractTest;

public class ObjectConvertTest extends AbstractTest {
    
    @Test
    public void toCollection() {
        int[] array1 = {0, 1, 2};
        List<Integer> list1 = new ArrayList<>();
        list1.add(3);
        list1.add(4);

        Collection<?> coll1 = ObjectConvert.toCollection(array1);
        Assert.assertEquals(coll1.size(),  3);

        Collection<?> coll2 = ObjectConvert.toCollection(list1);
        Assert.assertEquals(coll2.size(),  2);

        for (Object o : coll1) {
            System.out.println(o);
        }

        for (Object o : coll2) {
            System.out.println(o);
        }
    }
}