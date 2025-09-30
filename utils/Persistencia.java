package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import entities.Consulta;
import entities.Especialidade; // Importa a nova classe
import entities.Hospital;
import entities.Medico;
import entities.Paciente;

public class Persistencia {

    private static final String DIR_DADOS = "data";
    private static final Path PATH_PACIENTES = Paths.get(DIR_DADOS, "pacientes.csv");
    private static final Path PATH_MEDICOS = Paths.get(DIR_DADOS, "medicos.csv");
    private static final Path PATH_CONSULTAS = Paths.get(DIR_DADOS, "consultas.csv");
    private static final Path PATH_ESPECIALIDADES = Paths.get(DIR_DADOS, "especialidades.csv"); // Novo caminho
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

    public static void salvarEspecialidades(List<Especialidade> especialidades) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_ESPECIALIDADES)) {
            writer.write("nome\n"); // Cabeçalho
            for (Especialidade e : especialidades) {
                writer.write(e.getNome() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar especialidades: " + e.getMessage());
        }
    }
    
    public static void salvarMedicos(List<Medico> medicos) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_MEDICOS)) {
            writer.write("nome;cpf;crm;especialidade_nome\n"); // Cabeçalho alterado
            for (Medico m : medicos) {
                // Salvamos o NOME da especialidade, que é nosso identificador
                String linha = m.getNome() + SEPARADOR + m.getCpf() + SEPARADOR + m.getCrm() + SEPARADOR + m.getEspecialidade().getNome();
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
        carregarEspecialidades(hospital); // Primeiro, carregamos as especialidades
        carregarPacientes(hospital);
        carregarMedicos(hospital);      // Depois, os médicos (que dependem das especialidades)
        carregarConsultas(hospital);
    }

    private static void carregarEspecialidades(Hospital hospital) {
        if (!Files.exists(PATH_ESPECIALIDADES)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_ESPECIALIDADES)) {
            reader.readLine(); // Pula cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                hospital.cadastrarEspecialidadeSemSalvar(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar especialidades: " + e.getMessage());
        }
    }

    private static void carregarPacientes(Hospital hospital) {
        if (!Files.exists(PATH_PACIENTES)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_PACIENTES)) {
            reader.readLine(); // Pula o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                hospital.cadastrarPacienteSemSalvar(dados[0], dados[1], Integer.parseInt(dados[2]));
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
                String nomeEspecialidade = dados[3];
                // Buscamos o objeto Especialidade a partir do nome lido do arquivo
                Especialidade especialidade = hospital.buscarEspecialidadePorNome(nomeEspecialidade);
                if (especialidade != null) {
                    hospital.cadastrarMedicoSemSalvar(dados[0], dados[1], dados[2], especialidade);
                } else {
                    System.err.println("Erro: Especialidade '" + nomeEspecialidade + "' não encontrada para o médico '" + dados[0] + "'.");
                }
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
                Paciente p = hospital.buscarPacientePorCpf(dados[0]);
                Medico m = hospital.buscarMedicoPorCrm(dados[1]);
                if (p != null && m != null) {
                    LocalDateTime dataHora = LocalDateTime.parse(dados[2], FORMATTER);
                    hospital.agendarConsultaSemSalvar(p, m, dataHora, dados[3]);
                    // Atualiza o status se necessário
                    if(dados.length > 4) { // Verifica se a coluna status existe
                        Consulta consultaRecemCriada = hospital.getConsultas().get(hospital.getConsultas().size() - 1);
                        consultaRecemCriada.setStatus(dados[4]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private static void criarDiretorioSeNaoExistir() {
        try {
            Files.createDirectories(Paths.get(DIR_DADOS));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de dados: " + e.getMessage());
        }
    }
}

