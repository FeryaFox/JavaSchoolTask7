package ru.feryafox;

import ru.feryafox.plugins.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EncryptedClassLoader extends ClassLoader {
    private final int key;
    private final File dir;

    public EncryptedClassLoader(String key, File dir, ClassLoader parent) {
        super(parent);
        this.key = Integer.parseInt(key);
        this.dir = dir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File classFile = new File(dir, name.replace('.', File.separatorChar) + ".enc");
        if (!classFile.exists()) {
            throw new ClassNotFoundException("Class not found: " + name);
        }

        try {
            // Читаем зашифрованный файл класса
            byte[] encryptedBytes = Files.readAllBytes(classFile.toPath());

            // Дешифруем данные
            byte[] decryptedBytes = decrypt(encryptedBytes);

            // Создаем класс из байтов
            return defineClass(name, decryptedBytes, 0, decryptedBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Failed to load class: " + name, e);
        }
    }

    private byte[] decrypt(byte[] data) {
        byte[] decrypted = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            decrypted[i] = (byte) (data[i] - key); // Применяем дешифрование
        }
        return decrypted;
    }

    public static void main(String[] args) {
        try {
            File dir = new File("plugins");
            EncryptedClassLoader loader = new EncryptedClassLoader("5", dir, ClassLoader.getSystemClassLoader());

            // Загружаем класс
            Class<?> loadedClass = loader.loadClass("ru.feryafox.plugins.FooPlugin");
            System.out.println("Class loaded: " + loadedClass.getName());
            Plugin plugin = (Plugin) loadedClass.newInstance();
            plugin.doSomething();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
