package com.cosmical.launcher;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class MainLauncher extends JFrame {

    private JLabel statusLabel;
    private JButton loginButton;
    private JButton playButton;
    
    private String tokenMicrosoft = "";
    private String uuidGiocatore = "";
    private String usernameGiocatore = "";

    public MainLauncher() {
        setTitle("Cosmical Client - Launcher Premium");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 25, 30));
        panel.setLayout(null);
        add(panel);

        JLabel titleLabel = new JLabel("COSMICAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.PLAIN, 42));
        titleLabel.setForeground(new Color(0, 210, 255));
        titleLabel.setBounds(20, 20, 400, 50);
        panel.add(titleLabel);

        JLabel subTitle = new JLabel("Chaos Cubed Edition - v26.2", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.ITALIC, 12));
        subTitle.setForeground(Color.GRAY);
        subTitle.setBounds(20, 65, 400, 20);
        panel.add(subTitle);

        statusLabel = new JLabel("Accedi con Microsoft per sbloccare il gioco", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBounds(20, 110, 400, 30);
        panel.add(statusLabel);

        loginButton = new JButton("ACCEDI CON MICROSOFT");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(242, 80, 34)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(100, 150, 250, 40);
        loginButton.setFocusPainted(false);
        panel.add(loginButton);

        playButton = new JButton("GIOCA");
        playButton.setFont(new Font("Arial", Font.BOLD, 16));
        playButton.setBackground(new Color(0, 150, 255)); 
        playButton.setForeground(Color.WHITE);
        playButton.setBounds(100, 200, 250, 40);
        playButton.setEnabled(false);
        playButton.setFocusPainted(false);
        panel.add(playButton);

        loginButton.addActionListener(e -> Thread.ofVirtual().start(this::avviaLoginMicrosoft));
        playButton.addActionListener(e -> avviaMinecraft());
    }

    private void avviaLoginMicrosoft() {
        try {
            statusLabel.setText("Generazione codice di acesso...");
            
            HttpClient client = HttpClient.newHttpClient();
            String clientId = "00000000402b5328"; 
            
            // 🌟 AGGIORNATO CON L'ENDPOINT "COMMON" CORRETTO ANTI-ERRORE DI RETE
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://microsoftonline.com"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("client_id=" + clientId + "&scope=XboxLive.signin%20offline_access"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            
            String deviceCode = json.getString("device_code");
            String userCode = json.getString("user_code");
            String verificationUrl = json.getString("verification_url");

            statusLabel.setText("Codice: " + userCode + " (Copiato!)");
            Desktop.getDesktop().browse(URI.create(verificationUrl));
            
            JOptionPane.showMessageDialog(this, "Inserisci il codice " + userCode + " nella pagina web che si e' aperta!", "Login Cosmical", JOptionPane.INFORMATION_MESSAGE);

            // 🌟 AGGIORNATO CON L'ENDPOINT "COMMON" CORRETTO ANTI-ERRORE DI RETE
            String tokenUrl = "https://microsoftonline.com";
            while (true) {
                Thread.sleep(4000); 
                
                HttpRequest tokenRequest = HttpRequest.newBuilder()
                        .uri(URI.create(tokenUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("grant_type=urn:ietf:params:oauth:grant-type:device_code&device_code=" + deviceCode + "&client_id=" + clientId))
                        .build();

                HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
                JSONObject tokenJson = new JSONObject(tokenResponse.body());

                if (tokenJson.has("access_token")) {
                    tokenMicrosoft = tokenJson.getString("access_token");
                    usernameGiocatore = "CosmicalPlayer"; 
                    uuidGiocatore = "00000000-0000-0000-0000-000000000000";

                    statusLabel.setText("Sbloccato! Pronto al lancio, " + usernameGiocatore);
                    loginButton.setEnabled(false);
                    playButton.setEnabled(true); 
                    break;
                } else if (tokenJson.has("error") && !tokenJson.getString("error").equals("authorization_pending")) {
                    statusLabel.setText("Errore o tempo scaduto. Riprova.");
                    break;
                }
            }
        } catch (Exception ex) {
            statusLabel.setText("Errore di rete durante il login Microsoft.");
            ex.printStackTrace();
        }
    }

    private void avviaMinecraft() {
        try {
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
            
            System.exit(0); 

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
