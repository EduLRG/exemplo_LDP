package com.mycompany.fivefieldkono.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Inteligência artificial que escolhe jogadas para o computador no modo
 * singleplayer do Five Field Kono.
 * <p>
 * Conforme a {@link Dificuldade}, joga ao acaso (FACIL) ou prefere
 * movimentos que aproximam as suas peças da zona de meta (NORMAL).
 *
 * @author Eduardo
 * @version 1.0
 */
public class IA {

    /**
     * Representa um movimento possível: da origem (lo,co) ao destino (ld,cd).
     */
    public static class Movimento {
        /** Linha de origem. */
        public final int lo;
        /** Coluna de origem. */
        public final int co;
        /** Linha de destino. */
        public final int ld;
        /** Coluna de destino. */
        public final int cd;

        /**
         * Cria um movimento.
         *
         * @param lo linha de origem
         * @param co coluna de origem
         * @param ld linha de destino
         * @param cd coluna de destino
         */
        public Movimento(int lo, int co, int ld, int cd) {
            this.lo = lo; this.co = co; this.ld = ld; this.cd = cd;
        }
    }

    /** Nível de dificuldade desta IA. */
    private final Dificuldade dificuldade;

    /** Jogador controlado pela IA. */
    private final Jogador jogadorIA;

    /** Gerador de números aleatórios. */
    private final Random random = new Random();

    /**
     * Cria uma IA para um jogador e nível de dificuldade.
     *
     * @param dificuldade nível de dificuldade
     * @param jogadorIA   jogador controlado pela IA
     */
    public IA(Dificuldade dificuldade, Jogador jogadorIA) {
        this.dificuldade = dificuldade;
        this.jogadorIA = jogadorIA;
    }

    /**
     * Escolhe a próxima jogada da IA com base no estado do jogo.
     *
     * @param jogo o jogo atual
     * @return o {@link Movimento} escolhido, ou {@code null} se não houver jogadas
     */
    public Movimento escolherJogada(Jogo jogo) {
        List<Movimento> possiveis = listarMovimentos(jogo.getTabuleiro());
        if (possiveis.isEmpty()) return null;

        if (dificuldade == Dificuldade.FACIL) {
            return possiveis.get(random.nextInt(possiveis.size()));
        } else {
            return melhorMovimentoNormal(possiveis);
        }
    }

    /**
     * Lista todos os movimentos válidos das peças da IA.
     *
     * @param tab o tabuleiro atual
     * @return lista de movimentos possíveis
     */
    private List<Movimento> listarMovimentos(Tabuleiro tab) {
        List<Movimento> lista = new ArrayList<>();
        int t = Tabuleiro.TAMANHO;
        for (int lo = 0; lo < t; lo++) {
            for (int co = 0; co < t; co++) {
                Celula c = tab.getCelula(lo, co);
                if (c.estaVazia() || c.getPeca().getDono() != jogadorIA) continue;
                for (int dl = -1; dl <= 1; dl++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dl == 0 && dc == 0) continue;
                        int ld = lo + dl, cd = co + dc;
                        if (tab.validarMovimento(lo, co, ld, cd)) {
                            lista.add(new Movimento(lo, co, ld, cd));
                        }
                    }
                }
            }
        }
        return lista;
    }

    /**
     * Escolhe o movimento que mais aproxima a peça da zona de meta da IA.
     * Em caso de empate, escolhe ao acaso entre os melhores.
     *
     * @param possiveis lista de movimentos válidos
     * @return o movimento preferido
     */
    private Movimento melhorMovimentoNormal(List<Movimento> possiveis) {
        int linhaMeta = (jogadorIA == Jogador.JOGADOR2) ? 0 : 4;

        List<Movimento> melhores = new ArrayList<>();
        int melhorGanho = Integer.MIN_VALUE;

        for (Movimento m : possiveis) {
            int distAntes  = Math.abs(m.lo - linhaMeta);
            int distDepois = Math.abs(m.ld - linhaMeta);
            int ganho = distAntes - distDepois;

            if (ganho > melhorGanho) {
                melhorGanho = ganho;
                melhores.clear();
                melhores.add(m);
            } else if (ganho == melhorGanho) {
                melhores.add(m);
            }
        }
        return melhores.get(random.nextInt(melhores.size()));
    }
}
