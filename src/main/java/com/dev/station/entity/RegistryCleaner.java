package com.dev.station.entity;

import com.dev.station.util.AlertUtils;

import java.io.IOException;

public class RegistryCleaner {

    public static void deleteAppRegistryFolder(String registryPath) {
        String command = "powershell Remove-Item -Path 'HKCU:\\" + registryPath + "' -Recurse";
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                AlertUtils.showSuccessAlert("Success clean registry", "Registry folder deleted successfully.");
            } else {
                AlertUtils.showErrorAlert("Error clean registry", "Error occurred while deleting registry folder.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
