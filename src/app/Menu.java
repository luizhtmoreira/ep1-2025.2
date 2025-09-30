package app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import entities.*;
import utils.Persistencia;
import utils.Validador;

public class Menu {

    private Hospital hospital;
    private Scanner scanner;
    private DateTimeFormatter formatterConsulta = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DateTimeFormatter formatterInternacao = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Menu(Hospital hospital) {
        this.hospital = hospital;
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenuPrincipal() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n---- GESTÃO DE PACIENTES ----");
            System.out.println("1. Cadastrar Paciente");
            System.out.println("2. Listar Pacientes");
            System.out.println("\n---- GESTÃO DE PESSOAL MÉDICO ----");
            System.out.println("3. Cadastrar Especialidade");
            System.out.println("4. Listar Especialidades");
            System.out.println("5. Cadastrar Médico");
            System.out.println("6. Listar Médicos");
            System.out.println("\n---- GESTÃO DE PLANOS DE SAÚDE ----");
            System.out.println("7. Cadastrar Plano de Saúde");
            System.out.println("8. Listar Planos de Saúde");
            System.out.println("9. Adicionar Desconto a Plano");
            System.out.println("\n---- GESTÃO DE INFRAESTRUTURA ----");
            System.out.println("10. Cadastrar Quarto");
            System.out.println("11. Listar Quartos");
            System.out.println("\n---- OPERAÇÕES HOSPITALARES ----");
            System.out.println("12. Agendar Consulta");
            System.out.println("13. Gerir Consulta (Concluir/Cancelar)");
            System.out.println("14. Listar Consultas");
            System.out.println("15. Internar Paciente");
            System.out.println("16. Dar Alta a Paciente");
            System.out.println("17. Listar Internações");
            System.out.println("\n---- RELATÓRIOS E ESTATÍSTICAS ----");
            System.out.println("18. Ver Relatórios");
            System.out.println("0. Sair");
            
            opcao = Validador.lerInteiroPositivo("Escolha uma opção: ", scanner);
            
            switch (opcao) {
                case 1: cadastrarPaciente(); break;
                case 2: listarPacientes(); break;
                case 3: cadastrarEspecialidade(); break;
                case 4: listarEspecialidades(); break;
                case 5: cadastrarMedico(); break;
                case 6: listarMedicos(); break;
                case 7: cadastrarPlanoDeSaude(); break;
                case 8: listarPlanosDeSaude(); break;
                case 9: adicionarDescontoAPlano(); break;
                case 10: cadastrarQuarto(); break;
                case 11: listarQuartos(); break;
                case 12: agendarConsulta(); break;
                case 13: gerirConsulta(); break;
                case 14: listarConsultas(); break;
                case 15: internarPaciente(); break;
                case 16: darAltaPaciente(); break;
                case 17: listarInternacoes(); break;
                case 18: exibirMenuRelatorios(); break;
                case 0: System.out.println("A sair do sistema..."); break;
                default: System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }
    
    private void cadastrarPaciente() {
        System.out.println("\n--- Cadastro de Paciente ---");
        String nome = Validador.lerStringNaoVazia("Nome: ", scanner);
        String cpf = Validador.lerCpf("CPF (11 dígitos): ", scanner);
        int idade = Validador.lerInteiroPositivo("Idade: ", scanner);
        
        System.out.print("O paciente tem plano de saúde? (S/N): ");
        String temPlano = scanner.nextLine();

        if (temPlano.equalsIgnoreCase("S")) {
            if (hospital.getPlanosDeSaude().isEmpty()) {
                System.out.println("Nenhum plano de saúde cadastrado. Cadastre um plano antes de adicionar o paciente.");
                return;
            }
            System.out.println("Selecione o plano de saúde:");
            listarPlanosDeSaude();
            int indicePlano = Validador.lerInteiroPositivo("Escolha o número do plano: ", scanner) - 1;
            
            if (indicePlano >= 0 && indicePlano < hospital.getPlanosDeSaude().size()) {
                PlanoDeSaude planoEscolhido = hospital.getPlanosDeSaude().get(indicePlano);
                hospital.cadastrarPaciente(nome, cpf, idade, planoEscolhido);
            } else {
                System.out.println("Erro: Opção de plano inválida.");
            }
        } else {
            hospital.cadastrarPaciente(nome, cpf, idade);
        }
    }

    private void cadastrarMedico() {
        System.out.println("\n--- Cadastro de Médico ---");
        if (hospital.getEspecialidades().isEmpty()) {
            System.out.println("Erro: É necessário cadastrar ao menos uma especialidade antes de cadastrar um médico.");
            return;
        }
        String nome = Validador.lerStringNaoVazia("Nome: ", scanner);
        String cpf = Validador.lerCpf("CPF (11 dígitos): ", scanner);
        String crm = Validador.lerCrm("CRM: ", scanner);
        
        System.out.println("Selecione a especialidade:");
        listarEspecialidades();
        int indiceEspecialidade = Validador.lerInteiroPositivo("Escolha o número da especialidade: ", scanner) - 1;

        if (indiceEspecialidade >= 0 && indiceEspecialidade < hospital.getEspecialidades().size()) {
            Especialidade especialidadeEscolhida = hospital.getEspecialidades().get(indiceEspecialidade);
            double custoConsulta = Validador.lerDoublePositivo("Custo base da consulta (ex: 150.00): ", scanner);
            hospital.cadastrarMedico(nome, cpf, crm, especialidadeEscolhida, custoConsulta);
        } else {
            System.out.println("Erro: Opção de especialidade inválida.");
        }
    }
    
    private void agendarConsulta() {
        System.out.println("\n--- Agendamento de Consulta ---");
        if (hospital.getPacientes().isEmpty() || hospital.getMedicos().isEmpty()) {
            System.out.println("Erro: É necessário ter ao menos um paciente e um médico cadastrados.");
            return;
        }
        System.out.println("Selecione o paciente:");
        listarPacientes();
        int indicePaciente = Validador.lerInteiroPositivo("Escolha o número do paciente: ", scanner) - 1;
        
        System.out.println("\nSelecione o médico:");
        listarMedicos();
        int indiceMedico = Validador.lerInteiroPositivo("Escolha o número do médico: ", scanner) - 1;

        if (indicePaciente < 0 || indicePaciente >= hospital.getPacientes().size() || indiceMedico < 0 || indiceMedico >= hospital.getMedicos().size()) {
            System.out.println("Erro: Opção de paciente ou médico inválida.");
            return;
        }

        Paciente pacienteEscolhido = hospital.getPacientes().get(indicePaciente);
        Medico medicoEscolhido = hospital.getMedicos().get(indiceMedico);

        double custoFinal = pacienteEscolhido.calcularCustoConsulta(medicoEscolhido);
        System.out.println("Custo estimado da consulta: R$" + String.format("%.2f", custoFinal));
        
        System.out.print("Confirmar agendamento? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            LocalDateTime dataHora = Validador.lerDataHora("Digite a data e hora da consulta (formato dd/MM/yyyy HH:mm): ", scanner, formatterConsulta);
            String local = Validador.lerStringNaoVazia("Digite o local da consulta (ex: Consultório 3): ", scanner);
            hospital.agendarConsulta(pacienteEscolhido, medicoEscolhido, dataHora, local);
            System.out.println("Consulta agendada com sucesso!");
        } else {
            System.out.println("Agendamento cancelado.");
        }
    }

    private void internarPaciente() {
        System.out.println("\n--- Internar Paciente ---");
        if (hospital.getPacientes().isEmpty() || hospital.getMedicos().isEmpty() || hospital.getQuartosLivres().isEmpty()) {
            System.out.println("Erro: É necessário ter pacientes, médicos e quartos livres cadastrados para realizar uma internação.");
            return;
        }

        System.out.println("Selecione o paciente a ser internado:");
        listarPacientes();
        int indicePaciente = Validador.lerInteiroPositivo("Escolha o número do paciente: ", scanner) - 1;
        
        System.out.println("\nSelecione o médico responsável:");
        listarMedicos();
        int indiceMedico = Validador.lerInteiroPositivo("Escolha o número do médico: ", scanner) - 1;

        System.out.println("\nSelecione um quarto livre:");
        List<Quarto> quartosLivres = hospital.getQuartosLivres();
        for (int i = 0; i < quartosLivres.size(); i++) {
            System.out.println((i + 1) + ". " + quartosLivres.get(i).toString());
        }
        int indiceQuarto = Validador.lerInteiroPositivo("Escolha o número do quarto: ", scanner) - 1;

        if(indicePaciente < 0 || indicePaciente >= hospital.getPacientes().size() ||
           indiceMedico < 0 || indiceMedico >= hospital.getMedicos().size() ||
           indiceQuarto < 0 || indiceQuarto >= quartosLivres.size()){
            System.out.println("Erro: Opção inválida.");
            return;
        }
        
        Paciente pacienteEscolhido = hospital.getPacientes().get(indicePaciente);
        Medico medicoEscolhido = hospital.getMedicos().get(indiceMedico);
        Quarto quartoEscolhido = quartosLivres.get(indiceQuarto);
        
        LocalDate dataEntrada = Validador.lerData("Digite a data de entrada (dd/MM/yyyy): ", scanner, formatterInternacao);

        hospital.internarPaciente(pacienteEscolhido, medicoEscolhido, quartoEscolhido, dataEntrada);
    }

    private void darAltaPaciente() {
        System.out.println("\n--- Dar Alta a Paciente ---");
        List<Internacao> internados = hospital.getPacientesInternados();

        if (internados.isEmpty()) {
            System.out.println("Não há pacientes internados no momento.");
            return;
        }

        System.out.println("Selecione a internação para dar alta:");
        for (int i = 0; i < internados.size(); i++) {
            System.out.println((i + 1) + ". " + internados.get(i).toString());
        }
        int indiceInternacao = Validador.lerInteiroPositivo("Escolha o número da internação: ", scanner) - 1;
        
        if (indiceInternacao < 0 || indiceInternacao >= internados.size()) {
            System.out.println("Erro: Opção inválida.");
            return;
        }

        Internacao internacaoEscolhida = internados.get(indiceInternacao);

        LocalDate dataSaida = Validador.lerData("Digite a data de alta (dd/MM/yyyy): ", scanner, formatterInternacao);

        internacaoEscolhida.registrarAlta(dataSaida);
        Persistencia.salvarInternacoes(hospital.getInternacoes());
        System.out.println("Alta registrada com sucesso para o paciente " + internacaoEscolhida.getPaciente().getNome() + ".");
    }
    
    // Todos os outros métodos permanecem os mesmos e estão aqui para garantir a completude
    
    private void listarPacientes() {
        System.out.println("\n--- Lista de Pacientes ---");
        List<Paciente> pacientes = hospital.getPacientes();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
        } else {
            for (int i = 0; i < pacientes.size(); i++) {
                System.out.println((i + 1) + ". " + pacientes.get(i).toString());
            }
        }
    }

