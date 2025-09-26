package entities;

public class Especialidade {
    
    private String nome;

    public Especialidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        // A representação em texto da especialidade é apenas o seu nome.
        return this.nome;
    }
}