package bgu.spl.mics.application.passiveObjects;

import java.io.*;

/**
 * A Class that helps Serializing any object, prevents redundant code for each serialization
 */
public class SerializedPrinter {

    public SerializedPrinter(){}

    /**
     * Serializes an object and create a file of this object named 'filename'
     * @param o - the object to be serialized
     * @param filename - the name of the file to be created
     */
    public void printToFile(Object o, String filename){
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
            fos.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * A function used ONLY for debugging, deserializng the serialized object that were created at the end of the main Thread
     */
    public static Object deSerialization(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

}
