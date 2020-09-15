import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<String> f1;
    Future<Integer> f2;

    @Before
    public void setUp() throws Exception {
        f1 = new Future();
        f2 = new Future();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        f1.resolve("Done");
        String result1 = (String)f1.get();
        Assert.assertEquals("Done", result1);

        f2.resolve(3);
        int result2 = (int)f2.get();
        Assert.assertEquals("Done", result2);


    }

    @Test
    public void resolve() {
        f1.resolve("Done");
        String result1 = (String)f1.get();
        Assert.assertEquals("Done", result1);

        f2.resolve(3);
        int result2 = (int)f2.get();
        Assert.assertEquals("Done", result2);


    }

    @Test
    public void isDone() {
        Assert.assertFalse(f1.isDone());
        f1.resolve("Done");
        Assert.assertTrue(f1.isDone());
    }

    @Test
    public void get1() {
        String result = f1.get(0, TimeUnit.MILLISECONDS);
        f1.resolve("solved");
        String result2 = f1.get();
        Assert.assertNull(result);
        Assert.assertEquals("solved", result2);
    }
}