package entities;

// PacienteComPlano HERDA tudo que Paciente tem
public class PacienteComPlano extends Paciente {

    private PlanoDeSaude plano;

    public PacienteComPlano(String nome, String cpf, int idade, PlanoDeSaude plano) {
        // 1. Chama o construtor da classe mãe (Paciente) para construir a "parte Paciente" do objeto
        super(nome, cpf, idade);
        // 2. Inicializa o atributo específico desta classe
        this.plano = plano;
    }

    public PlanoDeSaude getPlano() {
        return plano;
    }

    public void setPlano(PlanoDeSaude plano) {
        this.plano = plano;
    }

    @Override
    public String toString() {
        // Reutilizamos o toString() do Paciente e adicionamos a informação do plano
        return super.toString().replace("]", ", Plano: " + this.plano.getNome() + "]");
    }
}