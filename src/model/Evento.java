package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Evento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String endereco;
    private Categoria categoria;
    private LocalDateTime horario;
    private String descricao;
    private List<Usuario> participantes = new ArrayList<>();

    public Evento(String nome, String endereco, Categoria categoria, LocalDateTime horario, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public Categoria getCategoria() { return categoria; }
    public LocalDateTime getHorario() { return horario; }
    public String getDescricao() { return descricao; }
    public List<Usuario> getParticipantes() { return participantes; }

    public void adicionarParticipante(Usuario usuario) {
        participantes.add(usuario);
    }

    public void removerParticipantePorEmail(String email) {
        participantes.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return nome + " | " + categoria + " | " + endereco + " | " + horario.format(formatter) + " | " + descricao;
    }
}
