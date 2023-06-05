package com.nure.tariffmanage.utill;

import java.io.*;

public class UserSessionWriter {
    private static final String fileName = "LocalData\\data.dat";

    public static void saveDataToFile(int i) {
        try  {
            File file = new File(fileName);
            if (!file.exists()){
                File dir = new File("LocalData");
                if (!dir.mkdir()){
                    System.err.println("Error creating directory!");
                }
            }
            DataOutputStream oos = new DataOutputStream(new FileOutputStream(fileName, false));
            oos.writeInt(i);
        } catch (IOException e) {
            System.out.println("Error saving");
        }
    }

    public static int loadDataFromFile() {
        int obj = 0;
        try (DataInputStream ois = new DataInputStream(new FileInputStream(fileName))) {
            obj = ois.readInt();
        } catch (IOException e) {
            if (e.getClass() == FileNotFoundException.class) {
                System.err.println("No data file detected");
                return 0;
            }
        }
        return obj;
    }
}