package com.mycompany.fivefieldkono.logica;

public class Celula {
    private final int linha;
    private final int coluna;
    private Peca peca;

    public Celula(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha()            { return linha; }
    public int getColuna()           { return coluna; }
    public Peca getPeca()            { return peca; }
    public void setPeca(Peca p)      { this.peca = p; }
    public boolean estaVazia()       { return peca == null; }

    /** Diagonal só permitida quando (linha+coluna) é par */
    public boolean permiteDiagonal() { return (linha + coluna) % 2 == 0; }
}