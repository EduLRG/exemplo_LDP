package com.mycompany.fivefieldkono.ui.control;

import com.mycompany.fivefieldkono.logica.*;
import com.mycompany.fivefieldkono.rede.RedeListener;
import com.mycompany.fivefieldkono.ui.FiveFieldKono;
import com.mycompany.fivefieldkono.ui.view.EstiloAlert;
import com.mycompany.fivefieldkono.ui.view.PainelEstado;
import com.mycompany.fivefieldkono.ui.view.TabuleiroView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Coordena uma partida de Five Field Kono, ligando a lógica do jogo às
 * vistas (tabuleiro e painel de estado) e ao adversário (rede ou IA).
 * <p>
 * Esta classe foca-se na coordenação: trata os cliques do utilizador,
 * gere os turnos e reage aos eventos. O desenho está em
 * {@link TabuleiroView} e {@link PainelEstado}, a rede em
 * {@link GestorRede} e a jogada do computador em {@link GestorIA}.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class ControladorJogo implements RedeListener {

    /** Aplicação principal, para regressar ao menu. */
    private final FiveFieldKono app;

    /** Lógica e estado da partida. */
    private final Jogo jogo = new Jogo();

    /** Vista do tabuleiro (desenho). */
    private final TabuleiroView tabuleiroView = new TabuleiroView();

    /** Painel de estado (turno e cronómetro). */
    private PainelEstado painelEstado;

    /** Jogador humano desta instância. */
    private final Jogador meuJogador;

    /** {@code true} se for partida contra o computador. */
    private final boolean singleplayer;

    /** Gestor da IA, no modo singleplayer. */
    private GestorIA gestorIA;

    /** Gestor de rede, no modo multijogador. */
    private GestorRede gestorRede;

    /** {@code true} se esta máquina é o servidor (modo rede). */
    private final boolean isServidor;

    /** Dados de ligação: "IP:PORTA" no cliente. */
    private final String dadosLigacao;

    /** Linha da célula selecionada, ou -1 se nenhuma. */
    private int selLinha = -1;

    /** Coluna da célula selecionada, ou -1 se nenhuma. */
    private int selColuna = -1;

    /**
     * Construtor para o modo singleplayer.
     *
     * @param app         a aplicação principal
     * @param dificuldade nível de dificuldade da IA
     */
    public ControladorJogo(FiveFieldKono app, Dificuldade dificuldade) {
        this.app          = app;
        this.singleplayer = true;
        this.isServidor   = false;
        this.dadosLigacao = null;
        this.meuJogador   = Jogador.JOGADOR1;
        this.gestorIA     = new GestorIA(dificuldade, Jogador.JOGADOR2);
    }

    /**
     * Construtor para o modo em rede.
     *
     * @param app        a aplicação principal
     * @param isServidor {@code true} se for o servidor (Jogador 1)
     * @param dados      "localhost" no servidor, ou "IP:PORTA" no cliente
     */
    public ControladorJogo(FiveFieldKono app, boolean isServidor, String dados) {
        this.app          = app;
        this.singleplayer = false;
        this.isServidor   = isServidor;
        this.dadosLigacao = dados;
        this.meuJogador   = isServidor ? Jogador.JOGADOR1 : Jogador.JOGADOR2;
    }

    /**
     * Constrói a cena completa da partida e arranca o jogo ou a ligação.
     *
     * @return a {@link Scene} da partida
     */
    public Scene criarCena() {
        tabuleiroView.getCanvas().setOnMouseClicked(
            e -> tratarClique(e.getX(), e.getY()));

        painelEstado = new PainelEstado(singleplayer ? "Começa!" : "A ligar...");

        VBox root = new VBox(
            painelEstado.getPainel(),
            tabuleiroView.getCanvas(),
            criarPainelControlo());
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(Color.web("#1a1a2e"), null, null)));

        redesenhar();

        if (singleplayer) {
            painelEstado.iniciarCronometro();
            atualizarTurno();
        } else {
            arrancarRede();
        }

        return new Scene(root);
    }

    /**
     * Cria o painel de controlo com os botões Consultar Regras e Desistir.
     *
     * @return a {@link HBox} do painel de controlo
     */
    private HBox criarPainelControlo() {
        Button btnRegras   = new Button("Consultar Regras");
        Button btnDesistir = new Button("Desistir");

        btnRegras.setOnAction(e -> mostrarRegras());
        btnDesistir.setOnAction(e -> confirmarDesistencia());

        btnRegras.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white; -fx-font-size:13;");
        btnDesistir.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-font-size:13;");

        HBox painel = new HBox(15, btnRegras, btnDesistir);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(10));
        return painel;
    }

    /**
     * Mostra a janela com as regras de movimento do jogo.
     */
    private void mostrarRegras() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Regras");
        alert.setHeaderText("Five Field Kono — Movimentos");
        alert.setContentText(
            "• Movimento ORTOGONAL (cima/baixo/esq/dir): sempre permitido para casa vazia adjacente.\n\n" +
            "• Movimento DIAGONAL: só permitido em interseções onde (linha + coluna) é PAR.\n\n" +
            "• Objetivo: levar as tuas 10 peças para as 2 linhas iniciais do adversário."
        );
        EstiloAlert.aplicar(alert);
        alert.showAndWait();
    }

    /**
     * Pede confirmação e, se confirmada, desiste e regressa ao menu.
     */
    private void confirmarDesistencia() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Tens a certeza que queres desistir?",
            ButtonType.YES, ButtonType.NO);
        EstiloAlert.aplicar(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (!singleplayer && gestorRede != null) gestorRede.enviar("DESISTIR");
                terminarEVoltar();
            }
        });
    }

    /**
     * Arranca a ligação de rede consoante o papel (servidor ou cliente).
     */
    private void arrancarRede() {
        if (isServidor) {
            gestorRede = new GestorRede(true, null, 0, this);
        } else {
            try {
                String[] partes = dadosLigacao.split(":");
                String host = partes[0];
                int porta = partes.length > 1
                    ? Integer.parseInt(partes[1])
                    : com.mycompany.fivefieldkono.rede.Servidor.PORTA_BASE;
                gestorRede = new GestorRede(false, host, porta, this);
            } catch (Exception ex) {
                painelEstado.setMensagem("IP:PORTA inválido");
            }
        }
    }

    /**
     * Reage a mudanças de estado da ligação.
     *
     * @param mensagem descrição do estado
     */
    @Override
    public void onConectado(String mensagem) {
        Platform.runLater(() -> {
            if (mensagem.equals("ADVERSARIO_LIGADO")) {
                painelEstado.iniciarCronometro();
                atualizarTurno();
                redesenhar();
            } else if (mensagem.startsWith("À escuta")) {
                mostrarInfoServidor();
            } else {
                painelEstado.setMensagem(mensagem);
            }
        });
    }

    /**
     * Mostra o IP e porta do servidor para o utilizador partilhar.
     */
    private void mostrarInfoServidor() {
        String ip = UtilRede.obterIpLocal();
        int porta = gestorRede.getPortaServidor();
        painelEstado.setMensagem("Aguarda adversário...");
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Servidor Criado");
        alerta.setHeaderText("Partilha isto com o teu colega:");
        alerta.setContentText("IP:PORTA = " + ip + ":" + porta + "\n\nAguarda que o colega se ligue...");
        EstiloAlert.aplicar(alerta);
        alerta.show();
    }

    /**
     * Recebe uma mensagem do adversário e trata-a na thread gráfica.
     *
     * @param msg a mensagem recebida
     */
    @Override
    public void onMensagem(String msg) {
        Platform.runLater(() -> interpretarMensagem(msg));
    }

    /**
     * Mostra um erro de rede no painel de estado.
     *
     * @param erro descrição do erro
     */
    @Override
    public void onErro(String erro) {
        Platform.runLater(() -> painelEstado.setMensagem("Erro: " + erro));
    }

    /**
     * Interpreta uma mensagem do adversário (jogada ou desistência).
     *
     * @param msg a mensagem a interpretar
     */
    private void interpretarMensagem(String msg) {
        if (msg.startsWith("MOVE:")) {
            String[] p = msg.substring(5).split(",");
            jogo.jogar(Integer.parseInt(p[0]), Integer.parseInt(p[1]),
                       Integer.parseInt(p[2]), Integer.parseInt(p[3]));
            redesenhar();
            atualizarTurno();
            if (jogo.isTerminado()) mostrarVencedor();
        } else if (msg.equals("DESISTIR")) {
            jogo.desistir(meuJogador.adversario());
            mostrarVencedor();
        }
    }

    /**
     * Trata um clique no tabuleiro: seleciona uma peça ou tenta movê-la.
     *
     * @param x coordenada X do clique
     * @param y coordenada Y do clique
     */
    private void tratarClique(double x, double y) {
        if (jogo.isTerminado()) return;
        if (jogo.getTurno() != meuJogador) return;

        int col = tabuleiroView.colunaDoClique(x);
        int lin = tabuleiroView.linhaDoClique(y);
        if (col < 0 || col >= Tabuleiro.TAMANHO) return;
        if (lin < 0 || lin >= Tabuleiro.TAMANHO) return;

        Celula celula = jogo.getTabuleiro().getCelula(lin, col);

        if (selLinha == -1) {
            if (!celula.estaVazia() && celula.getPeca().getDono() == meuJogador) {
                selLinha = lin;
                selColuna = col;
                redesenhar();
            }
        } else {
            boolean moveu = jogo.jogar(selLinha, selColuna, lin, col);
            if (moveu) {
                if (!singleplayer && gestorRede != null) {
                    gestorRede.enviar("MOVE:" + selLinha + "," + selColuna + "," + lin + "," + col);
                }
                limparSelecao();
                redesenhar();
                atualizarTurno();
                if (jogo.isTerminado()) {
                    mostrarVencedor();
                } else if (singleplayer) {
                    jogarComputador();
                }
            } else {
                if (!celula.estaVazia() && celula.getPeca().getDono() == meuJogador) {
                    selLinha = lin;
                    selColuna = col;
                } else {
                    limparSelecao();
                }
                redesenhar();
            }
        }
    }

    /**
     * Dispara a jogada do computador no modo singleplayer.
     */
    private void jogarComputador() {
        painelEstado.setTurno("O computador está a pensar...", false);
        gestorIA.jogar(jogo, () -> {
            redesenhar();
            atualizarTurno();
            if (jogo.isTerminado()) mostrarVencedor();
        });
    }

    /**
     * Limpa a seleção atual de célula.
     */
    private void limparSelecao() {
        selLinha = -1;
        selColuna = -1;
    }

    /**
     * Manda redesenhar o tabuleiro no estado atual.
     */
    private void redesenhar() {
        tabuleiroView.desenhar(jogo, selLinha, selColuna);
    }

    /**
     * Atualiza o painel de estado com o jogador da vez.
     */
    private void atualizarTurno() {
        if (jogo.isTerminado()) return;
        boolean minhavez = jogo.getTurno() == meuJogador;
        String texto;
        if (minhavez)            texto = "A TUA VEZ";
        else if (singleplayer)   texto = "Vez do computador";
        else                     texto = "Vez do adversário";
        painelEstado.setTurno(texto, minhavez);
    }

    /**
     * Mostra o resultado da partida e regressa ao menu.
     */
    private void mostrarVencedor() {
        painelEstado.pararCronometro();
        String msg = jogo.getVencedor() == meuJogador
            ? "Ganhaste! Parabéns!"
            : (singleplayer ? "O computador venceu!" : "Perdeste. O adversário venceu.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim do Jogo");
        alert.setHeaderText(msg);
        EstiloAlert.aplicar(alert);
        alert.showAndWait();
        terminarEVoltar();
    }

    /**
     * Fecha a rede, para o cronómetro e regressa ao menu inicial.
     */
    private void terminarEVoltar() {
        if (gestorRede != null) gestorRede.fechar();
        painelEstado.pararCronometro();
        app.voltarAoMenu();
    }
}