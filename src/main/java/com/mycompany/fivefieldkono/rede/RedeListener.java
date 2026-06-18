package com.mycompany.fivefieldkono.rede;

/**
 * Define os métodos de notificação que a interface implementa para
 * reagir a eventos de rede.
 * <p>
 * Tanto o {@link Servidor} como o {@link Cliente} comunicam com a
 * camada gráfica através desta interface, avisando quando a ligação é
 * estabelecida, quando chega uma mensagem do adversário ou quando
 * ocorre um erro de comunicação.
 *
 * @author Eduardo
 * @version 1.0
 */
public interface RedeListener {

    /**
     * Chamado quando a ligação muda de estado (à escuta ou adversário ligado).
     *
     * @param mensagem texto descritivo do estado da ligação
     */
    void onConectado(String mensagem);

    /**
     * Chamado quando chega uma mensagem do outro computador.
     *
     * @param mensagem o conteúdo recebido (por exemplo uma jogada)
     */
    void onMensagem(String mensagem);

    /**
     * Chamado quando ocorre um erro de comunicação.
     *
     * @param erro descrição do erro ocorrido
     */
    void onErro(String erro);
}
