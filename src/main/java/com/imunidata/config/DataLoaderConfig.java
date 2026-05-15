package com.imunidata.config;

import com.imunidata.service.RegistroVacinacaoService;
import lombok.AllArgsConstructor;
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
            try {
                long inicio = System.currentTimeMillis();
                var resource = resourceLoader.getResource("classpath:dados_vacinacao.csv");
                
                if (resource.exists()) {
                    log.info("Carregando dados do arquivo CSV...");
                    service.carregarDadosCSV(resource.getInputStream());
                    long duracao = System.currentTimeMillis() - inicio;
                    log.info("Dados carregados com sucesso em {} ms", duracao);
                } else {
                    log.warn("Arquivo dados_vacinacao.csv não encontrado");
                }
            } catch (Exception e) {
                log.error("Erro ao carregar dados iniciais: {}", e.getMessage(), e);
            }
        };
    }
}
