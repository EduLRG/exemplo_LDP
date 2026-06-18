package com.mycompany.fivefieldkono.ui.control;

import com.mycompany.fivefieldkono.logica.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Gere a jogada do computador no modo singleplayer, incluindo a pequena
 * pausa antes de jogar para tornar a experiência mais natural.
 * <p>
 * Encapsula a {@link IA} e usa um {@link PauseTransition} para que o
 * computador "pense" cerca de meio segundo antes de executar a jogada.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class GestorIA {

    /** Inteligência artificial que decide as jogadas. */
    private final IA ia;

    /**
     * Cria o gestor da IA.
     *
     * @param dificuldade nível de dificuldade
     * @param jogadorIA   jogador controlado pelo computador
     */
    public GestorIA(Dificuldade dificuldade, Jogador jogadorIA) {
        this.ia = new IA(dificuldade, jogadorIA);
    }

    /**
     * Calcula e executa a jogada da IA após uma breve pausa.
     *
     * @param jogo        o jogo atual
     * @param aoTerminar  ação a correr depois da jogada (ex: redesenhar)
     */
    public void jogar(Jogo jogo, Runnable aoTerminar) {
        PauseTransition pausa = new PauseTransition(Duration.seconds(0.5));
        pausa.setOnFinished(ev -> {
            IA.Movimento m = ia.escolherJogada(jogo);
            if (m != null) {
                jogo.jogar(m.lo, m.co, m.ld, m.cd);
            }
            aoTerminar.run();
        });
        pausa.play();
    }
}
