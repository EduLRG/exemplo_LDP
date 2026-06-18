package com.mycompany.fivefieldkono.logica;

/**
 * Representa cada um dos dois jogadores da partida de Five Field Kono.
 * <p>
 * O jogo é disputado entre {@code JOGADOR1} e {@code JOGADOR2}. Esta
 * enumeração também sabe identificar quem é o adversário de um jogador.
 *
 * @author Eduardo
 * @version 1.0
 */
public enum Jogador {

    /** Primeiro jogador. Por convenção, ocupa as linhas 0 e 1 e joga primeiro. */
    JOGADOR1,

    /** Segundo jogador. Por convenção, ocupa as linhas 3 e 4. */
    JOGADOR2;

    /**
     * Devolve o jogador adversário deste.
     *
     * @return {@code JOGADOR2} se este for {@code JOGADOR1}, e vice-versa
     */
    public Jogador adversario() {
        return this == JOGADOR1 ? JOGADOR2 : JOGADOR1;
    }
}
