package entities;

//classe abstrata porque nunca vamos criar um objeto do tipo pessoa
public abstract class Pessoa {
    
    private String nome;
    private String cpf;

    public Pessoa(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

 
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    // Método para facilitar a exibição dos dados (útil para debug)
    @Override
    public String toString() {
        return "Nome: " + nome + ", CPF: " + cpf;
    }
}