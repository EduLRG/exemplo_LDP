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
            var urlMover = GestorSom.class.getResource("/sons/mover.mp3");
            var urlVitoria = GestorSom.class.getResource("/sons/vitoria.mp3");

            if (urlMover != null) {
                somMover = new AudioClip(urlMover.toExternalForm());
            } else {
                System.out.println("AVISO: /sons/mover.mp3 não encontrado");
            }

            if (urlVitoria != null) {
                somVitoria = new AudioClip(urlVitoria.toExternalForm());
            } else {
                System.out.println("AVISO: /sons/vitoria.mp3 não encontrado");
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar sons: " + e.getMessage());
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
