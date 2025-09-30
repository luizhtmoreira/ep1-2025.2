package entities;

public class PacienteComPlano extends Paciente {

    private PlanoDeSaude plano;

    public PacienteComPlano(String nome, String cpf, int idade, PlanoDeSaude plano) {
        super(nome, cpf, idade);
        this.plano = plano;
    }

    public PlanoDeSaude getPlano() {
        return plano;
    }

    public void setPlano(PlanoDeSaude plano) {
        this.plano = plano;
    }

    // A MÁGICA DO POLIMORFISMO ACONTECE AQUI!
    @Override // Anotação que indica que estamos a sobrescrever um método da classe mãe
    public double calcularCustoConsulta(Medico medico) {
        // 1. Primeiro, calculamos o custo base (que já pode ter o desconto de idade)
        //    chamando o método da classe mãe (Paciente) com "super".
        double custoInicial = super.calcularCustoConsulta(medico);

        // 2. Depois, obtemos o desconto do plano para a especialidade do médico.
        double descontoPlano = this.plano.getDesconto(medico.getEspecialidade());

        // 3. Aplicamos o desconto do plano sobre o custo já ajustado.
        double custoFinal = custoInicial * (1.0 - descontoPlano);
        
        return custoFinal;
    }

    @Override
    public String toString() {
        // Apenas ajustando para mostrar o tipo
        return "Paciente [Tipo: Com Plano, " + super.toString()
            .replace("]", ", Plano: " + this.plano.getNome() + "]");
    }
}