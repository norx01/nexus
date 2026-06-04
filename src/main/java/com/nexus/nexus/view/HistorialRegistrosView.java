package com.nexus.nexus.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Personal;
import com.nexus.nexus.model.RegistroEntradaPersonal;
import com.nexus.nexus.repository.PersonalRepository;
import com.nexus.nexus.repository.RegistroEntradaPersonalRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HistorialRegistrosView {

    @Autowired
    private RegistroEntradaPersonalRepository registroRepository;

    @Autowired
    private PersonalRepository personalRepository;

    @GetMapping("/view/historial")
    public String historial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        if (desde == null) desde = LocalDate.now().withDayOfMonth(1); // primer día del mes
        if (hasta == null) hasta = LocalDate.now();

        LocalDate desdeF = desde;
        LocalDate hastaF = hasta;

        List<RegistroEntradaPersonal> registros = registroRepository.findAll().stream()
                .filter(r -> r.getFecha() != null
                        && !r.getFecha().isBefore(desdeF)
                        && !r.getFecha().isAfter(hastaF))
                .sorted((a, b) -> {
                    int cmp = b.getFecha().compareTo(a.getFecha());
                    if (cmp != 0) return cmp;
                    if (a.getHora_entrada() == null) return 1;
                    if (b.getHora_entrada() == null) return -1;
                    return b.getHora_entrada().compareTo(a.getHora_entrada());
                })
                .toList();

        Map<Integer, String> personalMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));

        long totalEntradas = registros.size();
        long dentroAhora = registros.stream().filter(r -> "Dentro".equalsIgnoreCase(r.getEstado())).count();

        model.addAttribute("registros", registros);
        model.addAttribute("personalMap", personalMap);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("totalEntradas", totalEntradas);
        model.addAttribute("dentroAhora", dentroAhora);

        return "historial/historial";
    }

    @GetMapping("/view/historial/pdf")
    public void exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletResponse response) throws Exception {

        if (desde == null) desde = LocalDate.now().withDayOfMonth(1);
        if (hasta == null) hasta = LocalDate.now();

        LocalDate desdeF = desde;
        LocalDate hastaF = hasta;

        List<RegistroEntradaPersonal> registros = registroRepository.findAll().stream()
                .filter(r -> r.getFecha() != null
                        && !r.getFecha().isBefore(desdeF)
                        && !r.getFecha().isAfter(hastaF))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .toList();

        Map<Integer, String> personalMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=historial_" + desde + "_" + hasta + ".pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
        Paragraph titulo = new Paragraph("Reporte de Registro de Entrada/Salida - Instructores", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        com.itextpdf.text.Font subtitleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
        Paragraph periodo = new Paragraph("Período: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " al " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subtitleFont);
        periodo.setAlignment(Element.ALIGN_CENTER);
        periodo.setSpacingAfter(10);
        document.add(periodo);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1f, 3.5f, 1.5f, 1.5f, 1.5f, 1.5f});

        // Cabeceras
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD,
                BaseColor.WHITE);
        String[] headers = {"#", "Instructor", "Fecha", "Hora Entrada", "Hora Salida", "Estado"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(22, 101, 52));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Filas
        com.itextpdf.text.Font rowFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
        int num = 1;
        for (RegistroEntradaPersonal r : registros) {
            table.addCell(new Phrase(String.valueOf(num++), rowFont));
            table.addCell(new Phrase(personalMap.getOrDefault(r.getId_personal(), "ID:" + r.getId_personal()), rowFont));
            table.addCell(new Phrase(r.getFecha() != null ? r.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "", rowFont));
            table.addCell(new Phrase(r.getHora_entrada() != null ? r.getHora_entrada().toString() : "", rowFont));
            table.addCell(new Phrase(r.getHora_salida() != null ? r.getHora_salida().toString() : "—", rowFont));

            PdfPCell estadoCell = new PdfPCell(new Phrase(r.getEstado() != null ? r.getEstado() : "", rowFont));
            estadoCell.setBackgroundColor("Dentro".equalsIgnoreCase(r.getEstado())
                    ? new BaseColor(220, 252, 231) : new BaseColor(243, 244, 246));
            table.addCell(estadoCell);
        }

        document.add(table);

        Paragraph total = new Paragraph("\nTotal registros: " + registros.size(), subtitleFont);
        document.add(total);

        document.close();
    }

    @GetMapping("/view/historial/excel")
    public void exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletResponse response) throws Exception {

        if (desde == null) desde = LocalDate.now().withDayOfMonth(1);
        if (hasta == null) hasta = LocalDate.now();

        LocalDate desdeF = desde;
        LocalDate hastaF = hasta;

        List<RegistroEntradaPersonal> registros = registroRepository.findAll().stream()
                .filter(r -> r.getFecha() != null
                        && !r.getFecha().isBefore(desdeF)
                        && !r.getFecha().isAfter(hastaF))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .toList();

        Map<Integer, String> personalMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=historial_" + desde + "_" + hasta + ".xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historial");

        // Estilo de cabecera
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] cols = {"#", "Instructor", "Fecha", "Hora Entrada", "Hora Salida", "Estado"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (RegistroEntradaPersonal r : registros) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(personalMap.getOrDefault(r.getId_personal(), "ID:" + r.getId_personal()));
            row.createCell(2).setCellValue(r.getFecha() != null ? r.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            row.createCell(3).setCellValue(r.getHora_entrada() != null ? r.getHora_entrada().toString() : "");
            row.createCell(4).setCellValue(r.getHora_salida() != null ? r.getHora_salida().toString() : "—");
            row.createCell(5).setCellValue(r.getEstado() != null ? r.getEstado() : "");
            rowNum++;
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
