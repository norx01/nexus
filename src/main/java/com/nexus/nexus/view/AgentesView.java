package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Agentes;
import com.nexus.nexus.repository.AgentesRepository;
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
public class AgentesView {

    @Autowired
    private AgentesRepository agentesRepository;

    @GetMapping("/view/agentes")
    public String lista(Model model) {
        model.addAttribute("agentes", agentesRepository.findAll());
        return "agentes/agentes";
    }

    @GetMapping("/view/agentes/form")
    public String form(Model model) {
        model.addAttribute("agente", new Agentes());
        return "agentes/agentesForm";
    }

    @PostMapping("/view/agentes/save")
    public String save(@ModelAttribute Agentes agente, RedirectAttributes ra) {
        agentesRepository.save(agente);
        ra.addFlashAttribute("success", "Agente registrado exitosamente");
        return "redirect:/view/agentes";
    }

    @GetMapping("/view/agentes/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("agente", agentesRepository.findById(id).orElse(null));
        return "agentes/agentesForm";
    }

    @PostMapping("/view/agentes/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        agentesRepository.deleteById(id);
        ra.addFlashAttribute("success", "Agente eliminado exitosamente");
        return "redirect:/view/agentes";
    }

    @GetMapping("/view/agentes/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=agentes.pdf");

        List<Agentes> lista = agentesRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Agentes"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID Agente");
        table.addCell("Nombre del Agente");
        table.addCell("Correo");

        for (Agentes a : lista) {
            table.addCell(a.getId_agente().toString());
            table.addCell(a.getNombre_del_agente());
            table.addCell(a.getCorreo());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/agentes/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=agentes.xlsx");

        List<Agentes> lista = agentesRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Agentes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Agente");
        header.createCell(1).setCellValue("Nombre del Agente");
        header.createCell(2).setCellValue("Correo");

        int rowNum = 1;
        for (Agentes a : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getId_agente().toString());
            row.createCell(1).setCellValue(a.getNombre_del_agente());
            row.createCell(2).setCellValue(a.getCorreo());
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
