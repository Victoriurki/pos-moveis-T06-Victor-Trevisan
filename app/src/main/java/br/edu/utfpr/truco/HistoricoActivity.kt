package br.edu.utfpr.truco

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Tela de historico: mostra quantas partidas cada jogador ganhou
 * desde o ultimo reinicio.
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
            getString(R.string.fmt_historico_linha, Placar.nomes[0], Placar.partidasGanhas[0])
        findViewById<TextView>(R.id.tvHistoricoJogador2).text =
            getString(R.string.fmt_historico_linha, Placar.nomes[1], Placar.partidasGanhas[1])
    }
}
