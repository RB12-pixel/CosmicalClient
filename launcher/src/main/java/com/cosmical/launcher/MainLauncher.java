package com.cosmical.launcher;

import com.microsoft.aad.msal4j.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        loginButton.addActionListener(e -> Thread.ofVirtual().start(this::avviaLoginMicrosoftUfficiale));
        playButton.addActionListener(e -> avviaMinecraft());
    }

    private void avviaLoginMicrosoftUfficiale() {
        try {
            statusLabel.setText("Apertura pagina di login sicura...");
            
            // ID applicazione ufficiale di Minecraft Xbox per il login Premium
            String clientId = "00000000402b5328"; 
            String authority = "https://login.microsoftonline.com/common/";

            PublicClientApplication app = PublicClientApplication.builder(clientId)
                    .authority(authority)
                    .build();

            // Apriamo il flusso interattivo ufficiale di Microsoft
            InteractiveRequestParameters parameters = InteractiveRequestParameters.builder(new URI("http://localhost"))
                    .scopes(Collections.singleton("XboxLive.signin"))
                    .build();

            CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
            IAuthenticationResult result = future.get();

            // Login riuscito tramite la finestra Microsoft originale!
            tokenMicrosoft = result.accessToken();
            usernameGiocatore = result.account().username().split("@")[0]; // Prende la prima parte dell'email come nick di test
            uuidGiocatore = "00000000-0000-0000-0000-000000000000";

            statusLabel.setText("Sbloccato! Benvenuto su Cosmical, " + usernameGiocatore);
            loginButton.setEnabled(false);
            playButton.setEnabled(true);

        } catch (Exception ex) {
            statusLabel.setText("Errore durante l'accesso Microsoft.");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossibile completare l'accesso Premium. Riprova.", "Errore Cosmical", grandfather(ex));
        }
    }

    private int grandfather(Exception e) {
        return JOptionPane.ERROR_MESSAGE;
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

            // Passiamo i token premium ufficiali e legali al 100% [com.myclient.launcher.MinecraftLauncher]
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
