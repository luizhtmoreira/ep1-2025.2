package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import utils.Persistencia;

public class Hospital {

    private List<Paciente> pacientes;
    private List<Medico> medicos;
    private List<Consulta> consultas;
    private List<Especialidade> especialidades;
    private List<PlanoDeSaude> planosDeSaude;
    private List<Quarto> quartos;
    private List<Internacao> internacoes;

    public Hospital() {
        this.pacientes = new ArrayList<>();
        this.medicos = new ArrayList<>();
        this.consultas = new ArrayList<>();
        this.especialidades = new ArrayList<>();
        this.planosDeSaude = new ArrayList<>();
        this.quartos = new ArrayList<>();
        this.internacoes = new ArrayList<>();
        System.out.println("A carregar dados existentes...");
        Persistencia.carregarDados(this);
    }
    
    private boolean isCpfEmUso(String cpf) {
        for (Paciente p : this.pacientes) {
            if (p.getCpf().equals(cpf)) {
                return true;
            }
        }
        for (Medico m : this.medicos) {
            if (m.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }

    public void cadastrarPaciente(String nome, String cpf, int idade) {
        if (isCpfEmUso(cpf)) {
            System.out.println("Erro: Já existe uma pessoa (paciente ou médico) cadastrada com este CPF.");
            return;
        }
        cadastrarPacienteSemSalvar(nome, cpf, idade);
        System.out.println("Paciente '" + nome + "' (Normal) cadastrado com sucesso!");
        Persistencia.salvarPacientes(this.pacientes);
    }

    public void cadastrarPaciente(String nome, String cpf, int idade, PlanoDeSaude plano) {
        if (isCpfEmUso(cpf)) {
            System.out.println("Erro: Já existe uma pessoa (paciente ou médico) cadastrada com este CPF.");
            return;
        }
        cadastrarPacienteComPlanoSemSalvar(nome, cpf, idade, plano);
        System.out.println("Paciente '" + nome + "' (Plano: " + plano.getNome() + ") cadastrado com sucesso!");
        Persistencia.salvarPacientes(this.pacientes);
    }

    public void cadastrarMedico(String nome, String cpf, String crm, Especialidade especialidade, double custoConsulta) {
        if (isCpfEmUso(cpf)) {
            System.out.println("Erro: Já existe uma pessoa (paciente ou médico) cadastrada com este CPF.");
            return;
        }
        if (buscarMedicoPorCrm(crm) != null) {
            System.out.println("Erro: Já existe um médico cadastrado com este CRM.");
            return;
        }
        cadastrarMedicoSemSalvar(nome, cpf, crm, especialidade, custoConsulta);
        System.out.println("Médico '" + nome + "' cadastrado com sucesso!");
        Persistencia.salvarMedicos(this.medicos);
    }
    
    public List<Internacao> getPacientesInternados() {
        return this.internacoes.stream()
            .filter(i -> i.getDataSaida() == null)
            .collect(Collectors.toList());
    }

    public long getNumeroDeConsultasPorMedico(Medico medico) {
        return this.consultas.stream()
            .filter(c -> c.getMedico().equals(medico) && c.getStatus().equals("CONCLUIDA"))
            .count();
    }

    public Optional<Medico> getMedicoMaisAtivo() {
        return this.medicos.stream()
            .max(Comparator.comparing(m -> getNumeroDeConsultasPorMedico(m)));
    }

    public Optional<Especialidade> getEspecialidadeMaisProcurada() {
        if (consultas.isEmpty()) {
            return Optional.empty();
        }
        Map<Especialidade, Long> contagem = this.consultas.stream()
            .filter(c -> c.getStatus().equals("CONCLUIDA"))
            .collect(Collectors.groupingBy(c -> c.getMedico().getEspecialidade(), Collectors.counting()));

        return contagem.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }

    public void atualizarConsultasNoArquivo() {
        Persistencia.salvarConsultas(this.consultas);
    }

    public void cadastrarQuarto(int numero) {
        if (buscarQuartoPorNumero(numero) != null) {
            System.out.println("Erro: Quarto número " + numero + " já cadastrado.");
            return;
        }
        cadastrarQuartoSemSalvar(numero);
        System.out.println("Quarto " + numero + " cadastrado com sucesso!");
        Persistencia.salvarQuartos(this.quartos);
    }

    public void internarPaciente(Paciente paciente, Medico medico, Quarto quarto, LocalDate dataEntrada) {
        for (Internacao i : this.internacoes) {
            if (i.getPaciente().equals(paciente) && i.getDataSaida() == null) {
                System.out.println("Erro: O paciente " + paciente.getNome() + " já está internado.");
                return;
            }
        }
        if (quarto.isOcupado()) {
            System.out.println("Erro: O quarto " + quarto.getNumero() + " já está ocupado.");
            return;
        }
        internarPacienteSemSalvar(paciente, medico, quarto, dataEntrada);
        System.out.println("Paciente " + paciente.getNome() + " internado com sucesso no quarto " + quarto.getNumero() + ".");
        Persistencia.salvarInternacoes(this.internacoes);
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
    
    public void cadastrarPlanoDeSaude(String nome) {
        if (buscarPlanoPorNome(nome) != null) {
            System.out.println("Erro: Plano de saúde já cadastrado.");
            return;
        }
        cadastrarPlanoDeSaudeSemSalvar(nome);
        System.out.println("Plano '" + nome + "' cadastrado com sucesso!");
        Persistencia.salvarPlanosDeSaude(this.planosDeSaude);
    }

    public void agendarConsulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local) {
        for (Consulta consultaExistente : this.consultas) {
            if (consultaExistente.getMedico().equals(medico) && consultaExistente.getDataHora().isEqual(dataHora)) {
                System.out.println("Erro: O médico " + medico.getNome() + " já possui uma consulta agendada para este horário.");
                return;
            }
        }
        agendarConsultaSemSalvar(paciente, medico, dataHora, local);
        Persistencia.salvarConsultas(this.consultas);
    }
    
    public void cadastrarQuartoSemSalvar(int numero) {
        Quarto novoQuarto = new Quarto(numero);
        this.quartos.add(novoQuarto);
    }

    public void internarPacienteSemSalvar(Paciente paciente, Medico medico, Quarto quarto, LocalDate dataEntrada) {
        quarto.ocupar();
        Internacao novaInternacao = new Internacao(paciente, medico, quarto, dataEntrada);
        this.internacoes.add(novaInternacao);
    }

    public void cadastrarPacienteSemSalvar(String nome, String cpf, int idade) {
        Paciente novoPaciente = new Paciente(nome, cpf, idade);
        this.pacientes.add(novoPaciente);
    }

    public void cadastrarPacienteComPlanoSemSalvar(String nome, String cpf, int idade, PlanoDeSaude plano) {
        PacienteComPlano novoPaciente = new PacienteComPlano(nome, cpf, idade, plano);
        this.pacientes.add(novoPaciente);
    }

    public void cadastrarEspecialidadeSemSalvar(String nome) {
        Especialidade novaEspecialidade = new Especialidade(nome);
        this.especialidades.add(novaEspecialidade);
    }
    
    public void cadastrarPlanoDeSaudeSemSalvar(String nome) {
        PlanoDeSaude novoPlano = new PlanoDeSaude(nome);
        this.planosDeSaude.add(novoPlano);
    }

    public void cadastrarMedicoSemSalvar(String nome, String cpf, String crm, Especialidade especialidade, double custoConsulta) {
        Medico novoMedico = new Medico(nome, cpf, crm, especialidade, custoConsulta);
        this.medicos.add(novoMedico);
    }

    public void agendarConsultaSemSalvar(Paciente p, Medico m, LocalDateTime dataHora, String local) {
        Consulta novaConsulta = new Consulta(p, m, dataHora, local);
        this.consultas.add(novaConsulta);
    }
    
    public Quarto buscarQuartoPorNumero(int numero) {
        for (Quarto q : this.quartos) {
            if (q.getNumero() == numero) {
                return q;
            }
        }
        return null;
    }

    public List<Quarto> getQuartosLivres() {
        return this.quartos.stream()
            .filter(quarto -> !quarto.isOcupado())
            .collect(Collectors.toList());
    }
    
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
    
    public PlanoDeSaude buscarPlanoPorNome(String nome) {
        for (PlanoDeSaude p : this.planosDeSaude) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        return null;
    }

    public List<Paciente> getPacientes() { return pacientes; }
    public List<Medico> getMedicos() { return medicos; }
    public List<Consulta> getConsultas() { return consultas; }
    public List<Especialidade> getEspecialidades() { return especialidades; }
    public List<PlanoDeSaude> getPlanosDeSaude() { return planosDeSaude; }
    public List<Quarto> getQuartos() { return quartos; }
    public List<Internacao> getInternacoes() { return internacoes; }
}