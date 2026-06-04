package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.PrestamoAmbientes;
import com.nexus.nexus.repository.AmbientesRepository;
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

@Controller
public class PrestamoAmbientesView {

    @Autowired
    private PrestamoAmbientesRepository prestamoAmbientesRepository;

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private AmbientesRepository ambientesRepository;

    @GetMapping("/view/prestamoAmbientes")
    public String lista(Model model) {
        model.addAttribute("prestamos", prestamoAmbientesRepository.findAll());
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
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Préstamo de Ambientes"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID Préstamo");
        table.addCell("ID Personal");
        table.addCell("ID Ambiente");
        table.addCell("Fecha");
        table.addCell("Hora Entrada");
        table.addCell("Hora Salida");

        for (PrestamoAmbientes p : lista) {
            table.addCell(p.getId_prestamo().toString());
            table.addCell(p.getId_personal().toString());
            table.addCell(p.getId_ambiente().toString());
            table.addCell(p.getFecha() != null ? p.getFecha().toString() : "N/A");
            table.addCell(p.getHora_entrada() != null ? p.getHora_entrada().toString() : "N/A");
            table.addCell(p.getHora_salida() != null ? p.getHora_salida().toString() : "N/A");
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/prestamoAmbientes/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=prestamoAmbientes.xlsx");

        List<PrestamoAmbientes> lista = prestamoAmbientesRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("PrestamoAmbientes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Préstamo");
        header.createCell(1).setCellValue("ID Personal");
        header.createCell(2).setCellValue("ID Ambiente");
        header.createCell(3).setCellValue("Fecha");
        header.createCell(4).setCellValue("Hora Entrada");
        header.createCell(5).setCellValue("Hora Salida");

        int rowNum = 1;
        for (PrestamoAmbientes p : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId_prestamo().toString());
            row.createCell(1).setCellValue(p.getId_personal().toString());
            row.createCell(2).setCellValue(p.getId_ambiente().toString());
            row.createCell(3).setCellValue(p.getFecha() != null ? p.getFecha().toString() : "N/A");
            row.createCell(4).setCellValue(p.getHora_entrada() != null ? p.getHora_entrada().toString() : "N/A");
            row.createCell(5).setCellValue(p.getHora_salida() != null ? p.getHora_salida().toString() : "N/A");
        }

        for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
