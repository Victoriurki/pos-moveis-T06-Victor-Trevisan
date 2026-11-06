package br.edu.utfpr.truco

/**
 * Estado do marcador em memoria (singleton Kotlin puro, sem Android).
 * Contem nomes, pontuacoes e partidas ganhas dos 2 jogadores e as
 * operacoes do enunciado: somar pontos, detectar vitoria (>= 12),
 * zerar tudo, resetar somente pontuacoes (apos a mensagem de vitoria)
 * e definir nomes.
 *
 * Indice de jogador: 0 = Jogador 1, 1 = Jogador 2.
 */
object Placar {

    /** Pontuacao minima para vencer a partida. */
    const val LIMITE_VITORIA: Int = 12

    private const val NUM_JOGADORES: Int = 2

    /**
     * Nomes correntes das duas equipes (0 = Jogador 1, 1 = Jogador 2).
     * String vazia significa "usar o nome padrao localizado"
     * (R.string.jogador_1 / R.string.jogador_2), resolvido pela UI
     * (MainActivity e HistoricoActivity) — o Placar nao conhece strings
     * de recurso, para nao quebrar o i18n.
     */
    val nomes: MutableList<String> = defaultNomes()

    /** Pontuacao corrente da partida em andamento. */
    val pontuacoes: IntArray = IntArray(NUM_JOGADORES)

    /** Quantidade de partidas ganhas por jogador desde o ultimo reinicio. */
    val partidasGanhas: IntArray = IntArray(NUM_JOGADORES)

    /**
     * Acrescenta [pontos] a pontuacao de [jogador]. Ao igualar ou ultrapassar
     * [LIMITE_VITORIA], a partida termina: incrementa o contador de partidas
     * ganhas do vencedor e devolve true. NAO zera as pontuacoes — isso fica
     * para [resetPontuacoes], chamada pela UI no fechamento da mensagem de
     * vitoria (requisito Q2: "zerando a pontuacao dos jogadores APÓS a
     * mensagem").
     *
     * @return true se o jogador venceu a partida.
     */
    fun adicionarPontos(jogador: Int, pontos: Int): Boolean {
        require(jogador in 0 until NUM_JOGADORES) { "jogador invalido: $jogador" }
        pontuacoes[jogador] += pontos
        return if (pontuacoes[jogador] >= LIMITE_VITORIA) {
            partidasGanhas[jogador]++
            true
        } else {
            false
        }
    }

    /**
     * Zera somente as pontuacoes da partida (nomes e contadores de partidas
     * ganhas sao preservados). Chamada pela MainActivity no dismiss do
     * AlertDialog de vitoria.
     */
    fun resetPontuacoes() {
        pontuacoes.fill(0)
    }

    /** Restaura todos os valores iniciais: pontuacoes, contadores e nomes. */
    fun zerarTudo() {
        resetPontuacoes()
        partidasGanhas.fill(0)
        nomes.clear()
        nomes.addAll(defaultNomes())
    }

    /** Atualiza os nomes das equipes informados pelo usuario. Nome em branco
     * (apos trim) grava string vazia = manter o nome padrao localizado. */
    fun definirNomes(nomeJogador1: String, nomeJogador2: String) {
        nomes[0] = nomeJogador1.trim().ifBlank { "" }
        nomes[1] = nomeJogador2.trim().ifBlank { "" }
    }

    private fun defaultNomes(): MutableList<String> = mutableListOf("", "")
}
