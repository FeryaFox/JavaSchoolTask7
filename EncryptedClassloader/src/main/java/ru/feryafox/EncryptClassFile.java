package ru.feryafox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptClassFile {
    private static final int KEY = 5; // Ключ шифрования

    public static void main(String[] args) {

        String inputF = "target\\classes\\ru\\feryafox\\plugins\\FooPlugin.class";
        String outputF = "plugins\\FooPlugin.enc";

        String pluginDirName = "plugins";
        File pluginDir = new File(pluginDirName);

        if (!pluginDir.exists()) {
            pluginDir.mkdir();
        }

        File inputFile = new File(inputF);
        File outputFile = new File(outputF);

        try {
            byte[] classBytes = readFile(inputFile);
            byte[] encryptedBytes = encrypt(classBytes);
            writeFile(outputFile, encryptedBytes);
            System.out.println("Class file encrypted successfully: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }

    private static byte[] encrypt(byte[] data) {
        byte[] encrypted = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encrypted[i] = (byte) (data[i] + KEY); // Шифрование байтов
        }
        return encrypted;
    }

    private static void writeFile(File file, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }
}

