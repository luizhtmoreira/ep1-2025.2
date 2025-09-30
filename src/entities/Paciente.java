package entities;

public class Paciente extends Pessoa {
    
    private int idade;

    public Paciente(String nome, String cpf, int idade) {
        super(nome, cpf); 
        this.idade = idade;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }
    
    // NOVO MÉTODO - BASE PARA O POLIMORFISMO
    public double calcularCustoConsulta(Medico medico) {
        double custoBase = medico.getCustoConsulta();
        // Requisito: Pacientes com 60+ anos têm desconto. Vamos assumir 20% de desconto.
        if (this.idade >= 60) {
            return custoBase * 0.80; // Aplica 20% de desconto
        }
        return custoBase;
    }

    @Override
    public String toString() {
        return "Paciente [Tipo: Normal, " + super.toString() + ", Idade: " + idade + "]";
    }
}