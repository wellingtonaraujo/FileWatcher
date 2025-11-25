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
            //Cria as pastas de exames monitorados
            ConfigUtil.createFolderExams(dir.toString());
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
    
    public static boolean createPastaBackup(String pastaBackup) {
        // Se for nulo ou vazio, não faz nada e retorna false
        if (pastaBackup == null || pastaBackup.trim().isEmpty()) {
            return false;
        }

        try {            
            Path path = Paths.get(pastaBackup, "FileWatcher","backup","exames") ;            
            System.out.println(path);
            System.out.println(Files.exists(path));

            // Se não existir, cria a pasta
            if (!Files.exists(path)) {                               
                Files.createDirectories(path);
            }
            
            //Cria as pastas de exames            
            ConfigUtil.createFolderExams(path.toString());
            //Retorna com verdadeiro            
            return true;

        } catch (IOException e) {
            e.printStackTrace(); // ou loga com seu logger
            return false;
        }
    }
    
    public static boolean createFolderExams(String path){
        Path especular = Paths.get(path, "especular");
        Path keratograph = Paths.get(path, "keratograph");
        Path pachycam = Paths.get(path, "pachycam");
        Path pentacam = Paths.get(path, "pentacam");
        
        try{
            //Se não existir cria pentacam
            if(!Files.exists(pentacam)) {
                Files.createDirectories(pentacam);
            }

            //Se não existir cria pachycam
            if(!Files.exists(pachycam)) {
                Files.createDirectories(pachycam);
            }

            //Se não existir cria keratograph
            if(!Files.exists(keratograph)) {
                Files.createDirectories(keratograph);
            }

            //Se não existir cria especular
            if(!Files.exists(especular)) {
                Files.createDirectories(especular);
            }

            return true;
        }catch (IOException e){
            e.printStackTrace(); // ou loga com seu logger
            return false;
        }
    }
}
