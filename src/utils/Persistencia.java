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

import entities.*;

public class Persistencia {

    private static final String DIR_DADOS = "data";
    private static final Path PATH_PACIENTES = Paths.get(DIR_DADOS, "pacientes.csv");
    private static final Path PATH_MEDICOS = Paths.get(DIR_DADOS, "medicos.csv");
    private static final Path PATH_CONSULTAS = Paths.get(DIR_DADOS, "consultas.csv");
    private static final Path PATH_ESPECIALIDADES = Paths.get(DIR_DADOS, "especialidades.csv");
    private static final Path PATH_PLANOS = Paths.get(DIR_DADOS, "planos_saude.csv");
    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void salvarPlanosDeSaude(List<PlanoDeSaude> planos) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_PLANOS)) {
            writer.write("nome\n");
            for (PlanoDeSaude p : planos) {
                writer.write(p.getNome() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar planos de saúde: " + e.getMessage());
        }
    }

    public static void salvarPacientes(List<Paciente> pacientes) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_PACIENTES)) {
            writer.write("tipo;nome;cpf;idade;plano_nome\n");
            for (Paciente p : pacientes) {
                String linha;
                if (p instanceof PacienteComPlano) {
                    PacienteComPlano pcp = (PacienteComPlano) p;
                    linha = "COM_PLANO" + SEPARADOR + pcp.getNome() + SEPARADOR + pcp.getCpf() + SEPARADOR + pcp.getIdade() + SEPARADOR + pcp.getPlano().getNome();
                } else {
                    linha = "NORMAL" + SEPARADOR + p.getNome() + SEPARADOR + p.getCpf() + SEPARADOR + p.getIdade() + SEPARADOR + "N/A";
                }
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar pacientes: " + e.getMessage());
        }
    }

    public static void salvarEspecialidades(List<Especialidade> especialidades) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_ESPECIALIDADES)) {
            writer.write("nome\n");
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
            writer.write("nome;cpf;crm;especialidade_nome;custo_consulta\n");
            for (Medico m : medicos) {
                String linha = m.getNome() + SEPARADOR + m.getCpf() + SEPARADOR + m.getCrm() + SEPARADOR + m.getEspecialidade().getNome() + SEPARADOR + m.getCustoConsulta();
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar medicos: " + e.getMessage());
        }
    }

    public static void salvarConsultas(List<Consulta> consultas) {
        criarDiretorioSeNaoExistir();
        try (BufferedWriter writer = Files.newBufferedWriter(PATH_CONSULTAS)) {
            writer.write("cpf_paciente;crm_medico;data_hora;local;status\n");
            for (Consulta c : consultas) {
                String linha = c.getPaciente().getCpf() + SEPARADOR + c.getMedico().getCrm() + SEPARADOR + c.getDataHora().format(FORMATTER) + SEPARADOR + c.getLocal() + SEPARADOR + c.getStatus();
                writer.write(linha + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar consultas: " + e.getMessage());
        }
    }

    public static void carregarDados(Hospital hospital) {
        carregarEspecialidades(hospital);
        carregarPlanosDeSaude(hospital);
        carregarPacientes(hospital);
        carregarMedicos(hospital);
        carregarConsultas(hospital);
    }

    private static void carregarEspecialidades(Hospital hospital) {
        if (!Files.exists(PATH_ESPECIALIDADES)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_ESPECIALIDADES)) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                hospital.cadastrarEspecialidadeSemSalvar(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar especialidades: " + e.getMessage());
        }
    }
    
    private static void carregarPlanosDeSaude(Hospital hospital) {
        if (!Files.exists(PATH_PLANOS)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_PLANOS)) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                hospital.cadastrarPlanoDeSaudeSemSalvar(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar planos de saúde: " + e.getMessage());
        }
    }

    private static void carregarPacientes(Hospital hospital) {
        if (!Files.exists(PATH_PACIENTES)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_PACIENTES)) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                String tipo = dados[0];
                String nome = dados[1];
                String cpf = dados[2];
                int idade = Integer.parseInt(dados[3]);
                if (tipo.equals("COM_PLANO")) {
                    String nomePlano = dados[4];
                    PlanoDeSaude plano = hospital.buscarPlanoPorNome(nomePlano);
                    if (plano != null) {
                        hospital.cadastrarPacienteComPlanoSemSalvar(nome, cpf, idade, plano);
                    }
                } else {
                    hospital.cadastrarPacienteSemSalvar(nome, cpf, idade);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar pacientes: " + e.getMessage());
        }
    }

    private static void carregarMedicos(Hospital hospital) {
        if (!Files.exists(PATH_MEDICOS)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_MEDICOS)) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                String nomeEspecialidade = dados[3];
                Especialidade especialidade = hospital.buscarEspecialidadePorNome(nomeEspecialidade);
                if (especialidade != null) {
                    double custoConsulta = Double.parseDouble(dados[4]);
                    hospital.cadastrarMedicoSemSalvar(dados[0], dados[1], dados[2], especialidade, custoConsulta);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar medicos: " + e.getMessage());
        }
    }

    private static void carregarConsultas(Hospital hospital) {
        if (!Files.exists(PATH_CONSULTAS)) return;
        try (BufferedReader reader = Files.newBufferedReader(PATH_CONSULTAS)) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(SEPARADOR);
                Paciente p = hospital.buscarPacientePorCpf(dados[0]);
                Medico m = hospital.buscarMedicoPorCrm(dados[1]);
                if (p != null && m != null) {
                    LocalDateTime dataHora = LocalDateTime.parse(dados[2], FORMATTER);
                    hospital.agendarConsultaSemSalvar(p, m, dataHora, dados[3]);
                    if (dados.length > 4) {
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