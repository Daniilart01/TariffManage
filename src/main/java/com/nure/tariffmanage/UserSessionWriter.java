package com.nure.tariffmanage;

import java.io.*;

public class UserSessionWriter {
    private static final String fileName = "src\\main\\resources\\com\\nure\\tariffmanage\\data.txt";

    public static void saveDataToFile(int i) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName, false))) {
            oos.writeInt(i);
        } catch (IOException e) {
            System.out.println("Error saving");
        }
    }

    public static int loadDataFromFile() {
        int obj = 0;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            obj = ois.readInt();
        } catch (IOException e) {
            if (e.getClass() == FileNotFoundException.class) {
                return 0;
            }
        }
        return obj;
    }

    public static void clearFile() {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.setLength(0);
        } catch (IOException e) {
            System.out.println("Error clearing file!");
        }
    }
}
