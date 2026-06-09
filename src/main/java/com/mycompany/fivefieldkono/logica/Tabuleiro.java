package com.mycompany.fivefieldkono.logica;

public class Tabuleiro {
    public static final int TAMANHO = 5;
    private final Celula[][] celulas = new Celula[TAMANHO][TAMANHO];

    public Tabuleiro() {
        for (int l = 0; l < TAMANHO; l++)
            for (int c = 0; c < TAMANHO; c++)
                celulas[l][c] = new Celula(l, c);
    }

    public Celula getCelula(int l, int c) { return celulas[l][c]; }

    private boolean dentro(int l, int c) {
        return l >= 0 && l < TAMANHO && c >= 0 && c < TAMANHO;
    }

    public boolean validarMovimento(int lo, int co, int ld, int cd) {
        if (!dentro(lo, co) || !dentro(ld, cd)) return false;
        if (celulas[lo][co].estaVazia())         return false;
        if (!celulas[ld][cd].estaVazia())         return false;

        int dl = Math.abs(ld - lo);
        int dc = Math.abs(cd - co);

        if (dl == 0 && dc == 0) return false;
        if (dl > 1  || dc > 1)  return false;

        boolean ortogonal = (dl + dc == 1);
        boolean diagonal  = (dl == 1 && dc == 1);

        if (ortogonal) return true;
        if (diagonal)  return celulas[lo][co].permiteDiagonal();
        return false;
    }

    public void mover(int lo, int co, int ld, int cd) {
        Peca p = celulas[lo][co].getPeca();
        celulas[lo][co].setPeca(null);
        celulas[ld][cd].setPeca(p);
    }
}