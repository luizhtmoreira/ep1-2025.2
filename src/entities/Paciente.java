package entities;

public class Paciente extends Pessoa {
    
    private int idade;
    // Futuramente: private List<Consulta> historicoConsultas;

    public Paciente(String nome, String cpf, int idade) {
        // 1. Chama o construtor da classe pai (Pessoa)
        super(nome, cpf); 
        
        // 2. Inicializa os atributos específicos de Paciente
        this.idade = idade;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    @Override
    public String toString() {
        // Aproveita o toString da classe Pessoa e adiciona as novas informações
        return "Paciente [" + super.toString() + ", Idade: " + idade + "]";
    }
}