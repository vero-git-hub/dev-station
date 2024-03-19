package com.dev.station.logs;

import com.dev.station.model.SettingsModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonLogger {
    private static final String LOG_FILE = "ds_logs.json";

    public static synchronized void log(String level, String message) {
        if (!SettingsModel.loadDeveloperModeSetting()) {
            // If developer mode is disabled, then we do not perform logging
            return;
        }

        try {
            JSONArray logArray;
            File file = new File(LOG_FILE);

            if (file.exists() && file.length() != 0) {
                String content = new String(Files.readAllBytes(Paths.get(LOG_FILE)));
                logArray = new JSONArray(content);
            } else {
                logArray = new JSONArray();
                if (!file.exists()) {
                    file.createNewFile();
                }
            }

            JSONObject logEntry = new JSONObject();
            logEntry.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            logEntry.put("level", level);
            logEntry.put("message", message);

            logArray.put(logEntry);

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(logArray.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
