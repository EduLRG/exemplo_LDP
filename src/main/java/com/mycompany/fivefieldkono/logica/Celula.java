package com.mycompany.fivefieldkono.logica;

/**
 * Representa uma célula (casa) do tabuleiro 5x5 do Five Field Kono.
 * <p>
 * Cada célula conhece a sua posição (linha e coluna) e a peça que a
 * ocupa, ou {@code null} caso esteja vazia. Sabe ainda dizer se é uma
 * interseção onde os movimentos diagonais são permitidos.
 *
 * @author Eduardo
 * @version 1.0
 */
public class Celula {

    /** Linha da célula no tabuleiro (0 a 4). */
    private final int linha;

    /** Coluna da célula no tabuleiro (0 a 4). */
    private final int coluna;

    /** Peça que ocupa a célula, ou {@code null} se estiver vazia. */
    private Peca peca;

    /**
     * Cria uma célula numa dada posição do tabuleiro.
     *
     * @param linha  índice da linha (0 a 4)
     * @param coluna índice da coluna (0 a 4)
     */
    public Celula(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    /**
     * Devolve a linha desta célula.
     *
     * @return o índice da linha (0 a 4)
     */
    public int getLinha() { return linha; }

    /**
     * Devolve a coluna desta célula.
     *
     * @return o índice da coluna (0 a 4)
     */
    public int getColuna() { return coluna; }

    /**
     * Devolve a peça que ocupa esta célula.
     *
     * @return a {@link Peca} presente, ou {@code null} se a célula estiver vazia
     */
    public Peca getPeca() { return peca; }

    /**
     * Coloca (ou remove) uma peça nesta célula.
     *
     * @param p a peça a colocar, ou {@code null} para esvaziar a célula
     */
    public void setPeca(Peca p) { this.peca = p; }

    /**
     * Indica se a célula está vazia.
     *
     * @return {@code true} se não houver peça, {@code false} caso contrário
     */
    public boolean estaVazia() { return peca == null; }

    /**
     * Indica se esta célula permite movimentos diagonais.
     * <p>
     * Pela regra do Five Field Kono, as diagonais só são permitidas em
     * interseções onde a soma da linha com a coluna é um número par.
     *
     * @return {@code true} se {@code (linha + coluna)} for par
     */
    public boolean permiteDiagonal() { return (linha + coluna) % 2 == 0; }
}
