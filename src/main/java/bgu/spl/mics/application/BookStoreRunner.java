package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        Gson gson = new Gson();
        HashMap<Integer, Customer> customersMap = new HashMap<>();
        List<Thread> threads = new LinkedList<>();
        RegisteredServices.getInstance().initializeRegistered();

        try {
            JsonObject inputFile = new JsonParser().parse(new FileReader(args[0])).getAsJsonObject();

            // ------------------------ Handling INVENTORY Load ------------------
            JsonArray booksJsonArray = inputFile.getAsJsonArray("initialInventory");
            BookInventoryInfo[] booksArray = gson.fromJson(booksJsonArray, BookInventoryInfo[].class);
            for(int i=0; i<booksArray.length; i++)
                booksArray[i].initializeSemaphore();
            Inventory.getInstance().load(booksArray);


            // ---------------------- Handling INITIAL RESOURCES ------------------
            JsonArray jsonVehicles = ((JsonObject) inputFile.getAsJsonArray("initialResources").get(0)).getAsJsonArray("vehicles");
            DeliveryVehicle[] dvArray = gson.fromJson(jsonVehicles, DeliveryVehicle[].class);
            ResourcesHolder.getInstance().load(dvArray);


            // --------------------------- Handling SERVICES ----------------------
            JsonObject services = inputFile.getAsJsonObject("services");

            int sellings = services.get("selling").getAsInt();
            for(int i=0; i<sellings; i++)
                threads.add(new Thread (new SellingService()));


            int inventoryServices = services.get("inventoryService").getAsInt();
            for(int i=0; i<inventoryServices; i++)
                threads.add(new Thread (new InventoryService()));

            int logistics = services.get("logistics").getAsInt();
            for(int i=0; i<logistics; i++)
                threads.add(new Thread (new LogisticsService()));

            int resourcesServices = services.get("resourcesService").getAsInt();
            for(int i=0; i<resourcesServices; i++)
                threads.add(new Thread (new ResourceService()));

            JsonArray customersJsonArray = services.getAsJsonArray("customers");
            for(JsonElement jElementCostumer : customersJsonArray){
                JsonObject jCostumer = jElementCostumer.getAsJsonObject();
                int id = jCostumer.get("id").getAsInt();
                String name = jCostumer.get("name").getAsString();
                String address = jCostumer.get("address").getAsString();
                int distance = jCostumer.get("distance").getAsInt();
                int creditCard = jCostumer.getAsJsonObject("creditCard").get("number").getAsInt();
                int credits = jCostumer.getAsJsonObject("creditCard").get("amount").getAsInt();
                Customer c = new Customer(id, name, address, distance, creditCard, credits);
                customersMap.put(id, c);

                HashMap<Integer, BlockingQueue<String>> orderSchedule = new HashMap<>();
                JsonArray jsonOrderSchedule = jCostumer.getAsJsonArray("orderSchedule");
                for (JsonElement jElementOrder : jsonOrderSchedule) {
                    String bookTitle = jElementOrder.getAsJsonObject().get("bookTitle").getAsString();
                    int tick = jElementOrder.getAsJsonObject().get("tick").getAsInt();
                    BlockingQueue<String> ref = orderSchedule.get(tick);
                    if (ref == null) {
                        orderSchedule.put(tick, new LinkedBlockingQueue<>());
                        ref = orderSchedule.get(tick);
                    }
                    try {
                        ref.put(bookTitle);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                threads.add(new Thread (new APIService(c, orderSchedule)));
            }


            threads.forEach(thread -> thread.start());

            JsonObject timerSettings = services.getAsJsonObject("time");
            int speed = timerSettings.get("speed").getAsInt();
            int duration = timerSettings.get("duration").getAsInt();
            Thread timerThread = new Thread(new TimeService(speed, duration));
            threads.add(timerThread);
            while(threads.size()-1 != RegisteredServices.getInstance().getRegistered()){}
            timerThread.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        threads.forEach(thread ->{
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        SerializedPrinter sp = new SerializedPrinter();

        sp.printToFile(customersMap, args[1]);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        sp.printToFile(MoneyRegister.getInstance(), args[4]);

    }


}
