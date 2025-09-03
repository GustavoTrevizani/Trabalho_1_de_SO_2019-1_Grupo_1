import java.util.*;

public class SimuladorEscalonamentoProcessos {
    static final int MAX_PROCESSOS = 5;      // Número máximo de processos simulados
    static final int FATIA_TEMPO = 3;        // Quantidade de tempo que cada processo pode executar por vez (time slice)

    // Arrays para armazenar atributos dos processos
    static int[] pid = new int[MAX_PROCESSOS];            // Identificador do processo
    static int[] tempoTotal = new int[MAX_PROCESSOS];     // Tempo total necessário para execução do processo
    static int[] tempoRestante = new int[MAX_PROCESSOS];  // Tempo restante para o processo terminar
    static int[] prioridade = new int[MAX_PROCESSOS];     // Prioridade do processo: 1 = alta, 2 = baixa
    static String[] status = new String[MAX_PROCESSOS];   // Status atual do processo (ex: NaFilaAlta, Executando, EsperandoIO, Finalizado)

    // Arrays para I/O
    static String[] tipoIO = new String[MAX_PROCESSOS];    // Tipo de dispositivo de I/O associado ao processo (disco, fita, impressora ou "nenhum")
    static int[] tempoIO = new int[MAX_PROCESSOS];         // Tempo total de I/O necessário para o processo
    static int[] tempoIORestante = new int[MAX_PROCESSOS]; // Tempo restante de I/O para o processo (0 se não está em I/O)

    // Filas para escalonamento
    static Queue<Integer> filaAlta = new LinkedList<>();    // Fila de processos com alta prioridade (prioridade 1)
    static Queue<Integer> filaBaixa = new LinkedList<>();   // Fila de processos com baixa prioridade (prioridade 2)
    static Queue<Integer> filaIOAlta = new LinkedList<>();  // Fila de I/O para dispositivos de alta prioridade (fita magnética e impressora)
    static Queue<Integer> filaIOBaixa = new LinkedList<>();   // Fila de I/O para dispositivo de baixa prioridade (disco)

