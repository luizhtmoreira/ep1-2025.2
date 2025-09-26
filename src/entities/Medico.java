package entities;

public class Medico extends Pessoa {

    private String crm;
    private Especialidade especialidade; // MUDANÇA AQUI: de String para Especialidade

    // O construtor agora recebe o objeto Especialidade
    public Medico(String nome, String cpf, String crm, Especialidade especialidade) {
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

    // O getter e setter agora trabalham com o objeto Especialidade
    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        // A mudança é transparente aqui, pois o toString() da especialidade será chamado
        return "Medico [" + super.toString() + ", CRM: " + crm + ", Especialidade: " + especialidade + "]";
    }
}