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

    public static Path getConfigFilePath() {
        return getConfigDir().resolve("config.properties");
    }
}
