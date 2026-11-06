# SDD — SDD-av-android-2026-2 — Marcador de Truco (Android nativo, Kotlin + Views XML)

Trabalho final da disciplina de Android Básico (UTFPR — Campus Pato Branco).
Nível de rito: L2 (sem Jira). Fonte primária: enunciado do PDF da avaliação (transcrito
verbatim no contexto do ciclo) + requisitos explícitos do usuário.

## 1. Descoberta (resumo)

Especificado no enunciado: app de 3 telas — principal (pontuação corrente + botões de
1/3/6/9/12 pontos para cada jogador), histórico (quantidade de partidas ganhas por
jogador) e telas auxiliares de zerar e informar nomes (2 EditText + botão Confirmar).
Condições de vitória: pontuação ≥ 12 → jogador vence a partida, mensagem informativa
(Toast/AlertDialog), zera pontuações e incrementa contador de partidas do vencedor.

Requisitos adicionais explícitos: (a) Kotlin sem Compose; (b) README descritivo do
ambiente até emulador; (c) justificativa dos componentes visuais básicos
(entrada/processamento/saída); (d) escolha justificada LinearLayout vs ConstraintLayout;
(e) i18n pt-BR/en/es; (f) JUnit4 sobre lógica pura testável sem Android; (g) roteamento
por Intent explícito manual, proibido Navigation Component; (h) Material com ícones
vector drawables próprios (ic_history, ic_restart, ic_person, 24dp); (i) testes de
instrumentação com espresso.

## 2. Decisões

- **D1 — Layout principal: LinearLayout.** Motivo: o modelo de tela do PDF tem
  estrutura de empilhamento vertical + linhas uniformes de 5 botões de mesmo peso —
  exatamente o caso de uso "grid uniforme fixo" que o LinearLayout resolve com
  `orientation` + `layout_weight`, sem custo cognitivo. Alternativa rejeitada:
  **ConstraintLayout** — cada um dos 13 botões precisaria de 2+ constraints manuais,
  verboso e sem benefício aqui; nada na tela pede posicionamento relativo flexível,
  telas aninhadas por hierarquia ou animação de transição.

- **D2 — Estado em memória via singleton `object Placar` (Kotlin puro).** Armazena
  nomes, pontuações, partidas ganhas e encapsula `adicionarPontos(jogador, pontos)`,
  detecção de vitória (≥ 12), `zerarTudo()` e `definirNomes()`. Motivo: o enunciado
  determina "armazenada em memória, por meio de variáveis do tipo inteiro"; o singleton
  permite que as 3 Activities compartilhem o mesmo estado sem `Parcelable`/`Bundle`, e
  torna a lógica testável com JUnit4 puro (sem Robolectric). Alternativa rejeitada:
  ViewModel — arquitetura adicional fora do escopo da disciplina e desnecessária para
  3 telas não rotativas; SharedPreferences contradiz "em memória" do enunciado.

- **D3 — Roteamento por Intent explícito manual.** `startActivity(Intent(this,
  HistoricoActivity::class.java))` nos 2 botões de navegação; retorno da tela de nomes
  via `finish()` e re-render em `onResume()` da principal. Alternativa rejeitada:
  Navigation Component — proibido explicitamente pelo usuário (requisito g).

- **D4 — Mensagem de vitória via AlertDialog.** Motivo: evento terminal da partida, o
  usuário precisa confirmar a leitura; AlertDialog da biblioteca Material (tema
  Material3) é modal e mais visível que Toast. Alternativa rejeitada: Toast —
  efêmero (~2 s) e não modal, fácil de perder num evento de vitória.

- **D5 — Botões de navegação com ícones: vetores locais próprios.** `ic_history`,
  `ic_restart`, `ic_person` (24dp, paths base do Material Design) em `res/drawable`,
  apontados por `app:icon` de `MaterialButton` (biblioteca
  `com.google.android.material:material:1.12.0`). Alternativa rejeitada: `ImageButton`
  com PNG — escala mal e foge do requisito (h).

- **D6 — i18n com recursos padrão: `res/values/` = pt-BR, mais `values-en` e `values-es`.**
  Motivo: é idiomático Android — o seletor de recursos resolve `values-<locale>` pelo
  idioma do sistema e cai no `values/` default como fallback; basta 1 `strings.xml` por
  idioma, sem código. Alternativa rejeitada: strings no código — quebra o requisito (e)
  e o lint `UseStringResources`.

- **D7 — Componentes de entrada/processamento/saída mapeados.** Entrada: EditText
  (nomes), Button/MaterialButton (comandos); processamento: `Placar` (singleton) +
  listeners de clique; saída: TextView (nomes/pontuação/histórico), AlertDialog
  (vitória), Toast (confirmação de zerar). Tabela completa no README §5.

- **D8 — Tema Theme.Material3.DayNight.NoActionBar.** Motivo: dá escura/claro de
  graça e vem da biblioteca Material requisitada. Alternativa rejeitada: AppCompat
  básico — perderia os estilos dos componentes Material3.

- **D9 — Nomes padrão.** O `Placar` inicia com "Jogador 1"/"Jogador 2"; a tela de nomes
  pré-preenche os EditText com os nomes atuais. `zerarTudo()` restaura esses padrões
  (o botão 'Zerar Histórico' zera pontuações, contadores e nomes, conforme Q5).

- **D10 — Vitória reseta apenas as pontuações.** Quando alguém atinge ≥ 12: incrementa
  `partidasGanhas[jogador]`, zera ambos os `pontuacoes` (requisito Q2: "zerando a
  pontuação dos jogadores após a mensagem") e preserva nomes + contadores de partidas.
  Nomes/contadores só voltam ao inicial por `zerarTudo()` (botão 'Zerar Histórico').
  Alternativa rejeitada: zerar tudo na vitória — perderia o histórico de partidas que
  o próprio Q3 exige exibir.

- **D11 — Toolchain fixada.** AGP 8.7.3, Gradle wrapper 8.10.2, Kotlin 2.0.21,
  compileSdk/targetSdk 35, minSdk 24, JVM 17, package/appId `br.edu.utfpr.truco`.
  Motivo: valores pedidos nas diretrizes técnicas do trabalho; JDK 21 da máquina é
  compatível (AGP 8.7.3 exige JDK 17+).

## 3. Testes

- **Unitários (JUnit4, sem Android):** `app/src/test/.../PlacarTest.kt` sobre
  `Placar` (soma de pontos, acumulação, limite de vitória exato em 12 e acima, zera
  pontuações na vitória, incrementa partidas do vencedor, não altera o adversário,
  `definirNomes`, `zerarTudo`).
- **Instrumentação (espresso):** `app/src/androidTest/.../MainActivityTest.kt` —
  clicar +1 e verificar TextView; clicar +12 e verificar zera pontuação após vitória;
  navegar a Histórico; informar nome, confirmar e verificar atualização na principal.
