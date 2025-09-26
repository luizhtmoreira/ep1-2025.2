package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import entities.Consulta;
import entities.Especialidade;
import entities.Hospital;
import entities.Medico;
import entities.Paciente;

public class Menu {

    private Hospital hospital;
    private Scanner scanner;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Menu(Hospital hospital) {
        this.hospital = hospital;
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenuPrincipal() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n---- Sistema de Gerenciamento Hospitalar ----");
            System.out.println("1. Cadastrar Paciente");
            System.out.println("2. Listar Pacientes");
            System.out.println("3. Cadastrar Especialidade");
            System.out.println("4. Listar Especialidades");
            System.out.println("5. Cadastrar Médico");
            System.out.println("6. Listar Médicos");
            System.out.println("7. Agendar Consulta");
            System.out.println("8. Listar Consultas");
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
                    case 7: agendarConsulta(); break;
                    case 8: listarConsultas(); break;
                    case 0: System.out.println("Saindo do sistema..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Erro: Entrada inválida ou falha na operação. Tente novamente.");
                opcao = -1;
            }
        }
        scanner.close();
    }

    // --- MÉTODOS DE ESPECIALIDADE ---
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

    // --- MÉTODO DE CADASTRO DE MÉDICO (MODIFICADO) ---
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
        
        hospital.cadastrarMedico(nome, cpf, crm, especialidadeEscolhida);
    }

    // --- DEMAIS MÉTODOS (sem grandes alterações) ---
    private void cadastrarPaciente() {
        System.out.println("\n--- Cadastro de Paciente ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Idade: ");
        int idade = Integer.parseInt(scanner.nextLine());
        hospital.cadastrarPaciente(nome, cpf, idade);
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

        System.out.print("Digite a data e hora da consulta (formato dd/MM/yyyy HH:mm): ");
        String dataHoraString = scanner.nextLine();
        System.out.print("Digite o local da consulta (ex: Consultório 3): ");
        String local = scanner.nextLine();

        try {
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraString, formatter);
            hospital.agendarConsulta(pacienteEscolhido, medicoEscolhido, dataHora, local);
        } catch (DateTimeParseException e) {
            System.out.println("Erro: Formato de data e hora inválido. Use o formato dd/MM/yyyy HH:mm.");
        }
    }
    
    private void listarConsultas() {
        System.out.println("\n--- Lista de Consultas Agendadas ---");
        List<Consulta> consultas = hospital.getConsultas();
        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta agendada.");
        } else {
            for (Consulta consulta : consultas) {
                System.out.println("---------------------------------");
                System.out.println("Paciente: " + consulta.getPaciente().getNome());
                System.out.println("Médico: " + consulta.getMedico().getNome() + " (" + consulta.getMedico().getEspecialidade() + ")");
                System.out.println("Data/Hora: " + consulta.getDataHora().format(formatter));
                System.out.println("Local: " + consulta.getLocal());
                System.out.println("Status: " + consulta.getStatus());
            }
            System.out.println("---------------------------------");
        }
    }
}