package com.mycompany.fivefieldkono.ui.view;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Painel de estado da partida: mostra de quem é a vez e o tempo decorrido.
 * <p>
 * Junta a etiqueta de turno e o cronómetro num único componente visual.
 * O cronómetro usa um {@link AnimationTimer} que atualiza o tempo a cada
 * frame, conforme abordado nos conteúdos de animação em JavaFX.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class PainelEstado {

    /** Etiqueta que indica o jogador da vez. */
    private final Label labelTurno;

    /** Etiqueta com o tempo decorrido (mm:ss). */
    private final Label labelTempo;

    /** Caixa que agrupa as etiquetas. */
    private final HBox painel;

    /** Temporizador que atualiza o cronómetro. */
    private AnimationTimer cronometro;

    /** Instante de início da contagem, em milissegundos. */
    private long tempoInicio;

    /**
     * Cria o painel de estado com os valores iniciais.
     *
     * @param textoInicialTurno texto a mostrar antes do jogo arrancar
     */
    public PainelEstado(String textoInicialTurno) {
        labelTurno = new Label(textoInicialTurno);
        labelTurno.setFont(Font.font(16));
        labelTurno.setTextFill(Color.WHITE);

        labelTempo = new Label("00:00");
        labelTempo.setFont(Font.font(16));
        labelTempo.setTextFill(Color.LIGHTGRAY);

        painel = new HBox(30, labelTurno, labelTempo);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(10));
    }

    /**
     * Devolve o componente visual para colocar na cena.
     *
     * @return a {@link HBox} do painel
     */
    public HBox getPainel() {
        return painel;
    }

    /**
     * Atualiza o texto e a cor do indicador de turno.
     *
     * @param texto    texto a mostrar
     * @param destaque {@code true} para verde (tua vez), {@code false} para cinzento
     */
    public void setTurno(String texto, boolean destaque) {
        labelTurno.setText(texto);
        labelTurno.setTextFill(destaque ? Color.LIGHTGREEN : Color.LIGHTGRAY);
    }

    /**
     * Define um texto simples no indicador de turno (cor branca).
     *
     * @param texto texto a mostrar
     */
    public void setMensagem(String texto) {
        labelTurno.setText(texto);
        labelTurno.setTextFill(Color.WHITE);
    }

    /**
     * Arranca o cronómetro a partir de zero.
     */
    public void iniciarCronometro() {
        tempoInicio = System.currentTimeMillis();
        cronometro = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long decorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
                labelTempo.setText(String.format("%02d:%02d", decorrido / 60, decorrido % 60));
            }
        };
        cronometro.start();
    }

    /**
     * Para o cronómetro, se estiver a correr.
     */
    public void pararCronometro() {
        if (cronometro != null) cronometro.stop();
    }
}
