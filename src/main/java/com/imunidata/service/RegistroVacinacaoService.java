package com.imunidata.service;

import com.imunidata.model.RegistroVacinacao;
import com.imunidata.repository.RegistroVacinacaoRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RegistroVacinacaoService {

    private final RegistroVacinacaoRepository repository;

    // Formatos de data suportados
    private static final DateTimeFormatter FMT_BR    = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO   = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<RegistroVacinacao> obterTodos()                          { return repository.findAll(); }
    public Optional<RegistroVacinacao> obterPorId(Long id)               { return repository.findById(id); }
    public RegistroVacinacao criar(RegistroVacinacao r)                   { return repository.save(r); }
    public void deletar(Long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("ID não encontrado: " + id);
        repository.deleteById(id);
    }
    public RegistroVacinacao atualizar(Long id, RegistroVacinacao r) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("ID não encontrado: " + id);
        r.setId(id);
        return repository.save(r);
    }

    public List<RegistroVacinacao> buscarPorVacina(String v)             { return repository.findByVacina(v); }
    public List<RegistroVacinacao> buscarPorEstado(String e)             { return repository.findByEstado(e); }
    public List<RegistroVacinacao> buscarPorMunicipio(String m)          { return repository.findByMunicipio(m); }
    public List<RegistroVacinacao> buscarPorIdade(String i)              { return repository.findByIdade(i); }
    public List<RegistroVacinacao> buscarPorEstadoEVacina(String e,String v) { return repository.findByEstadoAndVacina(e,v); }
    public List<RegistroVacinacao> obterResumosPorEstado()               { return repository.findAllOrderByEstadoAndVacina(); }

    /**
     * Importa CSV com detecção automática de separador (vírgula ou ponto-e-vírgula)
     * e detecção automática de cabeçalho.
     *
     * Suporta dois formatos:
     *   1. Formato simples:   municipio,estado,vacina,dose,quantidadeAplicada,dataRegistro,idade
     *   2. Formato Datasus:   campos com ponto-e-vírgula (exenploDOCSV.csv)
     */
    public List<RegistroVacinacao> carregarDadosCSV(InputStream csvFile) {
        List<RegistroVacinacao> registros = new ArrayList<>();

        try {
            // Ler tudo em memória para poder inspecionar o cabeçalho
            byte[] bytes = csvFile.readAllBytes();
            String conteudo = new String(bytes, StandardCharsets.UTF_8)
                    .replace("\r\n", "\n").replace("\r", "\n");

            String[] linhasTexto = conteudo.split("\n");
            if (linhasTexto.length < 2) {
                log.warn("CSV vazio ou sem dados");
                return registros;
            }

            String cabecalho = linhasTexto[0].toLowerCase();

            // Detectar separador
            char separador = cabecalho.contains(";") ? ';' : ',';
            log.info("CSV detectado — separador: '{}', linhas: {}", separador, linhasTexto.length);

            // Detectar se é formato Datasus (tem colunas como sigla_uf_paciente)
            boolean isFormatoDatasus = cabecalho.contains("sigla_uf_paciente")
                    || cabecalho.contains("descricao_vacina")
                    || cabecalho.contains("nome_municipio_paciente");

            if (isFormatoDatasus) {
                return importarFormatoDatasus(linhasTexto, separador);
            } else {
                return importarFormatoSimples(linhasTexto, separador);
            }

        } catch (IOException e) {
            log.error("Erro ao ler CSV: {}", e.getMessage());
            throw new RuntimeException("Erro ao ler arquivo CSV", e);
        }
    }

    // -----------------------------------------------------------------------
    // Formato simples: municipio,estado,vacina,dose,qtd,data,idade
    // -----------------------------------------------------------------------
    private List<RegistroVacinacao> importarFormatoSimples(String[] linhas, char sep) {
        List<RegistroVacinacao> registros = new ArrayList<>();
        int ok = 0, erros = 0;

        for (int i = 1; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if (linha.isEmpty()) continue;

            String[] cols = linha.split(String.valueOf(sep), -1);
            // Remover aspas extras
            for (int c = 0; c < cols.length; c++) cols[c] = desquote(cols[c]);

            if (cols.length < 6) {
                log.warn("Linha {} ignorada (colunas insuficientes: {})", i + 1, cols.length);
                erros++;
                continue;
            }

            try {
                LocalDate data = parsearData(cols[5].trim());
                String idade = cols.length > 6 ? cols[6].trim() : "";

                RegistroVacinacao r = RegistroVacinacao.builder()
                        .municipio(cols[0].trim())
                        .estado(cols[1].trim())
                        .vacina(cols[2].trim())
                        .dose(cols[3].trim())
                        .quantidadeAplicada(Integer.parseInt(cols[4].trim()))
                        .dataRegistro(data)
                        .idade(idade)
                        .build();

                registros.add(r);
                ok++;
            } catch (Exception e) {
                log.warn("Linha {} com erro: {}", i + 1, e.getMessage());
                erros++;
            }
        }

        log.info("Formato simples — OK: {}, erros: {}", ok, erros);
        return repository.saveAll(registros);
    }

    // -----------------------------------------------------------------------
    // Formato Datasus: CSV com ponto-e-vírgula, campos do Datasus
    // Mapeia: sigla_uf_paciente→estado, nome_municipio_paciente→municipio,
    //         descricao_vacina→vacina, descricao_dose_vacina→dose,
    //         numero_idade_paciente→idade, data_vacina→dataRegistro
    // -----------------------------------------------------------------------
    private List<RegistroVacinacao> importarFormatoDatasus(String[] linhas, char sep) {
        List<RegistroVacinacao> registros = new ArrayList<>();

        // Mapear índices pelo cabeçalho
        String[] header = splitLinha(linhas[0], sep);
        int idxEstado    = indexOf(header, "sigla_uf_paciente");
        int idxMunicipio = indexOf(header, "nome_municipio_paciente");
        int idxVacina    = indexOf(header, "descricao_vacina");
        int idxDose      = indexOf(header, "descricao_dose_vacina");
        int idxIdade     = indexOf(header, "numero_idade_paciente");
        int idxData      = indexOf(header, "data_vacina");

        log.info("Formato Datasus — colunas: estado={} municipio={} vacina={} dose={} idade={} data={}",
                 idxEstado, idxMunicipio, idxVacina, idxDose, idxIdade, idxData);

        int ok = 0, erros = 0;

        for (int i = 1; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if (linha.isEmpty()) continue;

            String[] cols = splitLinha(linha, sep);

            try {
                String estado    = get(cols, idxEstado);
                String municipio = get(cols, idxMunicipio);
                String vacina    = get(cols, idxVacina);
                String dose      = get(cols, idxDose);
                String idadeStr  = get(cols, idxIdade);
                String dataStr   = get(cols, idxData);

                if (estado.isEmpty() || municipio.isEmpty() || vacina.isEmpty()) {
                    erros++;
                    continue;
                }

                // data_vacina vem como "2026-01-08 00:00:00-03"
                String dataLimpa = dataStr.length() >= 10 ? dataStr.substring(0, 10) : dataStr;
                LocalDate data   = parsearData(dataLimpa);

                RegistroVacinacao r = RegistroVacinacao.builder()
                        .municipio(municipio)
                        .estado(estado)
                        .vacina(vacina)
                        .dose(dose.isEmpty() ? "—" : dose)
                        .quantidadeAplicada(1)   // Datasus não agrega qtd por linha
                        .dataRegistro(data)
                        .idade(idadeStr)
                        .build();

                registros.add(r);
                ok++;
            } catch (Exception e) {
                log.warn("Linha Datasus {} com erro: {}", i + 1, e.getMessage());
                erros++;
            }
        }

        log.info("Formato Datasus — OK: {}, erros: {}", ok, erros);
        return repository.saveAll(registros);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String[] splitLinha(String linha, char sep) {
        String[] cols = linha.split(String.valueOf(sep), -1);
        for (int i = 0; i < cols.length; i++) cols[i] = desquote(cols[i]);
        return cols;
    }

    private String desquote(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2)
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        return s.trim();
    }

    private String get(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return "";
        return cols[idx] == null ? "" : cols[idx].trim();
    }

    private int indexOf(String[] header, String campo) {
        for (int i = 0; i < header.length; i++)
            if (desquote(header[i]).equalsIgnoreCase(campo)) return i;
        return -1;
    }

    private LocalDate parsearData(String s) {
        s = s.trim();
        if (s.isEmpty()) return LocalDate.now();
        try { return LocalDate.parse(s, FMT_BR); }  catch (Exception e1) {
        try { return LocalDate.parse(s, FMT_ISO); } catch (Exception e2) {
            return LocalDate.now();
        }}
    }
}
