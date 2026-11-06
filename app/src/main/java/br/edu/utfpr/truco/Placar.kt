package br.edu.utfpr.truco

/**
 * Estado do marcador em memoria (singleton Kotlin puro, sem Android).
 * Contem nomes, pontuacoes e partidas ganhas dos 2 jogadores e as
 * operacoes do enunciado: somar pontos, detectar vitoria (>= 12),
 * zerar tudo e definir nomes.
 *
 * Indice de jogador: 0 = Jogador 1, 1 = Jogador 2.
 */
object Placar {

    /** Pontuacao minima para vencer a partida. */
    const val LIMITE_VITORIA: Int = 12

    private const val NUM_JOGADORES: Int = 2

    /** Nomes correntes das duas equipes (0 = Jogador 1, 1 = Jogador 2). */
    val nomes: MutableList<String> = defaultNomes()

    /** Pontuacao corrente da partida em andamento. */
    val pontuacoes: IntArray = IntArray(NUM_JOGADORES)

    /** Quantidade de partidas ganhas por jogador desde o ultimo reinicio. */
    val partidasGanhas: IntArray = IntArray(NUM_JOGADORES)

    /**
     * Acrescenta [pontos] a pontuacao de [jogador]. Ao igualar ou ultrapassar
     * [LIMITE_VITORIA], a partida termina: incrementa o contador de partidas
     * ganhas do vencedor e zera a pontuacao dos dois jogadores.
     *
     * @return true se o jogador venceu a partida.
     */
    fun adicionarPontos(jogador: Int, pontos: Int): Boolean {
        require(jogador in 0 until NUM_JOGADORES) { "jogador invalido: $jogador" }
        pontuacoes[jogador] += pontos
        return if (pontuacoes[jogador] >= LIMITE_VITORIA) {
            partidasGanhas[jogador]++
            pontuacoes[0] = 0
            pontuacoes[1] = 0
            true
        } else {
            false
        }
    }

    /** Restaura todos os valores iniciais: pontuacoes, contadores e nomes. */
    fun zerarTudo() {
        pontuacoes.fill(0)
        partidasGanhas.fill(0)
        nomes.clear()
        nomes.addAll(defaultNomes())
    }

    /** Atualiza os nomes das equipes informados pelo usuario. */
    fun definirNomes(nomeJogador1: String, nomeJogador2: String) {
        nomes[0] = nomeJogador1
        nomes[1] = nomeJogador2
    }

    /** Nome padrao de um jogador (usado ao zerar e no estado inicial). */
    fun nomePadrao(jogador: Int): String = if (jogador == 0) "Jogador 1" else "Jogador 2"

    private fun defaultNomes(): MutableList<String> = mutableListOf(
        nomePadrao(0),
        nomePadrao(1),
    )
}
