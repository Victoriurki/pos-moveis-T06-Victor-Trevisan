package br.edu.utfpr.truco

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitarios (JUnit4) da logica pura do marcador, extraida no singleton
 * [Placar] — nao dependem de nenhuma classe Android, rodam na JVM.
 *
 * API de nomes: string vazia significa "usar o nome padrao localizado"
 * (R.string.jogador_1 / R.string.jogador_2, resolvido pela UI).
 */
class PlacarTest {

    @Before
    fun setUp() {
        Placar.zerarTudo()
    }

    @Test
    fun pontuacaoInicialEhZeroParaOsDoisJogadores() {
        assertEquals(0, Placar.pontuacoes[0])
        assertEquals(0, Placar.pontuacoes[1])
    }

    @Test
    fun partidasGanhasIniciaisSaoZeroParaOsDoisJogadores() {
        assertEquals(0, Placar.partidasGanhas[0])
        assertEquals(0, Placar.partidasGanhas[1])
    }

    @Test
    fun nomesIniciaisSaoVaziosIndicandoPadraoLocalizado() {
        assertEquals("", Placar.nomes[0])
        assertEquals("", Placar.nomes[1])
    }

    @Test
    fun adicionarPontosJogador1IncrementaPontuacao() {
        Placar.adicionarPontos(0, 3)

        assertEquals(3, Placar.pontuacoes[0])
    }

    @Test
    fun adicionarPontosJogador2IncrementaPontuacao() {
        Placar.adicionarPontos(1, 6)

        assertEquals(6, Placar.pontuacoes[1])
    }

    @Test
    fun adicionarPontosAcumulaVariosLancesNoMesmoJogador() {
        Placar.adicionarPontos(0, 1)
        Placar.adicionarPontos(0, 3)
        Placar.adicionarPontos(0, 6)

        assertEquals(10, Placar.pontuacoes[0])
    }

    @Test
    fun adicionarPontosEmUmJogadorNaoAlteraOOutro() {
        Placar.adicionarPontos(1, 4)

        assertEquals(0, Placar.pontuacoes[0])
        assertEquals(4, Placar.pontuacoes[1])
    }

    @Test
    fun abaixoDeDozeNaoHaVitoria() {
        val venceu = Placar.adicionarPontos(0, 9)

        assertFalse(venceu)
        assertEquals(0, Placar.partidasGanhas[0])
    }

    @Test
    fun dozePontosMarcaVitoriaExata() {
        val venceu = Placar.adicionarPontos(1, 12)

        assertTrue(venceu)
        assertEquals(1, Placar.partidasGanhas[1])
    }

    @Test
    fun pontuacaoAcimaDeDozeTambemMarcaVitoria() {
        Placar.adicionarPontos(0, 9)

        assertTrue(Placar.adicionarPontos(0, 6))
    }

    @Test
    fun vitoriaIncrementaContadorDePartidasGanhasDoVencedor() {
        Placar.adicionarPontos(0, 6)
        Placar.adicionarPontos(0, 9)

        assertEquals(1, Placar.partidasGanhas[0])
    }

    @Test
    fun vitoriaZeraAsPontuacoesDosDoisJogadores() {
        Placar.adicionarPontos(0, 3)
        Placar.adicionarPontos(1, 6)

        // Sequencia Q2: adicionarPontos marca a vitoria (incrementa o
        // contador) mas NAO zera; quem zera e resetPontuacoes(), chamado
        // pela MainActivity no dismiss da mensagem de vitoria.
        val venceu = Placar.adicionarPontos(0, 12)

        // Ainda durante a mensagem de vitoria, o placar mantem a partida vencida.
        assertTrue(venceu)
        assertEquals(15, Placar.pontuacoes[0])
        assertEquals(6, Placar.pontuacoes[1])

        Placar.resetPontuacoes()

        assertEquals(0, Placar.pontuacoes[0])
        assertEquals(0, Placar.pontuacoes[1])
        assertEquals(1, Placar.partidasGanhas[0])
    }

    @Test
    fun resetPontuacoesPreservaContadoresENomes() {
        Placar.definirNomes("Time Tricolor", "Time Rubro")
        Placar.adicionarPontos(0, 12)
        Placar.adicionarPontos(1, 3)

        Placar.resetPontuacoes()

        assertEquals(1, Placar.partidasGanhas[0])
        assertEquals(0, Placar.partidasGanhas[1])
        assertEquals("Time Tricolor", Placar.nomes[0])
        assertEquals("Time Rubro", Placar.nomes[1])
    }

    @Test
    fun vitoriaPreservaONomeDoVencedor() {
        Placar.definirNomes("Time Tricolor", "Time Rubro")
        Placar.adicionarPontos(0, 12)
        Placar.resetPontuacoes()

        assertEquals("Time Tricolor", Placar.nomes[0])
    }

    @Test
    fun definirNomesAtualizaOsDoisNomes() {
        Placar.definirNomes("Equipe Alfa", "Equipe Beta")

        assertEquals("Equipe Alfa", Placar.nomes[0])
        assertEquals("Equipe Beta", Placar.nomes[1])
    }

    @Test
    fun definirNomesSobrescreveNomeAnterior() {
        Placar.definirNomes("Primeiro", "Segundo")
        Placar.definirNomes("Terceiro", "Quarto")

        assertEquals("Terceiro", Placar.nomes[0])
        assertEquals("Quarto", Placar.nomes[1])
    }

    @Test
    fun definirNomesComBrancoMantemPadraoLocalizado() {
        Placar.definirNomes("   ", "Time Rubro")

        assertEquals("", Placar.nomes[0])
        assertEquals("Time Rubro", Placar.nomes[1])
    }

    @Test
    fun zerarTudoRestauraPontuacoesPartidasENomes() {
        Placar.definirNomes("Time A", "Time B")
        Placar.adicionarPontos(0, 12)
        Placar.resetPontuacoes()
        Placar.adicionarPontos(1, 3)

        Placar.zerarTudo()

        assertEquals(0, Placar.pontuacoes[0])
        assertEquals(0, Placar.pontuacoes[1])
        assertEquals(0, Placar.partidasGanhas[0])
        assertEquals(0, Placar.partidasGanhas[1])
        assertEquals("", Placar.nomes[0])
        assertEquals("", Placar.nomes[1])
    }
}
