package com.imunidata.service;

import com.imunidata.model.RegistroVacinacao;
import com.imunidata.repository.RegistroVacinacaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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

    private static final DateTimeFormatter FMT_BR  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── CRUD básico ──────────────────────────────────────────────────────────
    public List<RegistroVacinacao> obterTodos()              { return repository.findAll(); }
    public Optional<RegistroVacinacao> obterPorId(Long id)  { return repository.findById(id); }
    public RegistroVacinacao criar(RegistroVacinacao r)      { return repository.save(r); }

    public void deletar(Long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("ID não encontrado: " + id);
        repository.deleteById(id);
    }

    public RegistroVacinacao atualizar(Long id, RegistroVacinacao r) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("ID não encontrado: " + id);
        r.setId(id);
        return repository.save(r);
    }

    // ── Buscas ───────────────────────────────────────────────────────────────
    public List<RegistroVacinacao> buscarPorVacina(String v)             { return repository.findByVacina(v); }
    public List<RegistroVacinacao> buscarPorEstado(String e)             { return repository.findByEstado(e); }
    public List<RegistroVacinacao> buscarPorMunicipio(String m)          { return repository.findByMunicipio(m); }
    public List<RegistroVacinacao> buscarPorIdade(String i)              { return repository.findByIdade(i); }
    public List<RegistroVacinacao> buscarPorEstadoEVacina(String e, String v) { return repository.findByEstadoAndVacina(e, v); }
    public List<RegistroVacinacao> obterResumosPorEstado()               { return repository.findAllOrderByEstadoAndVacina(); }

    public List<RegistroVacinacao> buscarPorData(LocalDate data) {
        return repository.findByDataRegistro(data);
    }

    public List<RegistroVacinacao> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repository.findByDataRegistroBetween(inicio, fim);
    }

    public List<RegistroVacinacao> buscarPorPeriodoEEstado(LocalDate inicio, LocalDate fim, String estado) {
        if (estado == null || estado.isBlank()) return buscarPorPeriodo(inicio, fim);
        return repository.findByDataRegistroBetweenAndEstado(inicio, fim, estado);
    }

    // ── Importação CSV ────────────────────────────────────────────────────────
    public List<RegistroVacinacao> carregarDadosCSV(InputStream csvFile) {
        List<RegistroVacinacao> registros = new ArrayList<>();
        try {
            byte[] bytes = csvFile.readAllBytes();
            String conteudo = new String(bytes, StandardCharsets.UTF_8)
                    .replace("\r\n", "\n").replace("\r", "\n");

            String[] linhas = conteudo.split("\n");
            if (linhas.length < 2) { log.warn("CSV vazio ou sem dados"); return registros; }

            String cabecalho = linhas[0].toLowerCase();
            char sep = cabecalho.contains(";") ? ';' : ',';
            boolean isDatasus = cabecalho.contains("sigla_uf_paciente")
                    || cabecalho.contains("descricao_vacina")
                    || cabecalho.contains("nome_municipio_paciente");

            log.info("CSV — separador:'{}', formato:{}, linhas:{}", sep, isDatasus ? "Datasus" : "simples", linhas.length);

            return isDatasus
                    ? importarFormatoDatasus(linhas, sep)
                    : importarFormatoSimples(linhas, sep);

        } catch (IOException e) {
            log.error("Erro ao ler CSV: {}", e.getMessage());
            throw new RuntimeException("Erro ao ler arquivo CSV", e);
        }
    }

    private List<RegistroVacinacao> importarFormatoSimples(String[] linhas, char sep) {
        List<RegistroVacinacao> lista = new ArrayList<>();
        int ok = 0, erros = 0;
        for (int i = 1; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if (linha.isEmpty()) continue;
            String[] cols = splitLinha(linha, sep);
            if (cols.length < 6) { erros++; continue; }
            try {
                lista.add(RegistroVacinacao.builder()
                        .municipio(cols[0].trim())
                        .estado(cols[1].trim())
                        .vacina(cols[2].trim())
                        .dose(cols[3].trim())
                        .quantidadeAplicada(Integer.parseInt(cols[4].trim()))
                        .dataRegistro(parsearData(cols[5].trim()))
                        .idade(cols.length > 6 ? cols[6].trim() : "")
                        .build());
                ok++;
            } catch (Exception e) { log.warn("Linha {}: {}", i + 1, e.getMessage()); erros++; }
        }
        log.info("Simples — OK:{}, erros:{}", ok, erros);
        return repository.saveAll(lista);
    }

    private List<RegistroVacinacao> importarFormatoDatasus(String[] linhas, char sep) {
        List<RegistroVacinacao> lista = new ArrayList<>();
        String[] header = splitLinha(linhas[0], sep);
        int idxEstado    = indexOf(header, "sigla_uf_paciente");
        int idxMunicipio = indexOf(header, "nome_municipio_paciente");
        int idxVacina    = indexOf(header, "descricao_vacina");
        int idxDose      = indexOf(header, "descricao_dose_vacina");
        int idxIdade     = indexOf(header, "numero_idade_paciente");
        int idxData      = indexOf(header, "data_vacina");
        int ok = 0, erros = 0;
        for (int i = 1; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if (linha.isEmpty()) continue;
            String[] cols = splitLinha(linha, sep);
            try {
                String estado = get(cols, idxEstado);
                String municipio = get(cols, idxMunicipio);
                String vacina = get(cols, idxVacina);
                if (estado.isEmpty() || municipio.isEmpty() || vacina.isEmpty()) { erros++; continue; }
                String dataStr = get(cols, idxData);
                String dataLimpa = dataStr.length() >= 10 ? dataStr.substring(0, 10) : dataStr;
                lista.add(RegistroVacinacao.builder()
                        .municipio(municipio).estado(estado).vacina(vacina)
                        .dose(get(cols, idxDose).isEmpty() ? "—" : get(cols, idxDose))
                        .quantidadeAplicada(1)
                        .dataRegistro(parsearData(dataLimpa))
                        .idade(get(cols, idxIdade))
                        .build());
                ok++;
            } catch (Exception e) { log.warn("Linha Datasus {}: {}", i + 1, e.getMessage()); erros++; }
        }
        log.info("Datasus — OK:{}, erros:{}", ok, erros);
        return repository.saveAll(lista);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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
        try { return LocalDate.parse(s, FMT_ISO); } catch (Exception e2) { return LocalDate.now(); }}
    }
}
