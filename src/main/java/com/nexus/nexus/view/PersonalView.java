package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Personal;
import com.nexus.nexus.repository.PersonalRepository;
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
public class PersonalView {

    @Autowired
    private PersonalRepository personalRepository;

    @GetMapping("/view/personal")
    public String lista(Model model) {
        model.addAttribute("personal", personalRepository.findAll());
        return "personal/personal";
    }

    @GetMapping("/view/personal/form")
    public String form(Model model) {
        model.addAttribute("personal", new Personal());
        return "personal/personalForm";
    }

    @PostMapping("/view/personal/save")
    public String save(@ModelAttribute Personal personal, RedirectAttributes ra) {
        personalRepository.save(personal);
        ra.addFlashAttribute("success", "Personal registrado exitosamente");
        return "redirect:/view/personal";
    }

    @GetMapping("/view/personal/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("personal", personalRepository.findById(id).orElse(null));
        return "personal/personalForm";
    }

    @PostMapping("/view/personal/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        personalRepository.deleteById(id);
        ra.addFlashAttribute("success", "Personal eliminado exitosamente");
        return "redirect:/view/personal";
    }

    @GetMapping("/view/personal/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=personal.pdf");

        List<Personal> lista = personalRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Listado de Personal"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.addCell("ID");
        table.addCell("Nombre");
        table.addCell("Documento");
        table.addCell("Teléfono");
        table.addCell("Correo");
        table.addCell("Dirección");
        table.addCell("Rol");

        for (Personal p : lista) {
            table.addCell(p.getId_personal().toString());
            table.addCell(p.getNombre());
            table.addCell(p.getDocumento());
            table.addCell(p.getTelefono());
            table.addCell(p.getCorreo());
            table.addCell(p.getDireccion());
            table.addCell(p.getRol());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/personal/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=personal.xlsx");

        List<Personal> lista = personalRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Personal");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Documento");
        header.createCell(3).setCellValue("Teléfono");
        header.createCell(4).setCellValue("Correo");
        header.createCell(5).setCellValue("Dirección");
        header.createCell(6).setCellValue("Rol");

        int rowNum = 1;
        for (Personal p : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId_personal().toString());
            row.createCell(1).setCellValue(p.getNombre());
            row.createCell(2).setCellValue(p.getDocumento());
            row.createCell(3).setCellValue(p.getTelefono());
            row.createCell(4).setCellValue(p.getCorreo());
            row.createCell(5).setCellValue(p.getDireccion());
            row.createCell(6).setCellValue(p.getRol());
        }

        for (int i = 0; i < 7; i++) sheet.autoSizeColumn(i);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
