package com.imunidata.config;

import com.imunidata.service.RegistroVacinacaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@Slf4j
public class DataLoaderConfig {

    @Bean
    public CommandLineRunner loadData(RegistroVacinacaoService service, ResourceLoader resourceLoader) {
        return args -> {
            log.info("=== Iniciando carga de dados iniciais ===");
            try {
                var resource = resourceLoader.getResource("classpath:dados_vacinacao.csv");
                if (resource.exists()) {
                    long inicio = System.currentTimeMillis();
                    log.info("Carregando dados do arquivo CSV: dados_vacinacao.csv");
                    var registros = service.carregarDadosCSV(resource.getInputStream());
                    long duracao = System.currentTimeMillis() - inicio;
                    log.info("CSV carregado com sucesso: {} registros em {} ms", registros.size(), duracao);
                } else {
                    log.warn("Arquivo dados_vacinacao.csv não encontrado no classpath. " +
                             "O sistema iniciará sem dados CSV pré-carregados.");
                }
            } catch (Exception e) {
                // Logar mas NÃO relançar: o boot deve continuar mesmo sem dados iniciais
                log.error("Falha ao carregar dados iniciais do CSV (a aplicação continuará): {}",
                          e.getMessage(), e);
            }
            log.info("=== Carga de dados iniciais concluída ===");
        };
    }
}
