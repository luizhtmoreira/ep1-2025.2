package entities;

public class Medico extends Pessoa {

    private String crm;
    private String especialidade;

    public Medico(String nome, String cpf, String crm, String especialidade) {
        super(nome, cpf);
        this.crm = crm;
        this.especialidade = especialidade;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        return "Medico [" + super.toString() + ", CRM: " + crm + ", Especialidade: " + especialidade + "]";
    }
}