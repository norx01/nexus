package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Ambientes;
import com.nexus.nexus.repository.AmbientesRepository;
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
public class AmbientesView {

    @Autowired
    private AmbientesRepository ambientesRepository;

    @GetMapping("/view/ambientes")
    public String lista(Model model) {
        model.addAttribute("ambientes", ambientesRepository.findAll());
        return "ambientes/ambientes";
    }

    @GetMapping("/view/ambientes/form")
    public String form(Model model) {
        model.addAttribute("ambiente", new Ambientes());
        return "ambientes/ambientesForm";
    }

    @PostMapping("/view/ambientes/save")
    public String save(@ModelAttribute Ambientes ambiente, RedirectAttributes ra) {
        ambientesRepository.save(ambiente);
        ra.addFlashAttribute("success", "Ambiente registrado exitosamente");
        return "redirect:/view/ambientes";
    }

    @GetMapping("/view/ambientes/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("ambiente", ambientesRepository.findById(id).orElse(null));
        return "ambientes/ambientesForm";
    }

    @PostMapping("/view/ambientes/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        ambientesRepository.deleteById(id);
        ra.addFlashAttribute("success", "Ambiente eliminado exitosamente");
        return "redirect:/view/ambientes";
    }

    @GetMapping("/view/ambientes/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ambientes.pdf");

        List<Ambientes> lista = ambientesRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Ambientes"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID Ambiente");
        table.addCell("Nombre");
        table.addCell("Sede");
        table.addCell("Estado");
        table.addCell("Observaciones");

        for (Ambientes a : lista) {
            table.addCell(a.getId_ambiente().toString());
            table.addCell(a.getNombre());
            table.addCell(a.getSede());
            table.addCell(a.getEstado());
            table.addCell(a.getObservaciones() != null ? a.getObservaciones() : "");
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/ambientes/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ambientes.xlsx");

        List<Ambientes> lista = ambientesRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ambientes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Ambiente");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Sede");
        header.createCell(3).setCellValue("Estado");
        header.createCell(4).setCellValue("Observaciones");

        int rowNum = 1;
        for (Ambientes a : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getId_ambiente().toString());
            row.createCell(1).setCellValue(a.getNombre());
            row.createCell(2).setCellValue(a.getSede());
            row.createCell(3).setCellValue(a.getEstado());
            row.createCell(4).setCellValue(a.getObservaciones() != null ? a.getObservaciones() : "");
        }

        for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
