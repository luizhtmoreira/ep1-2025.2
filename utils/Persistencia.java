package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import entities.Consulta;
import entities.Hospital;
import entities.Medico;
import entities.Paciente;

public class Persistencia {

    private static final String DIR_DADOS = "data";
    private static final Path PATH_PACIENTES = Paths.get(DIR_DADOS, "pacientes.csv");
    private static final Path PATH_MEDICOS = Paths.get(DIR_DADOS, "medicos.csv");
    private static final Path PATH_CONSULTAS = Paths.get(DIR_DADOS, "consultas.csv");
    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // --- MÉTODOS PARA SALVAR DADOS ---

    public static void salvarPacientes(List<Paciente> pacientes) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_PACIENTES)) {
            writer.write("nome;cpf;idade\n"); // Cabeçalho
            for (Paciente p : pacientes) {
                String linha = p.getNome() + SEPARADOR + p.getCpf() + SEPARADOR + p.getIdade();
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar pacientes: " + e.getMessage());
        }
    }
    
    public static void salvarMedicos(List<Medico> medicos) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_MEDICOS)) {
            writer.write("nome;cpf;crm;especialidade\n"); // Cabeçalho
            for (Medico m : medicos) {
                String linha = m.getNome() + SEPARADOR + m.getCpf() + SEPARADOR + m.getCrm() + SEPARADOR + m.getEspecialidade();
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar medicos: " + e.getMessage());
        }
    }

    public static void salvarConsultas(List<Consulta> consultas) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_CONSULTAS)) {
            writer.write("cpf_paciente;crm_medico;data_hora;local;status\n"); // Cabeçalho
            for (Consulta c : consultas) {
                String linha = c.getPaciente().getCpf() + SEPARADOR + 
                               c.getMedico().getCrm() + SEPARADOR + 
                               c.getDataHora().format(FORMATTER) + SEPARADOR + 
                               c.getLocal() + SEPARADOR +
                               c.getStatus();
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar consultas: " + e.getMessage());
        }
    }

    // --- MÉTODOS PARA CARREGAR DADOS ---

    public static void carregarDados(Hospital hospital) {
        carregarPacientes(hospital);
        carregarMedicos(hospital);
        carregarConsultas(hospital);
    }

    private static void carregarPacientes(Hospital hospital) {
        if (!Files.exists(PATH_PACIENTES)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_PACIENTES)) {
            reader.readLine(); // Pula o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                hospital.cadastrarPaciente(dados[0], dados[1], Integer.parseInt(dados[2]));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar pacientes: " + e.getMessage());
        }
    }

    private static void carregarMedicos(Hospital hospital) {
        if (!Files.exists(PATH_MEDICOS)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_MEDICOS)) {
            reader.readLine(); // Pula o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                hospital.cadastrarMedico(dados[0], dados[1], dados[2], dados[3]);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar medicos: " + e.getMessage());
        }
    }

    private static void carregarConsultas(Hospital hospital) {
        if (!Files.exists(PATH_CONSULTAS)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_CONSULTAS)) {
            reader.readLine(); // Pula o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                // Encontra os objetos Paciente e Medico nas listas do hospital
                Paciente p = hospital.buscarPacientePorCpf(dados[0]);
                Medico m = hospital.buscarMedicoPorCrm(dados[1]);
                if (p != null && m != null) {
                    LocalDateTime dataHora = LocalDateTime.parse(dados[2], FORMATTER);
                    hospital.agendarConsulta(p, m, dataHora, dados[3]);
                    // Atualiza o status se necessário
                    Consulta consultaRecemCriada = hospital.getConsultas().get(hospital.getConsultas().size() - 1);
                    consultaRecemCriada.setStatus(dados[4]);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar consultas: " + e.getMessage());
        }
    }


    // --- MÉTODO AUXILIAR ---
    private static void criarDiretorioSeNaoExistir() {
        try {
            Files.createDirectories(Paths.get(DIR_DADOS));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de dados: " + e.getMessage());
        }
    }
}
