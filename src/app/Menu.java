package app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import entities.*;
import utils.Persistencia;

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
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
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
                    case 0: System.out.println("A sair do sistema..."); break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: Entrada inválida ou falha na operação. Tente novamente.");
                opcao = -1;
            }
        }
        scanner.close();
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
        System.out.print("Escolha o número da consulta: ");
        int indiceConsulta = Integer.parseInt(scanner.nextLine()) - 1;
        Consulta consultaEscolhida = consultasAgendadas.get(indiceConsulta);

        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Concluir Consulta");
        System.out.println("2. Cancelar Consulta");
        System.out.print("Escolha uma opção: ");
        int acao = Integer.parseInt(scanner.nextLine());

        if (acao == 1) {
            System.out.print("Digite o diagnóstico: ");
            String diagnostico = scanner.nextLine();
            System.out.print("Digite a prescrição: ");
            String prescricao = scanner.nextLine();
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
        System.out.print("Digite o número do quarto: ");
        int numero = Integer.parseInt(scanner.nextLine());
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

    private void internarPaciente() {
        System.out.println("\n--- Internar Paciente ---");
        if (hospital.getPacientes().isEmpty() || hospital.getMedicos().isEmpty() || hospital.getQuartosLivres().isEmpty()) {
            System.out.println("Erro: É necessário ter pacientes, médicos e quartos livres cadastrados para realizar uma internação.");
            return;
        }

        System.out.println("Selecione o paciente a ser internado:");
        listarPacientes();
        System.out.print("Escolha o número do paciente: ");
        int indicePaciente = Integer.parseInt(scanner.nextLine()) - 1;
        Paciente pacienteEscolhido = hospital.getPacientes().get(indicePaciente);

        System.out.println("\nSelecione o médico responsável:");
        listarMedicos();
        System.out.print("Escolha o número do médico: ");
        int indiceMedico = Integer.parseInt(scanner.nextLine()) - 1;
        Medico medicoEscolhido = hospital.getMedicos().get(indiceMedico);

        System.out.println("\nSelecione um quarto livre:");
        List<Quarto> quartosLivres = hospital.getQuartosLivres();
        for (int i = 0; i < quartosLivres.size(); i++) {
            System.out.println((i + 1) + ". " + quartosLivres.get(i).toString());
        }
        System.out.print("Escolha o número do quarto: ");
        int indiceQuarto = Integer.parseInt(scanner.nextLine()) - 1;
        Quarto quartoEscolhido = quartosLivres.get(indiceQuarto);

        System.out.print("Digite a data de entrada (dd/MM/yyyy): ");
        String dataEntradaStr = scanner.nextLine();
        LocalDate dataEntrada = LocalDate.parse(dataEntradaStr, formatterInternacao);

        hospital.internarPaciente(pacienteEscolhido, medicoEscolhido, quartoEscolhido, dataEntrada);
    }

    private void darAltaPaciente() {
        System.out.println("\n--- Dar Alta a Paciente ---");
        List<Internacao> internados = new ArrayList<>();
        for (Internacao i : hospital.getInternacoes()) {
            if (i.getDataSaida() == null) {
                internados.add(i);
            }
        }

        if (internados.isEmpty()) {
            System.out.println("Não há pacientes internados no momento.");
            return;
        }

        System.out.println("Selecione a internação para dar alta:");
        for (int i = 0; i < internados.size(); i++) {
            System.out.println((i + 1) + ". " + internados.get(i).toString());
        }
        System.out.print("Escolha o número da internação: ");
        int indiceInternacao = Integer.parseInt(scanner.nextLine()) - 1;
        Internacao internacaoEscolhida = internados.get(indiceInternacao);

        System.out.print("Digite a data de alta (dd/MM/yyyy): ");
        String dataSaidaStr = scanner.nextLine();
        LocalDate dataSaida = LocalDate.parse(dataSaidaStr, formatterInternacao);

        internacaoEscolhida.registrarAlta(dataSaida);
        Persistencia.salvarInternacoes(hospital.getInternacoes());
        System.out.println("Alta registrada com sucesso para o paciente " + internacaoEscolhida.getPaciente().getNome() + ".");
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

    private void cadastrarPaciente() {
        System.out.println("\n--- Cadastro de Paciente ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Idade: ");
        int idade = Integer.parseInt(scanner.nextLine());
        System.out.print("O paciente tem plano de saúde? (S/N): ");
        String temPlano = scanner.nextLine();

        if (temPlano.equalsIgnoreCase("S")) {
            if (hospital.getPlanosDeSaude().isEmpty()) {
                System.out.println("Nenhum plano de saúde cadastrado. Cadastre um plano antes de adicionar o paciente.");
                return;
            }
            System.out.println("Selecione o plano de saúde:");
            listarPlanosDeSaude();
            System.out.print("Escolha o número do plano: ");
            int indicePlano = Integer.parseInt(scanner.nextLine()) - 1;
            PlanoDeSaude planoEscolhido = hospital.getPlanosDeSaude().get(indicePlano);
            hospital.cadastrarPaciente(nome, cpf, idade, planoEscolhido);
        } else {
            hospital.cadastrarPaciente(nome, cpf, idade);
        }
    }

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
        System.out.print("Nome da especialidade: ");
        String nome = scanner.nextLine();
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

    private void cadastrarMedico() {
        System.out.println("\n--- Cadastro de Médico ---");
        if (hospital.getEspecialidades().isEmpty()) {
            System.out.println("Erro: É necessário cadastrar ao menos uma especialidade antes de cadastrar um médico.");
            return;
        }
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("CRM: ");
        String crm = scanner.nextLine();
        
        System.out.println("Selecione a especialidade:");
        listarEspecialidades();
        System.out.print("Escolha o número da especialidade: ");
        int indiceEspecialidade = Integer.parseInt(scanner.nextLine()) - 1;
        Especialidade especialidadeEscolhida = hospital.getEspecialidades().get(indiceEspecialidade);
        
        System.out.print("Custo base da consulta (ex: 150.00): ");
        double custoConsulta = Double.parseDouble(scanner.nextLine());
        
        hospital.cadastrarMedico(nome, cpf, crm, especialidadeEscolhida, custoConsulta);
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
        System.out.print("Nome do plano: ");
        String nome = scanner.nextLine();
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
        System.out.print("Escolha o número do plano: ");
        int indicePlano = Integer.parseInt(scanner.nextLine()) - 1;
        PlanoDeSaude planoEscolhido = hospital.getPlanosDeSaude().get(indicePlano);

        System.out.println("\nSelecione a especialidade para o desconto:");
        listarEspecialidades();
        System.out.print("Escolha o número da especialidade: ");
        int indiceEspecialidade = Integer.parseInt(scanner.nextLine()) - 1;
        Especialidade especialidadeEscolhida = hospital.getEspecialidades().get(indiceEspecialidade);

        System.out.print("Digite o desconto (ex: 0.2 para 20%): ");
        double desconto = Double.parseDouble(scanner.nextLine());

        planoEscolhido.adicionarDesconto(especialidadeEscolhida, desconto);
        System.out.println("Desconto adicionado com sucesso!");
    }

    private void agendarConsulta() {
        System.out.println("\n--- Agendamento de Consulta ---");
        if (hospital.getPacientes().isEmpty() || hospital.getMedicos().isEmpty()) {
            System.out.println("Erro: É necessário ter ao menos um paciente e um médico cadastrados.");
            return;
        }
        System.out.println("Selecione o paciente:");
        listarPacientes();
        System.out.print("Escolha o número do paciente: ");
        int indicePaciente = Integer.parseInt(scanner.nextLine()) - 1;
        Paciente pacienteEscolhido = hospital.getPacientes().get(indicePaciente);

        System.out.println("\nSelecione o médico:");
        listarMedicos();
        System.out.print("Escolha o número do médico: ");
        int indiceMedico = Integer.parseInt(scanner.nextLine()) - 1;
        Medico medicoEscolhido = hospital.getMedicos().get(indiceMedico);

        double custoFinal = pacienteEscolhido.calcularCustoConsulta(medicoEscolhido);
        System.out.println("Custo estimado da consulta: R$" + String.format("%.2f", custoFinal));
        
        System.out.print("Confirmar agendamento? (S/N): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            System.out.print("Digite a data e hora da consulta (formato dd/MM/yyyy HH:mm): ");
            String dataHoraString = scanner.nextLine();
            System.out.print("Digite o local da consulta (ex: Consultório 3): ");
            String local = scanner.nextLine();
            try {
                LocalDateTime dataHora = LocalDateTime.parse(dataHoraString, formatterConsulta);
                hospital.agendarConsulta(pacienteEscolhido, medicoEscolhido, dataHora, local);
                System.out.println("Consulta agendada com sucesso!");
            } catch (DateTimeParseException e) {
                System.out.println("Erro: Formato de data e hora inválido. Use o formato dd/MM/yyyy HH:mm.");
            }
        } else {
            System.out.println("Agendamento cancelado.");
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
}