    public static void main(String[] args) {
        Random rand = new Random();

        // Tipos de I/O disponíveis para os processos
        String[] tiposIODisponiveis = {"disco", "fita", "impressora"};

        // Criação dos processos com tempos aleatórios e inicialização dos atributos
        for (int i = 0; i < MAX_PROCESSOS; i++) {
            pid[i] = i + 1;                              // ID do processo
            tempoTotal[i] = rand.nextInt(10) + 1;  // Tempo total entre 1 e 10 unidades
            tempoRestante[i] = tempoTotal[i];            // Inicialmente, tempo restante é igual ao total
            prioridade[i] = 1;                           // Todos começam com alta prioridade
            status[i] = "NaFilaAlta";                    // Status inicial: na fila de alta prioridade

            // Definir se o processo terá I/O (50% de chance)
            if (rand.nextDouble() < 0.5) {
                // Processo terá I/O
                int ioIndex = rand.nextInt(tiposIODisponiveis.length);
                tipoIO[i] = tiposIODisponiveis[ioIndex];
                tempoIO[i] = rand.nextInt(5) + 2;    // Tempo de I/O aleatório entre 2 e 6
            } else {
                // Processo sem I/O
                tipoIO[i] = "nenhum";
                tempoIO[i] = 0;
            }
            tempoIORestante[i] = 0; // Processo não está em I/O no início

            // Adiciona o processo na fila de alta prioridade (todos começam na fila alta)
            filaAlta.add(i);

            // Exibe informações iniciais do processo criado
            if (tipoIO[i].equals("nenhum")) {
                System.out.println("Processo " + pid[i] + " criado com tempo " + tempoTotal[i] + " e sem I/O.");
            } else {
                System.out.println("Processo " + pid[i] + " criado com tempo " + tempoTotal[i] + " e I/O tipo " + tipoIO[i] + " com duração I/O " + tempoIO[i]);
            }
        }

        // Loop principal da simulação: enquanto houver processos nas filas de prontos ou I/O
        while (!filaAlta.isEmpty() || !filaBaixa.isEmpty() || !filaIOAlta.isEmpty() || !filaIOBaixa.isEmpty()) {

            // Processa a fila de I/O de alta prioridade (fita e impressora)
            if (!filaIOAlta.isEmpty()) {
                int indiceIO = filaIOAlta.peek();       // Pega o processo no início da fila de I/O alta
                tempoIORestante[indiceIO]--;            // Decrementa o tempo restante de I/O
                if (tempoIORestante[indiceIO] <= 0) {
                    filaIOAlta.poll();                  // Remove o processo da fila de I/O alta
                    // Atualiza o status para pronto, conforme o tipo de I/O (fita/impressora → fila alta)
                    status[indiceIO] = "NaFilaAlta";
                    prioridade[indiceIO] = 1;
                    filaAlta.add(indiceIO);
                    System.out.println("Processo " + pid[indiceIO] + " terminou I/O alta e voltou para fila alta.");
                }
            }

            // Processa a fila de I/O de baixa prioridade (disco)
            if (!filaIOBaixa.isEmpty()) {
                int indiceIO = filaIOBaixa.peek();     // Pega o processo no início da fila de I/O baixa
                tempoIORestante[indiceIO]--;           // Decrementa o tempo restante de I/O
                if (tempoIORestante[indiceIO] <= 0) {
                    filaIOBaixa.poll();                // Remove o processo da fila de I/O baixa
                    // Atualiza o status para pronto, conforme o tipo de I/O (disco → fila baixa)
                    status[indiceIO] = "NaFilaBaixa";
                    prioridade[indiceIO] = 2;
                    filaBaixa.add(indiceIO);
                    System.out.println("Processo " + pid[indiceIO] + " terminou I/O baixa e voltou para fila baixa.");
                }
            }

            Integer indice = null; // Índice do processo que será executado na CPU neste ciclo

            // Prioridade: sempre tenta executar processos da fila alta primeiro
            if (!filaAlta.isEmpty()) {
                indice = filaAlta.poll();
            } else if (!filaBaixa.isEmpty()) {
                indice = filaBaixa.poll();
            }

            if (indice != null) {
                status[indice] = "Executando";     // Atualiza status para execução
                System.out.println("Executando processo " + pid[indice] + " com tempo restante " + tempoRestante[indice]);

                // Só tenta iniciar I/O se o processo tiver I/O definido e não estiver já em I/O
                boolean fazIO = !tipoIO[indice].equals("nenhum") && tempoIORestante[indice] == 0 && (rand.nextInt(10) < 3);

                if (fazIO) {
                    status[indice] = "EsperandoIO";              // Processo passa a esperar I/O
                    tempoIORestante[indice] = tempoIO[indice];   // Define tempo restante de I/O

                    // Move o processo para a fila de I/O correta, conforme o tipo de dispositivo
                    if (tipoIO[indice].equals("disco")) {
                        filaIOBaixa.add(indice);
                        System.out.println("Processo " + pid[indice] + " iniciou I/O disco (fila baixa).");
                    } else {
                        filaIOAlta.add(indice);
                        System.out.println("Processo " + pid[indice] + " iniciou I/O fita/impressora (fila alta).");
                    }
                    continue;     // Pula a execução na CPU neste ciclo, pois está em I/O
                }

                // Executa o processo por uma fatia de tempo ou até terminar
                int tempoExec = Math.min(FATIA_TEMPO, tempoRestante[indice]);
                tempoRestante[indice] -= tempoExec;

                if (tempoRestante[indice] <= 0) {
                    // Processo terminou sua execução
                    status[indice] = "Finalizado";
                    System.out.println("Processo " + pid[indice] + " finalizado.");
                } else {
                    // Processo não terminou, então pode mudar de fila conforme prioridade
                    if (prioridade[indice] == 1) {
                        // Se estava na fila alta, desce para fila baixa (feedback)
                        prioridade[indice] = 2;
                        status[indice] = "NaFilaBaixa";
                        filaBaixa.add(indice);
                        System.out.println("Processo " + pid[indice] + " movido para fila baixa.");
                    } else {
                        // Se já estava na fila baixa, volta para o fim da fila baixa
                        status[indice] = "NaFilaBaixa";
                        filaBaixa.add(indice);
                        System.out.println("Processo " + pid[indice] + " continua na fila baixa.");
                    }
                }
            }
        }

        System.out.println("Todos os processos foram finalizados.");
    }
}
