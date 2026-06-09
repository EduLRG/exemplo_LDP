package com.mycompany.fivefieldkono.logica;

public enum Jogador {
    JOGADOR1, JOGADOR2;

    public Jogador adversario() {
        return this == JOGADOR1 ? JOGADOR2 : JOGADOR1;
    }
}