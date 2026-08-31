package com.cosmical.launcher;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainLauncher {
    public static void main(String[] args) {
        System.out.println("[Cosmical] Apertura Login Microsoft sicuro...");
        try {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithWebview();
            
            String tokenMicrosoft = result.getAccessToken();
            String uuidGiocatore = result.getProfile().getId();
            String usernameGiocatore = result.getProfile().getName();
            
            System.out.println("[Cosmical] Login effettuato! Benvenuto " + usernameGiocatore);

            String appData = System.getenv("APPDATA");
            String gameDir = appData + File.separator + ".minecraft";
            String assetsDir = gameDir + File.separator + "assets";
            String clientJar = gameDir + File.separator + "versions" + File.separator + "26.2-Fabric" + File.separator + "26.2-Fabric.jar"; 

            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx4G");
            command.add("-cp");
            command.add(clientJar); 
            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");

            command.add("--username");    command.add(usernameGiocatore);
            command.add("--uuid");        command.add(uuidGiocatore);
            command.add("--accessToken"); command.add(tokenMicrosoft);
            command.add("--userType");    command.add("msa");

            command.add("--version");     command.add("26.2");
            command.add("--gameDir");     command.add(gameDir);
            command.add("--assetsDir");   command.add(assetsDir);
            command.add("--assetIndex");  command.add("26.2");

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(gameDir));
            pb.inheritIO();
            pb.start();

        } catch (MicrosoftAuthenticationException e) {
            System.out.println("Errore login Microsoft: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
