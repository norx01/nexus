package com.nexus.nexus.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nexus.nexus.model.Usuarios;
import com.nexus.nexus.repository.UsuariosRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.util.List;

@Controller
public class UsuariosView
{
    @Autowired
    private UsuariosRepository usuariosRepository;

    @GetMapping("/view/usuarios")
    public String lista(Model model)
    {
        model.addAttribute("usuarios", usuariosRepository.findAll());
        return "usuarios";
    }

    @GetMapping("/view/usuarios/form")
    public String form(Model model)
    {
        model.addAttribute("usuarios", new Usuarios());
        return "usuariosForm";
    }

    @PostMapping("/view/usuarios/save")
    public String save(@ModelAttribute Usuarios usuarios, RedirectAttributes ra)
    {
        usuariosRepository.save(usuarios);
        ra.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
        return "redirect:/view/usuarios";
    }

    @GetMapping("/view/usuarios/edit/{id}")
    public String edit(@PathVariable Long id, Model model)
    {
        Usuarios usuarios = usuariosRepository.findById(id).orElse(null);
        model.addAttribute("usuarios", usuarios);
        return "usuariosForm";
    }

    @PostMapping("/view/usuarios/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra)
    {
        usuariosRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
        return "redirect:/view/usuarios";
    }


    @GetMapping("/view/usuarios/pdf")
    public void exportarPDF(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=Usuarios.pdf");

        List<Usuarios> usuariosList = usuariosRepository.findAll();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Listado de Usuarios"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        //Columnas
        table.addCell("ID Usuario");
        table.addCell("Nombre");
        table.addCell("Apellido");
        table.addCell("Fecha");
        table.addCell("Dni");
        table.addCell("Correo");

        //filas
        for (Usuarios usuarios : usuariosList)
        {
            table.addCell(usuarios.getId_usuario().toString());
            table.addCell(usuarios.getNombre());
            table.addCell(usuarios.getApellido());
            table.addCell(usuarios.getFecha().toString());
            table.addCell(usuarios.getDni().toString());
            table.addCell(usuarios.getCorreo());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/view/usuarios/excel")
    public void exportarExcel(HttpServletResponse response) throws Exception
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Usuarios.xlsx");

        List<Usuarios> usuariosList = usuariosRepository.findAll(); // Reemplaza con tu repositorio

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Aprendices");

        // Crear encabezado
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID Usuarios");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Apellido");
        headerRow.createCell(3).setCellValue("Fecha");
        headerRow.createCell(4).setCellValue("Dni");
        headerRow.createCell(5).setCellValue("Correo");

        // Agregar datos
        int rowNum = 1;
        for (Usuarios f : usuariosList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getId_usuario());
            row.createCell(1).setCellValue(f.getNombre());
            row.createCell(2).setCellValue(f.getApellido());
            row.createCell(3).setCellValue(f.getFecha());
            row.createCell(4).setCellValue(f.getDni());
            row.createCell(5).setCellValue(f.getCorreo());
        }

        // Autoajustar columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
