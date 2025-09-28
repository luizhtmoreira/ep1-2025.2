package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import utils.Persistencia;

public class Hospital {

    private List<Paciente> pacientes;
    private List<Medico> medicos;
    private List<Consulta> consultas;
    private List<Especialidade> especialidades;
    private List<PlanoDeSaude> planosDeSaude; // ADICIONE ESTA LINHA

    public Hospital() {
        this.pacientes = new ArrayList<>();
        this.medicos = new ArrayList<>();
        this.consultas = new ArrayList<>();
        this.especialidades = new ArrayList<>();
        this.planosDeSaude = new ArrayList<>(); // ADICIONE ESTA LINHA
        System.out.println("Carregando dados existentes...");
        Persistencia.carregarDados(this);
    }
    
    // --- MÉTODOS PÚBLICOS QUE SALVAM ---

    public void cadastrarPaciente(String nome, String cpf, int idade) {
        if(buscarPacientePorCpf(cpf) != null) {
             System.out.println("Erro: Já existe um paciente cadastrado com o CPF " + cpf);
             return;
        }
        cadastrarPacienteSemSalvar(nome, cpf, idade);
        System.out.println("Paciente '" + nome + "' cadastrado com sucesso!");
        Persistencia.salvarPacientes(this.pacientes);
    }

    public void cadastrarEspecialidade(String nome) {
        if (buscarEspecialidadePorNome(nome) != null) {
            System.out.println("Erro: Especialidade já cadastrada.");
            return;
        }
        cadastrarEspecialidadeSemSalvar(nome);
        System.out.println("Especialidade '" + nome + "' cadastrada com sucesso!");
        Persistencia.salvarEspecialidades(this.especialidades);
    }

    public void cadastrarMedico(String nome, String cpf, String crm, Especialidade especialidade) {
        if(buscarMedicoPorCrm(crm) != null) {
            System.out.println("Erro: Já existe um médico cadastrado com o CRM " + crm);
            return;
        }
        cadastrarMedicoSemSalvar(nome, cpf, crm, especialidade);
        System.out.println("Médico '" + nome + "' cadastrado com sucesso!");
        Persistencia.salvarMedicos(this.medicos);
    }

    public void agendarConsulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local) {
        for (Consulta consultaExistente : this.consultas) {
            if (consultaExistente.getMedico().equals(medico) && consultaExistente.getDataHora().isEqual(dataHora)) {
                System.out.println("Erro: O médico " + medico.getNome() + " já possui uma consulta agendada para este horário.");
                return;
            }
        }
        agendarConsultaSemSalvar(paciente, medico, dataHora, local);
        System.out.println("Consulta agendada com sucesso!");
        Persistencia.salvarConsultas(this.consultas);
    }

    // --- MÉTODOS "INTERNOS" SEM SALVAR (USADOS PELA PERSISTÊNCIA) ---

    public void cadastrarPacienteSemSalvar(String nome, String cpf, int idade) {
        Paciente novoPaciente = new Paciente(nome, cpf, idade);
        this.pacientes.add(novoPaciente);
    }

    public void cadastrarEspecialidadeSemSalvar(String nome) {
        Especialidade novaEspecialidade = new Especialidade(nome);
        this.especialidades.add(novaEspecialidade);
    }

    public void cadastrarMedicoSemSalvar(String nome, String cpf, String crm, Especialidade especialidade) {
        Medico novoMedico = new Medico(nome, cpf, crm, especialidade);
        this.medicos.add(novoMedico);
    }
    
    public void agendarConsultaSemSalvar(Paciente p, Medico m, LocalDateTime dataHora, String local) {
        Consulta novaConsulta = new Consulta(p, m, dataHora, local);
        this.consultas.add(novaConsulta);
    }

    // --- MÉTODOS DE PLANO DE SAÚDE (NOVOS) ---

public void cadastrarPlanoDeSaude(String nome) {
    if (buscarPlanoPorNome(nome) != null) {
        System.out.println("Erro: Plano de saúde já cadastrado.");
        return;
    }
    PlanoDeSaude novoPlano = new PlanoDeSaude(nome);
    this.planosDeSaude.add(novoPlano);
    System.out.println("Plano '" + nome + "' cadastrado com sucesso!");
    // Persistencia.salvarPlanos(this.planosDeSaude); // Adicionaremos a persistência depois
}

public PlanoDeSaude buscarPlanoPorNome(String nome) {
    for (PlanoDeSaude p : this.planosDeSaude) {
        if (p.getNome().equalsIgnoreCase(nome)) {
            return p;
        }
    }
    return null;
}

public List<PlanoDeSaude> getPlanosDeSaude() {
    return planosDeSaude;
}

    // --- MÉTODOS DE BUSCA E GETTERS ---
    
    public Paciente buscarPacientePorCpf(String cpf) {
        for (Paciente p : this.pacientes) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }

    public Medico buscarMedicoPorCrm(String crm) {
        for (Medico m : this.medicos) {
            if (m.getCrm().equals(crm)) {
                return m;
            }
        }
        return null;
    }

    public Especialidade buscarEspecialidadePorNome(String nome) {
        for (Especialidade e : this.especialidades) {
            if (e.getNome().equalsIgnoreCase(nome)) {
                return e;
            }
        }
        return null;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }
}