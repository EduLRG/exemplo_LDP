package com.mycompany.fivefieldkono.ui.view;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

/**
 * Aplica um estilo visual consistente às janelas {@link Alert} da aplicação,
 * combinando com a paleta escura azulada do tabuleiro do Five Field Kono.
 * <p>
 * Centralizar o estilo aqui evita repetir o mesmo CSS em cada janela e
 * garante que todos os diálogos (dificuldade, fim de jogo, servidor) têm
 * o mesmo aspeto.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public final class EstiloAlert {

    /** Construtor privado: classe apenas com métodos estáticos. */
    private EstiloAlert() { }

    /**
     * Aplica o estilo do jogo a um {@link Alert}.
     * <p>
     * Define fundo escuro, texto claro e botões a combinar com a interface.
     *
     * @param alert o alerta a estilizar
     */
    public static void aplicar(Alert alert) {
        DialogPane pane = alert.getDialogPane();

        pane.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-border-color: #0f3460;" +
            "-fx-border-width: 2;"
        );

        pane.lookupAll(".label").forEach(node ->
            node.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px;")
        );

        pane.getButtonTypes().forEach(bt -> {
            var botao = pane.lookupButton(bt);
            if (botao != null) {
                botao.setStyle(
                    "-fx-background-color: #0f3460;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-cursor: hand;"
                );
            }
        });
    }
}