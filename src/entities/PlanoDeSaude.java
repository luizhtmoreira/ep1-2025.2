package entities;

import java.util.HashMap;
import java.util.Map;

public class PlanoDeSaude {
    
    private String nome;
    // A chave é o objeto Especialidade, o valor é o desconto (ex: 0.2 para 20%)
    private Map<Especialidade, Double> descontosPorEspecialidade;

    public PlanoDeSaude(String nome) {
        this.nome = nome;
        this.descontosPorEspecialidade = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Método para adicionar ou atualizar um desconto
    public void adicionarDesconto(Especialidade especialidade, double desconto) {
        // Garantimos que o desconto esteja entre 0 e 1 (0% e 100%)
        if (desconto >= 0 && desconto <= 1) {
            this.descontosPorEspecialidade.put(especialidade, desconto);
        } else {
            System.err.println("Erro: Desconto inválido. Deve ser um valor entre 0.0 e 1.0.");
        }
    }

    // Método para obter o desconto para uma dada especialidade
    public double getDesconto(Especialidade especialidade) {
        // Se não houver um desconto específico, o padrão é 0.
        return this.descontosPorEspecialidade.getOrDefault(especialidade, 0.0);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}