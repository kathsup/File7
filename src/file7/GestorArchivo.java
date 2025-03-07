/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package file7;

/**
 *
 * @author 50494
 */
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;

public class GestorArchivo {

    // Guarda el contenido en un archivo de texto
    public static void guardarArchivo(String ruta, String contenido) {
        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carga el contenido desde un archivo de texto
    public static String cargarArchivo(String ruta) {
        StringBuilder contenido = new StringBuilder();
        try (FileReader reader = new FileReader(ruta)) {
            int i;
            while ((i = reader.read()) != -1) {
                contenido.append(i).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.toString();
    }

    // Convierte el StyledDocument en un String con formato (color, fuente, tamaño)
    public static String convertirDocumentoATexto(StyledDocument doc) {
        StringBuilder resultado = new StringBuilder();

        try {
            int length = doc.getLength();
            for (int i = 0; i < length; ) {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                int end = element.getEndOffset();
                String texto = doc.getText(i, end - i);

                String font = StyleConstants.getFontFamily(attrs);
                int size = StyleConstants.getFontSize(attrs);
                Color color = StyleConstants.getForeground(attrs);
                String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

                resultado.append(texto)
                        .append("|").append(font)
                        .append("|").append(size)
                        .append("|").append(colorHex)
                        .append("\n");

                i = end;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return resultado.toString();
    }

    // Carga un String con formato a un StyledDocument
    public static void cargarTextoADocumento(JTextPane textPane, String contenido) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength()); // Limpiar documento
            String[] lineas = contenido.split("\n");

            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;
                
                String[] partes = linea.split("\\|");
                if (partes.length < 4) continue;

                String texto = partes[0];
                String font = partes[1];
                int size = Integer.parseInt(partes[2]);
                Color color = Color.decode(partes[3]);

                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attrs, font);
                StyleConstants.setFontSize(attrs, size);
                StyleConstants.setForeground(attrs, color);

                doc.insertString(doc.getLength(), texto, attrs);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}