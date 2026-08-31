package com.cosmical.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class MainLauncher {
    public static void main(String[] args) {
        // Finestra di input pulita stile Windows 10 per inserire il nickname
        String usernameGiocatore = JOptionPane.showInputDialog(null, "Inserisci il tuo Nome Utente Cosmical:", "Cosmical Client Login", JOptionPane.QUESTION_MESSAGE);
        
        if (usernameGiocatore == null || usernameGiocatore.trim().isEmpty()) {
            usernameGiocatore = "CosmicalPlayer";
        }

        try {
            String appData = System.getenv("APPDATA");
            String gameDir = appData + File.separator + ".minecraft";
            String assetsDir = gameDir + File.separator + "assets";
            String clientJar = gameDir + File.separator + "versions" + File.separator + "26.2-Fabric" + File.separator + "26.2-Fabric.jar"; 

            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx4G"); // 4GB di RAM dedicati per non laggare nel Chaos Cubed
            command.add("-cp");
            command.add(clientJar); 
            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");

            // Parametri di avvio stabili
            command.add("--username");    command.add(usernameGiocatore);
            command.add("--uuid");        command.add("00000000-0000-0000-0000-000000000000");
            command.add("--accessToken"); command.add("00000000000000000000000000000000");
            command.add("--userType");    command.add("msa");

            command.add("--version");     command.add("26.2");
            command.add("--gameDir");     command.add(gameDir);
            command.add("--assetsDir");   command.add(assetsDir);
            command.add("--assetIndex");  command.add("26.2");

            System.out.println("[Cosmical] Lancio in corso per l'utente: " + usernameGiocatore);
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(gameDir));
            pb.inheritIO();
            pb.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
