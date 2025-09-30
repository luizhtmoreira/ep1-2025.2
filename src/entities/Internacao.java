package entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Internacao {
    
    private Paciente paciente;
    private Medico medicoResponsavel;
    private Quarto quarto;
    private LocalDate dataEntrada;
    private LocalDate dataSaida; // Será null enquanto o paciente estiver internado

    public Internacao(Paciente paciente, Medico medicoResponsavel, Quarto quarto, LocalDate dataEntrada) {
        this.paciente = paciente;
        this.medicoResponsavel = medicoResponsavel;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataSaida = null; // A data de saída é nula na criação
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedicoResponsavel() {
        return medicoResponsavel;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    // Método de ação para registrar a alta do paciente
    public void registrarAlta(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
        this.quarto.desocupar(); // Importante: ao dar alta, o quarto fica livre!
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String saidaFormatada = (dataSaida == null) ? "INTERNADO" : dataSaida.format(formatter);
        
        return "Internacao [" +
               "Paciente: " + paciente.getNome() +
               ", Medico: " + medicoResponsavel.getNome() +
               ", Quarto: " + quarto.getNumero() +
               ", Entrada: " + dataEntrada.format(formatter) +
               ", Saida: " + saidaFormatada +
               "]";
    }
}