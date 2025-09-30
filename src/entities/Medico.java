package entities;

public class Medico extends Pessoa {

    private String crm;
    private Especialidade especialidade;
    private double custoConsulta; // NOVO ATRIBUTO

    public Medico(String nome, String cpf, String crm, Especialidade especialidade, double custoConsulta) {
        super(nome, cpf);
        this.crm = crm;
        this.especialidade = especialidade;
        this.custoConsulta = custoConsulta;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }
    
    public double getCustoConsulta() {
        return custoConsulta;
    }

    public void setCustoConsulta(double custoConsulta) {
        this.custoConsulta = custoConsulta;
    }

    @Override
    public String toString() {
        return "Medico [" + super.toString() + ", CRM: " + crm + ", Especialidade: " + especialidade + ", Custo: R$" + String.format("%.2f", custoConsulta) + "]";
    }
}