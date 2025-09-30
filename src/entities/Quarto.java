package entities;

public class Quarto {
    
    private int numero;
    private boolean ocupado;

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false; // Todo quarto começa desocupado
    }

    public int getNumero() {
        return numero;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    // Métodos de ação para controlar o status
    public void ocupar() {
        this.ocupado = true;
    }

    public void desocupar() {
        this.ocupado = false;
    }

    @Override
    public String toString() {
        return "Quarto [Número: " + numero + ", Status: " + (ocupado ? "Ocupado" : "Livre") + "]";
    }
}