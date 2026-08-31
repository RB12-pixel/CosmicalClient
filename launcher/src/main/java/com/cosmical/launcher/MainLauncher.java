package com.cosmical.launcher;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainLauncher extends JFrame {

    private JLabel statusLabel;
    private JTextField usernameField;
    private JButton playButton;

    public MainLauncher() {
        // Configurazione Finestra di Cosmical
        setTitle("Cosmical Client - Launcher Autonomo");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Pannello scuro stile Lunar
        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 25, 30));
        panel.setLayout(null);
        add(panel);

        // Titolo Cosmical Azzurro
        JLabel titleLabel = new JLabel("COSMICAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.PLAIN, 42));
        titleLabel.setForeground(new Color(0, 210, 255));
        titleLabel.setBounds(20, 20, 400, 50);
        panel.add(titleLabel);

        // Sottotitolo versione
        JLabel subTitle = new JLabel("Chaos Cubed Edition - v26.2", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.ITALIC, 12));
        subTitle.setForeground(Color.GRAY);
        subTitle.setBounds(20, 65, 400, 20);
        panel.add(subTitle);

        statusLabel = new JLabel("Inserisci il tuo Nickname per giocare:", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBounds(20, 110, 400, 30);
        panel.add(statusLabel);

        // 🌟 NOVITÀ: Casella di testo elegante per il nome utente
        usernameField = new JTextField("CosmicalPlayer");
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBackground(new Color(40, 40, 45));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBounds(100, 145, 250, 35);
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(usernameField);

        // Pulsante GIOCA
        playButton = new JButton("GIOCA");
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(0, 150, 255)); // Azzurro Cosmical
        playButton.setForeground(Color.WHITE);
        playButton.setBounds(100, 200, 250, 45);
        playButton.setFocusPainted(false);
        panel.add(playButton);

        // Quando clicchi GIOCA, parte Minecraft all'istante
        playButton.addActionListener(e -> avviaMinecraft(usernameField.getText().trim()));
    }

    private void avviaMinecraft(String usernameGiocatore) {
        if (usernameGiocatore.isEmpty()) {
            usernameGiocatore = "CosmicalPlayer";
        }

        try {
            String appData = System.getenv("APPDATA");
            String gameDir = appData + File.separator + ".minecraft";
            String assetsDir = gameDir + File.separator + "assets";
            String clientJar = gameDir + File.separator + "versions" + File.separator + "26.2-Fabric" + File.separator + "26.2-Fabric.jar"; 

            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xmx4G"); // 4GB di RAM dedicati per non laggar nel mondo in LAN
            command.add("-cp");
            command.add(clientJar); 
            command.add("net.fabricmc.loader.impl.launch.knot.KnotClient");

            // Parametri di avvio istantanei offline esenti da errori di rete
            command.add("--username");    command.add(usernameGiocatore);
            command.add("--uuid");        command.add("00000000-0000-0000-0000-000000000000");
            command.add("--accessToken"); command.add("00000000000000000000000000000000");
            command.add("--userType");    command.add("mojang");

            command.add("--version");     command.add("26.2");
            command.add("--gameDir");     command.add(gameDir);
            command.add("--assetsDir");   command.add(assetsDir);
            command.add("--assetIndex");  command.add("26.2");

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(gameDir));
            pb.inheritIO();
            pb.start();
            
            System.exit(0); // Chiude la finestra di Cosmical quando il gioco si accende

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainLauncher().setVisible(true);
        });
    }
}
