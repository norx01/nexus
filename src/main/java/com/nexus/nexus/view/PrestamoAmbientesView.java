package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Ambientes;
import com.nexus.nexus.model.Personal;
import com.nexus.nexus.model.PrestamoAmbientes;
import com.nexus.nexus.repository.AmbientesRepository;
import com.nexus.nexus.repository.LlavesRepository;
import com.nexus.nexus.repository.PersonalRepository;
import com.nexus.nexus.repository.PrestamoAmbientesRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PrestamoAmbientesView {

    @Autowired
    private PrestamoAmbientesRepository prestamoAmbientesRepository;

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private AmbientesRepository ambientesRepository;

    @Autowired
    private LlavesRepository llavesRepository;

    private void agregarMapas(Model model) {
        Map<Integer, String> personalMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));
        Map<Integer, String> ambientesMap = ambientesRepository.findAll().stream()
                .collect(Collectors.toMap(Ambientes::getId_ambiente, Ambientes::getNombre));
        model.addAttribute("personalMap", personalMap);
        model.addAttribute("ambientesMap", ambientesMap);
    }

    @GetMapping("/view/prestamoAmbientes")
    public String lista(Model model) {
        var todos = prestamoAmbientesRepository.findAll();
        var activos = todos.stream().filter(p -> "Activo".equalsIgnoreCase(p.getEstado())).toList();
        var historial = todos.stream().filter(p -> !"Activo".equalsIgnoreCase(p.getEstado()))
                .sorted((a, b) -> {
                    if (a.getFecha() == null) return 1;
                    if (b.getFecha() == null) return -1;
                    return b.getFecha().compareTo(a.getFecha());
                }).toList();
        model.addAttribute("activos", activos);
        model.addAttribute("historial", historial);
        agregarMapas(model);
        return "prestamoAmbientes/prestamoAmbientes";
    }

    @GetMapping("/view/prestamoAmbientes/form")
    public String form(Model model) {
        model.addAttribute("prestamo", new PrestamoAmbientes());
        model.addAttribute("personal", personalRepository.findAll());
        model.addAttribute("ambientes", ambientesRepository.findAll());
        return "prestamoAmbientes/prestamoAmbientesForm";
    }

    @PostMapping("/view/prestamoAmbientes/save")
    public String save(@ModelAttribute PrestamoAmbientes prestamo, RedirectAttributes ra) {
        prestamoAmbientesRepository.save(prestamo);
        ra.addFlashAttribute("success", "Préstamo registrado exitosamente");
        return "redirect:/view/prestamoAmbientes";
    }

    @GetMapping("/view/prestamoAmbientes/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("prestamo", prestamoAmbientesRepository.findById(id).orElse(null));
        model.addAttribute("personal", personalRepository.findAll());
        model.addAttribute("ambientes", ambientesRepository.findAll());
        return "prestamoAmbientes/prestamoAmbientesForm";
    }

    @PostMapping("/view/prestamoAmbientes/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        prestamoAmbientesRepository.deleteById(id);
        ra.addFlashAttribute("success", "Préstamo eliminado exitosamente");
        return "redirect:/view/prestamoAmbientes";
    }

    @GetMapping("/view/prestamoAmbientes/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=prestamoAmbientes.pdf");

        List<PrestamoAmbientes> lista = prestamoAmbientesRepository.findAll();
        Map<Integer, String> personalMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));
        Map<Integer, String> ambientesMap = ambientesRepository.findAll().stream()
                .collect(Collectors.toMap(Ambientes::getId_ambiente, Ambientes::getNombre));

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Reporte de Préstamos de Ambientes"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID");
        table.addCell("Instructor");
        table.addCell("Ambiente");
        table.addCell("Fecha");
        table.addCell("Hora Entrada");
        table.addCell("Hora Salida");

        for (PrestamoAmbientes p : lista) {
            table.addCell(p.getId_prestamo().toString());
            table.addCell(personalMap.getOrDefault(p.getId_personal(), "ID:" + p.getId_personal()));
            table.addCell(ambientesMap.getOrDefault(p.getId_ambiente(), "ID:" + p.getId_ambiente()));
            table.addCell(p.getFecha() != null ? p.getFecha().toString() : "N/A");
            table.addCell(p.getHora_entrada() != null ? p.getHora_entrada().toString() : "N/A");
            table.addCell(p.getHora_salida() != null ? p.getHora_salida().toString() : "En curso");
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/prestamoAmbientes/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=prestamoAmbientes.xlsx");

        List<PrestamoAmbientes> lista = prestamoAmbientesRepository.findAll();
        Map<Integer, String> pMap = personalRepository.findAll().stream()
                .collect(Collectors.toMap(Personal::getId_personal, Personal::getNombre));
        Map<Integer, String> aMap = ambientesRepository.findAll().stream()
                .collect(Collectors.toMap(Ambientes::getId_ambiente, Ambientes::getNombre));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Préstamos Ambientes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Instructor");
        header.createCell(2).setCellValue("Ambiente");
        header.createCell(3).setCellValue("Fecha");
        header.createCell(4).setCellValue("Hora Entrada");
        header.createCell(5).setCellValue("Hora Salida");
        header.createCell(6).setCellValue("Estado");

        int rowNum = 1;
        for (PrestamoAmbientes p : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId_prestamo().toString());
            row.createCell(1).setCellValue(pMap.getOrDefault(p.getId_personal(), "ID:" + p.getId_personal()));
            row.createCell(2).setCellValue(aMap.getOrDefault(p.getId_ambiente(), "ID:" + p.getId_ambiente()));
            row.createCell(3).setCellValue(p.getFecha() != null ? p.getFecha().toString() : "N/A");
            row.createCell(4).setCellValue(p.getHora_entrada() != null ? p.getHora_entrada().toString() : "N/A");
            row.createCell(5).setCellValue(p.getHora_salida() != null ? p.getHora_salida().toString() : "En curso");
            row.createCell(6).setCellValue(p.getEstado() != null ? p.getEstado() : "Activo");
        }

        for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
