package entities;

import java.time.LocalDateTime;

public class Consulta {

    private Paciente paciente;
    private Medico medico;
    private LocalDateTime dataHora;
    private String local;
    private String status; // Ex: "AGENDADA", "CONCLUIDA", "CANCELADA"
    private String diagnostico;
    private String prescricao;

    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataHora = dataHora;
        this.local = local;
        this.status = "AGENDADA"; // Toda nova consulta começa como agendada
        this.diagnostico = "";    // Inicialmente vazios
        this.prescricao = "";     // Inicialmente vazios
    }

    // Getters e Setters
    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    // Método para concluir a consulta
    public void concluirConsulta(String diagnostico, String prescricao) {
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.status = "CONCLUIDA";
    }

    // Método para cancelar a consulta
    public void cancelarConsulta() {
        this.status = "CANCELADA";
    }

    @Override
    public String toString() {
        return "Consulta [" +
               "Data/Hora: " + dataHora +
               ", Local: '" + local + '\'' +
               ", Status: '" + status + '\'' +
               "\n  Paciente: " + paciente.getNome() + // Exibe apenas o nome para clareza
               "\n  Medico: " + medico.getNome() + " (CRM: " + medico.getCrm() + ")" + // Exibe nome e CRM
               "\n  Diagnostico: '" + diagnostico + '\'' +
               "\n  Prescricao: '" + prescricao + '\'' +
               ']';
    }
}