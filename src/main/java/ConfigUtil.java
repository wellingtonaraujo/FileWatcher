/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author welli
 */
package com.wellington.filewatcher;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtil {
    public static Path getConfigDir() {
        String os = System.getProperty("os.name").toLowerCase();

        // Windows → usa APPDATA
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA"); // ex: C:\Users\Usuario\AppData\Roaming
            if (appData != null && !appData.isEmpty()) {
                return Paths.get(appData, "FileWatcher", "config");
            }
        }

        // Fallback pra Linux/Mac ou se APPDATA não estiver setado
        return Paths.get(System.getProperty("user.home"), ".filewatcher", "config");
    }
    
    public static Path getMonitoredDir() {
        String os = System.getProperty("os.name").toLowerCase();
        Path dir;

        if (os.contains("win")) {
            // Windows: C:\FileWatcher\monitored
            dir = Paths.get("C:", "FileWatcher", "monitored");
        } else {
            // Linux / macOS: ~/FileWatcher/monitored
            String home = System.getProperty("user.home");
            dir = Paths.get(home, "FileWatcher", "monitored");
        }

        // Garante que o diretório exista
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
            // aqui você pode usar log ou até lançar uma RuntimeException, se quiser falhar forte
        }

        return dir;
    }

    public static Path getConfigFilePath() {
        return getConfigDir().resolve("config.properties");
    }
    
    public static Path getMonitoredPath() {
        return getMonitoredDir();
    }
}
