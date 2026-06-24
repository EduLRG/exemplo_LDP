package com.mycompany.fivefieldkono.ui.view;

import javafx.scene.media.AudioClip;

/**
 * Gere os efeitos sonoros do jogo Five Field Kono.
 * <p>
 * Carrega os ficheiros de áudio da pasta de recursos e disponibiliza
 * métodos para os reproduzir. Utiliza {@link AudioClip}, adequado para
 * efeitos curtos e repetidos como o som de mover uma peça.
 *
 * @author Eduardo
 * @version 1.0
 */
public final class GestorSom {

    /** Som reproduzido ao mover uma peça. */
    private static AudioClip somMover;

    /** Som reproduzido ao ganhar a partida. */
    private static AudioClip somVitoria;

    /** Construtor privado: classe apenas com métodos estáticos. */
    private GestorSom() { }

    /**
     * Carrega os ficheiros de som a partir da pasta de recursos.
     * Deve ser chamado uma vez no arranque da aplicação.
     */
    public static void carregar() {
        try {
            somMover = new AudioClip(
                GestorSom.class.getResource("/sons/mover.mp3").toExternalForm());
            somVitoria = new AudioClip(
                GestorSom.class.getResource("/sons/vitoria.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("Não foi possível carregar os sons: " + e.getMessage());
        }
    }

    /**
     * Reproduz o som de mover uma peça.
     */
    public static void tocarMover() {
        if (somMover != null) somMover.play();
    }

    /**
     * Reproduz o som de vitória.
     */
    public static void tocarVitoria() {
        if (somVitoria != null) somVitoria.play();
    }
}
