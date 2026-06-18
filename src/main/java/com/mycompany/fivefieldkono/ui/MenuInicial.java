package com.mycompany.fivefieldkono.ui;

import com.mycompany.fivefieldkono.logica.Dificuldade;
import com.mycompany.fivefieldkono.ui.control.ControladorJogo;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * Constrói e gere o menu inicial da aplicação.
 * <p>
 * Oferece as opções de jogar contra o computador (escolhendo a
 * dificuldade), criar um jogo em rede como servidor, entrar num jogo como
 * cliente, ou sair. Cada opção abre a cena correspondente na janela
 * principal.
 *
 * @author Eduardo
 * @version 1.0
 */
public class MenuInicial {

    /** Aplicação principal, usada para abrir partidas e trocar de cena. */
    private final FiveFieldKono app;

    /** Janela principal onde as cenas são mostradas. */
    private final Stage stage;

    /**
     * Cria o menu inicial.
     *
     * @param app   a aplicação principal
     * @param stage a janela principal
     */
    public MenuInicial(FiveFieldKono app, Stage stage) {
        this.app = app;
        this.stage = stage;
    }

    /**
     * Constrói a cena do menu inicial.
     *
     * @return a {@link Scene} do menu
     */
    public Scene criarCena() {
        Label titulo = new Label("Five Field Kono");
        titulo.setFont(Font.font(28));
        titulo.setTextFill(Color.WHITE);

        Button btnSingle   = new Button("Singleplayer");
        Button btnServidor = new Button("Criar Jogo (Servidor)");
        Button btnCliente  = new Button("Entrar no Jogo (Cliente)");
        Button btnSair      = new Button("Sair");

        for (Button b : new Button[]{ btnSingle, btnServidor, btnCliente, btnSair })
            b.setPrefWidth(240);

        btnSingle.setOnAction(e -> escolherDificuldade());
        btnServidor.setOnAction(e -> abrirRede(true, "localhost"));
        btnCliente.setOnAction(e -> pedirEnderecoCliente());
        btnSair.setOnAction(e -> { Platform.exit(); System.exit(0); });

        VBox root = new VBox(16, titulo, btnSingle, btnServidor, btnCliente, btnSair);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(
            new BackgroundFill(Color.web("#1a1a2e"), null, null)));

        btnSingle.setStyle("-fx-background-color:#16a34a; -fx-text-fill:white; -fx-font-size:14;");
        btnServidor.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-font-size:14;");
        btnCliente.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white; -fx-font-size:14;");
        btnSair.setStyle("-fx-background-color:#444; -fx-text-fill:white; -fx-font-size:14;");

        return new Scene(root, 420, 400);
    }

    /**
     * Mostra um diálogo para o utilizador escolher a dificuldade e abre a
     * partida singleplayer correspondente.
     */
    private void escolherDificuldade() {
        ButtonType facil  = new ButtonType("Fácil");
        ButtonType normal = new ButtonType("Normal");
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Singleplayer");
        dialog.setHeaderText("Escolhe a dificuldade do computador:");
        dialog.getButtonTypes().setAll(facil, normal, cancelar);

        Optional<ButtonType> escolha = dialog.showAndWait();
        if (escolha.isPresent()) {
            if (escolha.get() == facil)  abrirSingleplayer(Dificuldade.FACIL);
            else if (escolha.get() == normal) abrirSingleplayer(Dificuldade.NORMAL);
        }
    }

    /**
     * Abre uma partida contra o computador.
     *
     * @param dificuldade nível escolhido
     */
    private void abrirSingleplayer(Dificuldade dificuldade) {
        ControladorJogo c = new ControladorJogo(app, dificuldade);
        stage.setScene(c.criarCena());
        stage.setTitle("Five Field Kono — Singleplayer (" +
            (dificuldade == Dificuldade.FACIL ? "Fácil" : "Normal") + ")");
    }

    /**
     * Pede ao utilizador o endereço IP:PORTA e abre a partida como cliente.
     */
    private void pedirEnderecoCliente() {
        TextInputDialog dialog = new TextInputDialog("192.168.1.1:6666");
        dialog.setTitle("Ligar ao Servidor");
        dialog.setHeaderText("Escreve o IP e PORTA do servidor:");
        dialog.setContentText("Formato IP:PORTA");
        dialog.showAndWait().ifPresent(txt -> abrirRede(false, txt.trim()));
    }

    /**
     * Abre uma partida em rede.
     *
     * @param isServidor {@code true} para servidor, {@code false} para cliente
     * @param dados      "localhost" no servidor, ou "IP:PORTA" no cliente
     */
    private void abrirRede(boolean isServidor, String dados) {
        ControladorJogo c = new ControladorJogo(app, isServidor, dados);
        stage.setScene(c.criarCena());
        stage.setTitle("Five Field Kono — " + (isServidor ? "Servidor (J1)" : "Cliente (J2)"));
    }
}
