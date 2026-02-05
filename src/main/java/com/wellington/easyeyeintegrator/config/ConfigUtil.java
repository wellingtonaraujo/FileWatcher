package com.wellington.easyeyeintegrator.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

public class ConfigUtil {

    public static Path getConfigDir() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isEmpty()) {
                return Paths.get(appData, "EasyEyeIntegrator", "config");
            }
        }

        return Paths.get(System.getProperty("user.home"), ".easyeyeintegrator", "config");
    }

    public static Path getResourcesDir() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isEmpty()) {
                return Paths.get(appData, "EasyEyeIntegrator", "resources");
            }
        }

        return Paths.get(System.getProperty("user.home"), ".easyeyeintegrator", "resources");
    }

    public static Path getMonitoredDir() {
        String os = System.getProperty("os.name").toLowerCase();
        Path dir;

        if (os.contains("win")) {
            dir = Paths.get("C:", "EasyEyeIntegrator", "monitored");
        } else {
            dir = Paths.get(System.getProperty("user.home"), "EasyEyeIntegrator", "monitored");
        }

        try {
            Files.createDirectories(dir);
            createFolderExams(dir.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dir;
    }

    public static Path getConfigFilePath() {
        return getConfigDir().resolve("config.properties");
    }

    public static boolean createPastaBackup(String pastaBackup) {
        if (pastaBackup == null || pastaBackup.trim().isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(pastaBackup, "EasyEyeIntegrator", "backup", "exames");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            createFolderExams(path.toString());
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFolderExams(String path) {
        try {
            Files.createDirectories(Paths.get(path, "pentacam"));
            Files.createDirectories(Paths.get(path, "pachycam"));
            Files.createDirectories(Paths.get(path, "keratograph"));
            Files.createDirectories(Paths.get(path, "especular"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String gerarIdComputador() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String os = System.getProperty("os.name", "unknown");

            String mac = "";
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface ni = nics.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;

                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes == null || macBytes.length == 0) continue;

                StringBuilder sb = new StringBuilder();
                for (byte b : macBytes) {
                    sb.append(String.format("%02X", b));
                }
                mac = sb.toString();
                break;
            }

            String base = hostname + "|" + os + "|" + mac;
            UUID uuid = UUID.nameUUIDFromBytes(base.getBytes(StandardCharsets.UTF_8));
            String raw = uuid.toString().replace("-", "").toUpperCase();

            return "PC-" + raw.substring(0, 10);

        } catch (Exception e) {
            String random = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            return "PC-" + random.substring(0, 10);
        }
    }
}
