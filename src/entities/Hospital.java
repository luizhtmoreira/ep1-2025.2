package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Hospital {

    private List<Paciente> pacientes;
    private List<Medico> medicos;
    private List<Consulta> consultas;

    public Hospital() {
        this.pacientes = new ArrayList<>();
        this.medicos = new ArrayList<>();
        this.consultas = new ArrayList<>();
    }
    
    // --- MÉTODOS DE PACIENTE ---
    public void cadastrarPaciente(String nome, String cpf, int idade) {
        for (Paciente pacienteExistente : this.pacientes) {
            if (pacienteExistente.getCpf().equals(cpf)) {
                System.out.println("Erro: Já existe um paciente cadastrado com o CPF " + cpf);
                return;
            }
        }
        Paciente novoPaciente = new Paciente(nome, cpf, idade);
        this.pacientes.add(novoPaciente);
        System.out.println("Paciente '" + nome + "' cadastrado com sucesso!");
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    // --- MÉTODOS DE MÉDICO ---
    public void cadastrarMedico(String nome, String cpf, String crm, String especialidade) {
        for (Medico medicoExistente : this.medicos) {
            if (medicoExistente.getCrm().equals(crm)) {
                System.out.println("Erro: Já existe um médico cadastrado com o CRM " + crm);
                return;
            }
        }
        Medico novoMedico = new Medico(nome, cpf, crm, especialidade);
        this.medicos.add(novoMedico);
        System.out.println("Médico '" + nome + "' cadastrado com sucesso!");
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    // --- MÉTODOS DE CONSULTA ---
    public void agendarConsulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local) {
        // REGRA DE NEGÓCIO: Verificar se o médico já tem uma consulta no mesmo horário.
        for (Consulta consultaExistente : this.consultas) {
            if (consultaExistente.getMedico().equals(medico) && consultaExistente.getDataHora().isEqual(dataHora)) {
                System.out.println("Erro: O médico " + medico.getNome() + " já possui uma consulta agendada para este horário.");
                return; // Interrompe o agendamento
            }
        }

        // Se passou na validação, cria e adiciona a nova consulta
        Consulta novaConsulta = new Consulta(paciente, medico, dataHora, local);
        this.consultas.add(novaConsulta);
        System.out.println("Consulta agendada com sucesso!");
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }
}