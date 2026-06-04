package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Llaves;
import com.nexus.nexus.repository.AmbientesRepository;
import com.nexus.nexus.repository.LlavesRepository;
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
public class LlavesView {

    @Autowired
    private LlavesRepository llavesRepository;

    @Autowired
    private AmbientesRepository ambientesRepository;

    @GetMapping("/view/llaves")
    public String lista(Model model) {
        model.addAttribute("llaves", llavesRepository.findAll());
        return "llaves/llaves";
    }

    @GetMapping("/view/llaves/form")
    public String form(Model model) {
        model.addAttribute("llave", new Llaves());
        model.addAttribute("ambientes", ambientesRepository.findAll());
        return "llaves/llavesForm";
    }

    @PostMapping("/view/llaves/save")
    public String save(@ModelAttribute Llaves llave, RedirectAttributes ra) {
        llavesRepository.save(llave);
        ra.addFlashAttribute("success", "Llave registrada exitosamente");
        return "redirect:/view/llaves";
    }

    @GetMapping("/view/llaves/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("llave", llavesRepository.findById(id).orElse(null));
        model.addAttribute("ambientes", ambientesRepository.findAll());
        return "llaves/llavesForm";
    }

    @PostMapping("/view/llaves/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        llavesRepository.deleteById(id);
        ra.addFlashAttribute("success", "Llave eliminada exitosamente");
        return "redirect:/view/llaves";
    }

    @GetMapping("/view/llaves/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=llaves.pdf");

        List<Llaves> lista = llavesRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Llaves"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID Llave");
        table.addCell("ID Ambiente");
        table.addCell("Nombre");
        table.addCell("Estado");

        for (Llaves l : lista) {
            table.addCell(l.getId_llave().toString());
            table.addCell(l.getId_ambiente() != null ? l.getId_ambiente().toString() : "N/A");
            table.addCell(l.getNombre());
            table.addCell(l.getEstado());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/llaves/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=llaves.xlsx");

        List<Llaves> lista = llavesRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Llaves");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Llave");
        header.createCell(1).setCellValue("ID Ambiente");
        header.createCell(2).setCellValue("Nombre");
        header.createCell(3).setCellValue("Estado");

        int rowNum = 1;
        for (Llaves l : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(l.getId_llave().toString());
            row.createCell(1).setCellValue(l.getId_ambiente() != null ? l.getId_ambiente().toString() : "N/A");
            row.createCell(2).setCellValue(l.getNombre());
            row.createCell(3).setCellValue(l.getEstado());
        }

        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
