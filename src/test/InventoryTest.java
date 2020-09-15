import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;

import static bgu.spl.mics.application.passiveObjects.OrderResult.*;
import static org.junit.Assert.*;

public class InventoryTest {
    BookInventoryInfo[] books;
    Inventory inventory;

    @Before
    public void setUp() throws Exception {
        books = new BookInventoryInfo[10];
        for(int i=0; i<books.length; i++)
            books[i] = new BookInventoryInfo("Book "+i, i, i*2);

        Field instance = Inventory.class.getDeclaredField("instance");
        instance.set(null, null);
        inventory = Inventory.getInstance();
        inventory.load(books);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void load() {
        OrderResult result;

        for(int i=1; i<9; i++) {
            result = inventory.take("Book "+i);
            Assert.assertEquals(SUCCESSFULLY_TAKEN, result);
        }
        result = inventory.take("Book 0");
        Assert.assertTrue(NOT_IN_STOCK == result);
        result = inventory.take("Book 1");
        Assert.assertEquals(NOT_IN_STOCK, result);

        int price = inventory.checkAvailabiltyAndGetPrice("Book 9");
        Assert.assertEquals(18, price);
    }

    @Test
    public void take() {
        OrderResult result;
        result = inventory.take("Book 1");
        Assert.assertEquals(SUCCESSFULLY_TAKEN, result);
        result = inventory.take("Book 1");
        Assert.assertEquals(NOT_IN_STOCK, result);
        result = inventory.take("Book 1");
        Assert.assertEquals(NOT_IN_STOCK, result);
        result = inventory.take("NotAvailable"); //!-- IS THAT THE CORRECT RESULT? OR EXCEPTION --!
        Assert.assertEquals(NOT_IN_STOCK, result);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        int price;

        price = inventory.checkAvailabiltyAndGetPrice("Book 9");
        Assert.assertEquals(18, price);

        price = inventory.checkAvailabiltyAndGetPrice("Book 0");
        Assert.assertEquals(-1, price);
    }

    @Test
    public void printInventoryToFile() {
        inventory.printInventoryToFile("serializedInventory");
        HashMap<String, Integer> hMap;
        try {
            FileInputStream fileIn = new FileInputStream("/serializedInventory.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            hMap = (HashMap<String, Integer>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }

        //i supposed to be the amount of book in the inventory
        for(int i=0; i<books.length; i++){
            Assert.assertEquals(i, (int)hMap.get("Book " + i));
        }
    }
}