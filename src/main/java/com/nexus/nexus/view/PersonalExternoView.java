package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.PersonalExterno;
import com.nexus.nexus.repository.PersonalExternoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PersonalExternoView
{
    @Autowired
    private PersonalExternoRepository personalExternoRepository;

    @GetMapping("/view/personalExterno")
    public String lista(Model model)
    {
        model.addAttribute("personalExterno", personalExternoRepository.findAll());
        return "personalExterno/personalExterno";
    }

    @GetMapping("/view/personalExterno/form")
    public String form(Model model)
    {
        model.addAttribute("personalExterno", new PersonalExterno());
        return "personalExterno/personalExternoForm";
    }

    @PostMapping("/view/personalExterno/save")
    public String save(@ModelAttribute PersonalExterno personalExterno, RedirectAttributes ra)
    {
        personalExternoRepository.save(personalExterno);
        ra.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
        return "redirect:/view/personalExterno";
    }

    @GetMapping("/view/personalExterno/edit/{id}")
    public String edit(@PathVariable Long id, Model model)
    {
        PersonalExterno personalExterno = personalExternoRepository.findById(id).orElse(null);
        model.addAttribute("personalExterno", personalExterno);
        return "personalExterno/personalExternoForm";
    }

    @PostMapping("/view/personalExterno/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra)
    {
        personalExternoRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
        return "redirect:/view/personalExterno";
    }


    @GetMapping("/view/personalExterno/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=personalExterno.pdf");

        List<PersonalExterno> personalExternoList = personalExternoRepository.findAll();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Listado de personalExterno"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(13);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        //Columnas
        table.addCell("ID PersonalExterno");
        table.addCell("Nombre");
        table.addCell("Tipo de documento");
        table.addCell("Documento");
        table.addCell("Telefono");
        table.addCell("Empresa");
        table.addCell("Motivo Visita");
        table.addCell("Fecha de visita");
        table.addCell("Hora ingreso");
        table.addCell("Hora salida");
        table.addCell("Tiempo de estancia");
        table.addCell("Estado");
        table.addCell("Fecha de creacion");

        //filas
        for (PersonalExterno personalExterno : personalExternoList)
        {
            table.addCell(personalExterno.getId_personal_externo().toString());
            table.addCell(personalExterno.getNombre());
            table.addCell(personalExterno.getTipo_documento());
            table.addCell(personalExterno.getDocumento());
            table.addCell(personalExterno.getTelefono());
            table.addCell(personalExterno.getEmpresa());
            table.addCell(personalExterno.getMotivo_visita());
            table.addCell(personalExterno.getFecha_visita() != null ? personalExterno.getFecha_visita().toString() : "N/A");
            table.addCell(personalExterno.getHora_ingreso() != null ? personalExterno.getHora_ingreso().toString() : "N/A");
            table.addCell(personalExterno.getHora_salida() != null ? personalExterno.getHora_salida().toString() : "N/A");
            table.addCell(personalExterno.getTiempo_estancia().toString());
            table.addCell(personalExterno.getEstado());
            table.addCell(personalExterno.getFecha_creacion() != null ? personalExterno.getFecha_creacion().toString() : "N/A");
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/personalExterno/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=personalExterno.xlsx");

        List<PersonalExterno> personalExternoList = personalExternoRepository.findAll(); // Reemplaza con tu repositorio

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Aprendices");

        // Crear encabezado
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID PersonalExterno");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Tipo de documento");
        headerRow.createCell(3).setCellValue("Documento");
        headerRow.createCell(4).setCellValue("Telefono");
        headerRow.createCell(5).setCellValue("Empresa");
        headerRow.createCell(6).setCellValue("Motivo Visita");
        headerRow.createCell(7).setCellValue("Fecha de visita");
        headerRow.createCell(8).setCellValue("Hora ingreso");
        headerRow.createCell(9).setCellValue("Hora salida");
        headerRow.createCell(10).setCellValue("Tiempo de estancia");
        headerRow.createCell(11).setCellValue("Estado");
        headerRow.createCell(12).setCellValue("Fecha de creacion");

        // Agregar datos
        int rowNum = 1;
        for (PersonalExterno personalExterno : personalExternoList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(personalExterno.getId_personal_externo().toString());
            row.createCell(1).setCellValue(personalExterno.getNombre());
            row.createCell(2).setCellValue(personalExterno.getTipo_documento());
            row.createCell(3).setCellValue(personalExterno.getDocumento());
            row.createCell(4).setCellValue(personalExterno.getTelefono());
            row.createCell(5).setCellValue(personalExterno.getEmpresa());
            row.createCell(6).setCellValue(personalExterno.getMotivo_visita());
            row.createCell(7).setCellValue(personalExterno.getFecha_visita() != null ? personalExterno.getFecha_visita().toString() : "N/A");
            row.createCell(8).setCellValue(personalExterno.getHora_ingreso() != null ? personalExterno.getHora_ingreso().toString() : "N/A");
            row.createCell(9).setCellValue(personalExterno.getHora_salida() != null ? personalExterno.getHora_salida().toString() : "N/A");
            row.createCell(10).setCellValue(personalExterno.getTiempo_estancia().toString());
            row.createCell(11).setCellValue(personalExterno.getEstado());
            row.createCell(12).setCellValue(personalExterno.getFecha_creacion() != null ? personalExterno.getFecha_creacion().toString() : "N/A");
        }

        // Autoajustar columnas
        for (int i = 0; i < 13; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
