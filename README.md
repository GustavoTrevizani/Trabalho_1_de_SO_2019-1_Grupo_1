# Trabalho 1 - Sistemas Operacionais

## 🎯 Objetivo
Este projeto tem como objetivo implementar um **simulador de escalonamento de processos** utilizando o algoritmo **Round Robin com Feedback**.  
O trabalho foi desenvolvido para a disciplina de **Sistemas Operacionais (GER383540)** na **Universidade Feevale**.

## 👨‍💻 Integrantes
- Gustavo Trevizani  
- Natália Barili  

## 🖥️ Descrição do Projeto
O simulador representa o funcionamento de um escalonador de processos em um sistema operacional, onde múltiplos processos competem pela CPU.  
O algoritmo utilizado é o **Round Robin com Feedback**, que organiza os processos em filas de diferentes prioridades e gerencia operações de entrada/saída (I/O).

### Premissas do Código
- **Número máximo de processos:** 5  
- **Fatia de tempo (time slice):** 3 unidades de tempo  
- **Tempos de execução e I/O:** gerados aleatoriamente na criação dos processos  
- **Tipos de I/O considerados:**
  - Disco → fila de baixa prioridade  
  - Fita magnética / impressora → fila de alta prioridade  
- **Filas utilizadas:**
  - Alta prioridade  
  - Baixa prioridade  
  - Filas de I/O  
- Cada processo possui:
  - `pid` (identificador)  
  - `prioridade`  
  - `tempo total` e `tempo restante`  
  - `status`  
  - atributos de I/O  

### Funcionamento
1. Todos os processos iniciam na **fila de alta prioridade**.  
2. Cada processo executa até:
   - Terminar sua execução, ou  
   - Consumir toda a fatia de tempo (sendo movido para a fila de menor prioridade).  
3. Processos que precisam de I/O são enviados para a **fila de I/O** correspondente e retornam depois ao escalonador.  
4. A CPU sempre dá preferência à fila de maior prioridade.  

### Exemplo de Saída
```text
Processo 1 criado com tempo 5 e sem I/O.  
Processo 2 criado com tempo 2 e sem I/O.  
Processo 3 criado com tempo 9 e sem I/O.  
Processo 4 criado com tempo 7 e sem I/O.  
Processo 5 criado com tempo 8 e sem I/O.  
Executando processo 1 com tempo restante 5  
Processo 1 movido para fila baixa.  
Executando processo 2 com tempo restante 2  
Processo 2 finalizado.  
...
Executando processo 5 com tempo restante 2  
Processo 5 finalizado.  
Todos os processos foram finalizados.  
