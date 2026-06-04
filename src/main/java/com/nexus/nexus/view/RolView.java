package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Rol;
import com.nexus.nexus.repository.RolRepository;
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
public class RolView {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/view/rol")
    public String lista(Model model) {
        model.addAttribute("roles", rolRepository.findAll());
        return "rol/rol";
    }

    @GetMapping("/view/rol/form")
    public String form(Model model) {
        model.addAttribute("rol", new Rol());
        return "rol/rolForm";
    }

    @PostMapping("/view/rol/save")
    public String save(@ModelAttribute Rol rol, RedirectAttributes ra) {
        rolRepository.save(rol);
        ra.addFlashAttribute("success", "Rol registrado exitosamente");
        return "redirect:/view/rol";
    }

    @GetMapping("/view/rol/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("rol", rolRepository.findById(id).orElse(null));
        return "rol/rolForm";
    }

    @PostMapping("/view/rol/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        rolRepository.deleteById(id);
        ra.addFlashAttribute("success", "Rol eliminado exitosamente");
        return "redirect:/view/rol";
    }

    @GetMapping("/view/rol/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=roles.pdf");

        List<Rol> lista = rolRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Roles"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID Rol");
        table.addCell("Descripción");
        table.addCell("Estado");

        for (Rol r : lista) {
            table.addCell(r.getId_rol().toString());
            table.addCell(r.getDescripcion());
            table.addCell(r.getEstado());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/rol/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=roles.xlsx");

        List<Rol> lista = rolRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Roles");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Rol");
        header.createCell(1).setCellValue("Descripción");
        header.createCell(2).setCellValue("Estado");

        int rowNum = 1;
        for (Rol r : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getId_rol().toString());
            row.createCell(1).setCellValue(r.getDescripcion());
            row.createCell(2).setCellValue(r.getEstado());
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