    private void cadastrarEspecialidade() {
        System.out.println("\n--- Cadastro de Especialidade ---");
        String nome = Validador.lerStringNaoVazia("Nome da especialidade: ", scanner);
        hospital.cadastrarEspecialidade(nome);
    }

    private void listarEspecialidades() {
        System.out.println("\n--- Lista de Especialidades ---");
        List<Especialidade> especialidades = hospital.getEspecialidades();
        if (especialidades.isEmpty()) {
            System.out.println("Nenhuma especialidade cadastrada.");
        } else {
            for (int i = 0; i < especialidades.size(); i++) {
                System.out.println((i + 1) + ". " + especialidades.get(i).toString());
            }
        }
    }

    private void listarMedicos() {
        System.out.println("\n--- Lista de Médicos ---");
        List<Medico> medicos = hospital.getMedicos();
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico cadastrado.");
        } else {
            for (int i = 0; i < medicos.size(); i++) {
                System.out.println((i + 1) + ". " + medicos.get(i).toString());
            }
        }
    }

    private void cadastrarPlanoDeSaude() {
        System.out.println("\n--- Cadastro de Plano de Saúde ---");
        String nome = Validador.lerStringNaoVazia("Nome do plano: ", scanner);
        hospital.cadastrarPlanoDeSaude(nome);
    }

    private void listarPlanosDeSaude() {
        System.out.println("\n--- Lista de Planos de Saúde ---");
        List<PlanoDeSaude> planos = hospital.getPlanosDeSaude();
        if (planos.isEmpty()) {
            System.out.println("Nenhum plano de saúde cadastrado.");
        } else {
            for (int i = 0; i < planos.size(); i++) {
                System.out.println((i + 1) + ". " + planos.get(i).toString());
            }
        }
    }

    private void adicionarDescontoAPlano() {
        System.out.println("\n--- Adicionar Desconto a Plano de Saúde ---");
        if (hospital.getPlanosDeSaude().isEmpty() || hospital.getEspecialidades().isEmpty()) {
            System.out.println("Erro: É necessário ter ao menos um plano e uma especialidade cadastrados.");
            return;
        }

        System.out.println("Selecione o plano de saúde:");
        listarPlanosDeSaude();
        int indicePlano = Validador.lerInteiroPositivo("Escolha o número do plano: ", scanner) - 1;
        
        System.out.println("\nSelecione a especialidade para o desconto:");
        listarEspecialidades();
        int indiceEspecialidade = Validador.lerInteiroPositivo("Escolha o número da especialidade: ", scanner) - 1;

        if (indicePlano >= 0 && indicePlano < hospital.getPlanosDeSaude().size() && indiceEspecialidade >= 0 && indiceEspecialidade < hospital.getEspecialidades().size()) {
            PlanoDeSaude planoEscolhido = hospital.getPlanosDeSaude().get(indicePlano);
            Especialidade especialidadeEscolhida = hospital.getEspecialidades().get(indiceEspecialidade);
            double desconto = Validador.lerDoublePositivo("Digite o desconto (ex: 0.2 para 20%): ", scanner);
            planoEscolhido.adicionarDesconto(especialidadeEscolhida, desconto);
            System.out.println("Desconto adicionado com sucesso!");
        } else {
            System.out.println("Erro: Opção de plano ou especialidade inválida.");
        }
    }

    private void listarConsultas() {
        System.out.println("\n--- Histórico de Consultas ---");
        List<Consulta> consultas = hospital.getConsultas();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta registada.");
        } else {
            for (Consulta consulta : consultas) {
                System.out.println("---------------------------------");
                System.out.println("Paciente: " + consulta.getPaciente().getNome());
                System.out.println("Médico: " + consulta.getMedico().getNome() + " (" + consulta.getMedico().getEspecialidade() + ")");
                System.out.println("Data/Hora: " + consulta.getDataHora().format(formatterConsulta));
                System.out.println("Local: " + consulta.getLocal());
                System.out.println("Status: " + consulta.getStatus());
                if (!consulta.getDiagnostico().isEmpty()) {
                    System.out.println("Diagnóstico: " + consulta.getDiagnostico());
                    System.out.println("Prescrição: " + consulta.getPrescricao());
                }
            }
            System.out.println("---------------------------------");
        }
    }

    private void gerirConsulta() {
        System.out.println("\n--- Gerir Consulta ---");
        
        List<Consulta> consultasAgendadas = hospital.getConsultas().stream()
            .filter(c -> c.getStatus().equals("AGENDADA"))
            .collect(Collectors.toList());

        if (consultasAgendadas.isEmpty()) {
            System.out.println("Não há consultas agendadas para gerir.");
            return;
        }

        System.out.println("Selecione a consulta a gerir:");
        for (int i = 0; i < consultasAgendadas.size(); i++) {
            System.out.println((i + 1) + ". " + consultasAgendadas.get(i).toString());
        }
        int indiceConsulta = Validador.lerInteiroPositivo("Escolha o número da consulta: ", scanner) - 1;

        if (indiceConsulta < 0 || indiceConsulta >= consultasAgendadas.size()) {
            System.out.println("Erro: Opção inválida.");
            return;
        }
        
        Consulta consultaEscolhida = consultasAgendadas.get(indiceConsulta);

        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Concluir Consulta");
        System.out.println("2. Cancelar Consulta");
        int acao = Validador.lerInteiroPositivo("Escolha uma opção: ", scanner);

        if (acao == 1) {
            String diagnostico = Validador.lerStringNaoVazia("Digite o diagnóstico: ", scanner);
            String prescricao = Validador.lerStringNaoVazia("Digite a prescrição: ", scanner);
            consultaEscolhida.concluirConsulta(diagnostico, prescricao);
            System.out.println("Consulta concluída com sucesso!");
        } else if (acao == 2) {
            consultaEscolhida.cancelarConsulta();
            System.out.println("Consulta cancelada com sucesso!");
        } else {
            System.out.println("Ação inválida.");
            return;
        }
        
        hospital.atualizarConsultasNoArquivo();
    }

    private void cadastrarQuarto() {
        System.out.println("\n--- Cadastro de Quarto ---");
        int numero = Validador.lerInteiroPositivo("Digite o número do quarto: ", scanner);
        hospital.cadastrarQuarto(numero);
    }

    private void listarQuartos() {
        System.out.println("\n--- Lista de Todos os Quartos ---");
        List<Quarto> quartos = hospital.getQuartos();
        if (quartos.isEmpty()) {
            System.out.println("Nenhum quarto cadastrado.");
        } else {
            for (int i = 0; i < quartos.size(); i++) {
                System.out.println((i + 1) + ". " + quartos.get(i).toString());
            }
        }
    }

    private void listarInternacoes() {
        System.out.println("\n--- Histórico de Internações ---");
        List<Internacao> internacoes = hospital.getInternacoes();
        if (internacoes.isEmpty()) {
            System.out.println("Nenhuma internação registrada.");
        } else {
            for (Internacao i : internacoes) {
                System.out.println(i.toString());
            }
        }
    }

    private void exibirMenuRelatorios() {
        System.out.println("\n--- Submenu de Relatórios ---");
        System.out.println("1. Pacientes internados no momento");
        System.out.println("2. Consultas concluídas por médico");
        System.out.println("3. Estatísticas gerais (médico mais ativo, especialidade mais procurada)");
        int opcao = Validador.lerInteiroPositivo("Escolha uma opção: ", scanner);
        
        switch (opcao) {
            case 1:
                relatorioPacientesInternados();
                break;
            case 2:
                relatorioConsultasPorMedico();
                break;
            case 3:
                relatorioEstatisticasGerais();
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void relatorioPacientesInternados() {
        System.out.println("\n--- Relatório: Pacientes Internados Atualmente ---");
        List<Internacao> internados = hospital.getPacientesInternados();
        if (internados.isEmpty()) {
            System.out.println("Não há pacientes internados no momento.");
        } else {
            internados.forEach(System.out::println);
        }
    }

    private void relatorioConsultasPorMedico() {
        System.out.println("\n--- Relatório: Consultas Concluídas por Médico ---");
        List<Medico> medicos = hospital.getMedicos();
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico cadastrado.");
        } else {
            for (Medico m : medicos) {
                long numConsultas = hospital.getNumeroDeConsultasPorMedico(m);
                System.out.println("- " + m.getNome() + " (" + m.getEspecialidade() + "): " + numConsultas + " consultas concluídas.");
            }
        }
    }

    private void relatorioEstatisticasGerais() {
        System.out.println("\n--- Relatório: Estatísticas Gerais ---");
        
        Optional<Medico> medicoMaisAtivoOpt = hospital.getMedicoMaisAtivo();
        if (medicoMaisAtivoOpt.isPresent()) {
            Medico medico = medicoMaisAtivoOpt.get();
            System.out.println("Médico mais ativo: " + medico.getNome() + " com " + hospital.getNumeroDeConsultasPorMedico(medico) + " consultas.");
        } else {
            System.out.println("Médico mais ativo: Não há dados de consultas concluídas.");
        }

        Optional<Especialidade> especialidadeMaisProcuradaOpt = hospital.getEspecialidadeMaisProcurada();
        if (especialidadeMaisProcuradaOpt.isPresent()) {
            System.out.println("Especialidade mais procurada: " + especialidadeMaisProcuradaOpt.get().getNome());
        } else {
            System.out.println("Especialidade mais procurada: Não há dados de consultas concluídas.");
        }
    }
}