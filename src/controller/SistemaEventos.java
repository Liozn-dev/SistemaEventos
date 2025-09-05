package controller;

import model.Evento;
import model.Usuario;
import model.Categoria;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SistemaEventos {
    private static Scanner sc = new Scanner(System.in);
    private static List<Evento> eventos = new ArrayList<>();
    private static final String ARQUIVO = "data/events.data";
    private static Usuario usuarioAtual = null;
    // Vamos assumir duração padrão de 2 horas para determinar se um evento está ocorrendo
    private static final int DURACAO_HORAS = 2;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        criarPastaData();
        carregarEventos();
        boasVindas();
        int opcao;
        do {
            mostrarMenu();
            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            switch (opcao) {
                case 1: cadastrarEvento(); break;
                case 2: listarEventos(); break;
                case 3: participarEvento(); break;
                case 4: listarParticipacoesDoUsuario(); break;
                case 5: cancelarParticipacao(); break;
                case 6: listarEventosPassados(); break;
                case 7: listarEventosNaMinhaCidade(); break;
                case 0: salvarEventos(); System.out.println("Saindo..."); break;
                default: System.out.println("Opção inválida!"); break;
            }
        } while (opcao != 0);
    }

    // Método para criar a pasta "data" se não existir
    private static void criarPastaData() {
        File pasta = new File("data");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }

    private static void boasVindas() {
        System.out.println("Bem-vindo ao Sistema de Eventos\n"); 
        System.out.println("Primeiro preciso cadastrar um usuário para este computador.");
        System.out.print("Nome: "); String nome = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Cidade: "); String cidade = sc.nextLine();
        System.out.print("Telefone: "); String telefone = sc.nextLine();
        System.out.print("Idade: "); int idade = Integer.parseInt(sc.nextLine());
        usuarioAtual = new Usuario(nome, email, cidade, telefone, idade);
        System.out.println("Usuário cadastrado: " + usuarioAtual + "\n");
        // Notificar eventos na cidade do usuário que ocorrerão em breve (próximos 7 dias)
        notificarEventosCidade();
    }

    private static void mostrarMenu() {
        System.out.println("===== SISTEMA DE EVENTOS =====");
        System.out.println("1 - Cadastrar evento");
        System.out.println("2 - Listar eventos (ordenados por horário)");
        System.out.println("3 - Participar de evento");
        System.out.println("4 - Meus eventos confirmados");
        System.out.println("5 - Cancelar participação");
        System.out.println("6 - Ver eventos que já ocorreram");
        System.out.println("7 - Ver eventos na minha cidade");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private static void cadastrarEvento() {
        try {
            System.out.print("Nome do evento: "); String nome = sc.nextLine();
            System.out.print("Endereço: "); String endereco = sc.nextLine();
            System.out.println("Categorias disponíveis: FESTA, ESPORTE, SHOW, CONFERENCIA, TEATRO, OUTROS");
            System.out.print("Categoria (escreva uma das opções acima): "); String cat = sc.nextLine();
            Categoria categoria = Categoria.fromString(cat);
            System.out.print("Data e hora (dd/MM/yyyy HH:mm): "); String dataHora = sc.nextLine();
            LocalDateTime horario = LocalDateTime.parse(dataHora, formatter);
            System.out.print("Descrição: "); String descricao = sc.nextLine();

            eventos.add(new Evento(nome, endereco, categoria, horario, descricao));
            System.out.println("Evento cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar evento. Verifique os dados e o formato da data (dd/MM/yyyy HH:mm).\nDetalhe: " + e.getMessage());
        }
    }

    private static void listarEventos() {
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        eventos.sort(Comparator.comparing(Evento::getHorario));
        LocalDateTime agora = LocalDateTime.now();
        for (int i = 0; i < eventos.size(); i++) {
            Evento ev = eventos.get(i);
            String status = statusDoEvento(ev, agora);
            System.out.println(i + " - " + ev + " -> " + status);
        }
    }

    private static String statusDoEvento(Evento ev, LocalDateTime agora) {
        LocalDateTime inicio = ev.getHorario();
        LocalDateTime fim = inicio.plusHours(DURACAO_HORAS);
        if (!inicio.isAfter(agora) && !fim.isBefore(agora)) {
            return "OCORRENDO AGORA";
        } else if (inicio.isBefore(agora)) {
            return "JÁ OCORREU";
        } else {
            long minutos = java.time.Duration.between(agora, inicio).toMinutes();
            return "Em " + minutos + " minutos";
        }
    }

    private static void participarEvento() {
        listarEventos();
        System.out.print("Digite o índice do evento: ");
        try {
            int idx = Integer.parseInt(sc.nextLine());
            if (idx >= 0 && idx < eventos.size()) {
                Evento ev = eventos.get(idx);
                ev.adicionarParticipante(usuarioAtual);
                System.out.println("Você confirmou presença no evento: " + ev.getNome());
            } else {
                System.out.println("Índice inválido.");
            }
        } catch (Exception e) {
            System.out.println("Entrada inválida."); 
        }
    }

    private static void listarParticipacoesDoUsuario() {
        System.out.println("Seus eventos confirmados:");
        boolean any = false;
        for (Evento ev : eventos) {
            for (Usuario u : ev.getParticipantes()) {
                if (u.getEmail().equalsIgnoreCase(usuarioAtual.getEmail())) {
                    System.out.println(ev + " (" + ev.getHorario().format(formatter) + ")");
                    any = true;
                }
            }
        }
        if (!any) System.out.println("Você não confirmou presença em nenhum evento.");
    }

    private static void cancelarParticipacao() {
        listarParticipacoesDoUsuario();
        System.out.print("Digite o nome do evento que deseja cancelar (exatamente): ");
        String nome = sc.nextLine();
        for (Evento ev : eventos) {
            if (ev.getNome().equalsIgnoreCase(nome)) {
                ev.removerParticipantePorEmail(usuarioAtual.getEmail());
                System.out.println("Participação cancelada em: " + ev.getNome());
                return;
            }
        }
        System.out.println("Evento não encontrado ou você não está inscrito nele.");
    }

    private static void listarEventosPassados() {
        LocalDateTime agora = LocalDateTime.now();
        boolean any = false;
        for (Evento ev : eventos) {
            if (ev.getHorario().isBefore(agora)) {
                System.out.println(ev + " -> Já ocorreu em: " + ev.getHorario().format(formatter));
                any = true;
            }
        }
        if (!any) System.out.println("Nenhum evento já ocorreu.");
    }

    private static void listarEventosNaMinhaCidade() {
        System.out.println("Eventos na cidade de: " + usuarioAtual.getCidade());
        LocalDateTime agora = LocalDateTime.now();
        boolean any = false;
        for (Evento ev : eventos) {
            if (ev.getEndereco().toLowerCase().contains(usuarioAtual.getCidade().toLowerCase())) {
                System.out.println(ev + " -> " + statusDoEvento(ev, agora));
                any = true;
            }
        }
        if (!any) System.out.println("Nenhum evento encontrado na sua cidade.");
    }

    private static void notificarEventosCidade() {
        System.out.println("Verificando eventos na sua cidade nos próximos 7 dias...");
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusDays(7);
        boolean any = false;
        for (Evento ev : eventos) {
            if (ev.getEndereco().toLowerCase().contains(usuarioAtual.getCidade().toLowerCase()) 
                && (ev.getHorario().isAfter(agora) && ev.getHorario().isBefore(limite))) {
                System.out.println("-> Próximo evento: " + ev + " em " + ev.getHorario().format(formatter));
                any = true;
            }
        }
        if (!any) System.out.println("Nenhum evento nos próximos 7 dias na sua cidade."); 
        System.out.println();   
    }

    private static void salvarEventos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO))) {
            // cada evento em uma linha com campos separados por ||
            // participantes separados por ;; e cada participante tem campos nome::email::cidade::telefone::idade
            for (Evento ev : eventos) {
                StringBuilder sb = new StringBuilder();
                sb.append(escape(ev.getNome())).append("||")
                  .append(escape(ev.getEndereco())).append("||")
                  .append(ev.getCategoria().name()).append("||")
                  .append(ev.getHorario().format(formatter)).append("||")
                  .append(escape(ev.getDescricao())).append("||");

                List<Usuario> parts = ev.getParticipantes();
                for (int i = 0; i < parts.size(); i++) {
                    Usuario u = parts.get(i);
                    sb.append(escape(u.getNome())).append("::")
                      .append(escape(u.getEmail())).append("::")
                      .append(escape(u.getCidade())).append("::")
                      .append(escape(u.getTelefone())).append("::")
                      .append(u.getIdade());
                    if (i < parts.size() - 1) sb.append(";;");
                }
                bw.write(sb.toString());
                bw.newLine();
            }
            System.out.println("Eventos salvos em: " + ARQUIVO);
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("||", "[PIPE]")
                .replace("::", "[COLON]")
                .replace(";;", "[SEMICOLON]");
    }

    private static String unescape(String s) {
        return s.replace("[PIPE]", "||")
                .replace("[COLON]", "::")
                .replace("[SEMICOLON]", ";;")
                .replace("\\\\", "\\");
    }

    private static void carregarEventos() {
        File f = new File(ARQUIVO);
        if (!f.exists()) {
            eventos = new ArrayList<>();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|\\|", -1);
                if (parts.length < 6) continue;
                String nome = unescape(parts[0]);
                String endereco = unescape(parts[1]);
                Categoria categoria = Categoria.fromString(parts[2]);
                LocalDateTime horario = LocalDateTime.parse(parts[3], formatter);
                String descricao = unescape(parts[4]);
                Evento ev = new Evento(nome, endereco, categoria, horario, descricao);
                String participantesRaw = parts[5];
                if (!participantesRaw.isEmpty()) {
                    String[] pList = participantesRaw.split(";;");
                    for (String p : pList) {
                        String[] flds = p.split("::", -1);
                        if (flds.length >= 5) {
                            String pname = unescape(flds[0]);
                            String pemail = unescape(flds[1]);
                            String pcity = unescape(flds[2]);
                            String pphone = unescape(flds[3]);
                            int page = Integer.parseInt(flds[4]);
                            ev.adicionarParticipante(new Usuario(pname, pemail, pcity, pphone, page));
                        }
                    }
                }
                eventos.add(ev);
            }
            System.out.println(eventos.size() + " eventos carregados do arquivo de texto.");
        } catch (Exception e) {
            eventos = new ArrayList<>();
            System.out.println("Não foi possível carregar events.data: " + e.getMessage());
        }
    }
}
