package model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private String email;
    private String cidade;
    private String telefone;
    private int idade;

    public Usuario(String nome, String email, String cidade, String telefone, int idade) {
        this.nome = nome;
        this.email = email;
        this.cidade = cidade;
        this.telefone = telefone;
        this.idade = idade;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCidade() { return cidade; }
    public String getTelefone() { return telefone; }
    public int getIdade() { return idade; }

    @Override
    public String toString() {
        return nome + " <" + email + "> (" + cidade + ")";
    }
}
