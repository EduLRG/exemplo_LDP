package com.mycompany.fivefieldkono.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FiveFieldKono extends Application {

    @Override
    public void start(Stage stage) {
        Label titulo = new Label("Five Field Kono");
        titulo.setFont(Font.font(28));

        Button btnServidor = new Button("Criar Jogo (Servidor)");
        Button btnCliente  = new Button("Entrar no Jogo (Cliente)");

        btnServidor.setPrefWidth(220);
        btnCliente.setPrefWidth(220);

        btnServidor.setOnAction(e -> {
            try {
                // Percorre todas as interfaces para encontrar IP local correto
                String ipEncontrado = "Não encontrado";
                java.util.Enumeration<java.net.NetworkInterface> interfaces =
                    java.net.NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    java.net.NetworkInterface iface = interfaces.nextElement();
                    if (!iface.isUp() || iface.isLoopback()) continue;

                    java.util.Enumeration<java.net.InetAddress> enderecos =
                        iface.getInetAddresses();

                    while (enderecos.hasMoreElements()) {
                        java.net.InetAddress addr = enderecos.nextElement();
                        String ip = addr.getHostAddress();
                        if (!addr.isLoopbackAddress() && ip.contains(".") &&
                            (ip.startsWith("192.168") || ip.startsWith("10.") || ip.startsWith("172."))) {
                            ipEncontrado = ip;
                            break;
                        }
                    }
                }

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Servidor Criado");
                alerta.setHeaderText("Partilha este IP com o teu colega:");
                alerta.setContentText("📡 IP: " + ipEncontrado + "\n🔌 Porta: 6666\n\nAguarda que o colega se ligue...");
                alerta.show();
                abrirJogo(stage, true, "localhost");

            } catch (Exception ex) {
                abrirJogo(stage, true, "localhost");
            }
        });

        btnCliente.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("localhost");
            dialog.setTitle("Ligar ao Servidor");
            dialog.setHeaderText("Endereço IP do servidor:");
            dialog.showAndWait().ifPresent(ip -> abrirJogo(stage, false, ip.trim()));
        });

        VBox root = new VBox(20, titulo, btnServidor, btnCliente);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(
            new BackgroundFill(Color.web("#1a1a2e"), null, null)));

        titulo.setTextFill(Color.WHITE);
        btnServidor.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-font-size:14;");
        btnCliente.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white; -fx-font-size:14;");

        stage.setTitle("Five Field Kono");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    private void abrirJogo(Stage stage, boolean isServidor, String ip) {
        ControladorJogo controlador = new ControladorJogo(isServidor, ip);
        stage.setScene(controlador.criarCena());
        stage.setTitle("Five Field Kono — " + (isServidor ? "Servidor (J1)" : "Cliente (J2)"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}