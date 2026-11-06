package br.edu.utfpr.truco

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Tela de historico: mostra quantas partidas cada jogador ganhou
 * desde o ultimo reinicio, no formato "%1$s: %2$d partidas ganhas"
 * (strings localizadas).
 */
class HistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)
    }

    override fun onResume() {
        super.onResume()
        atualizarHistorico()
    }

    private fun atualizarHistorico() {
        findViewById<TextView>(R.id.tvHistoricoJogador1).text =
            getString(R.string.fmt_historico_linha, nomeExibivel(0), Placar.partidasGanhas[0])
        findViewById<TextView>(R.id.tvHistoricoJogador2).text =
            getString(R.string.fmt_historico_linha, nomeExibivel(1), Placar.partidasGanhas[1])
    }

    /**
     * Nome apresentado: o nome customizado informado na tela de nomes ou,
     * se vazio, o nome padrao localizado (mesma regra da MainActivity).
     */
    private fun nomeExibivel(jogador: Int): String =
        if (Placar.nomes[jogador].isBlank()) {
            getString(if (jogador == 0) R.string.jogador_1 else R.string.jogador_2)
        } else {
            Placar.nomes[jogador]
        }
}
