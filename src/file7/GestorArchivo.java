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
import javax.swing.text.rtf.RTFEditorKit;


public class GestorArchivo {

    public static void guardarArchivo(String ruta, String contenido) {
        try (FileWriter writer = new FileWriter(ruta)) {
            writer.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cargarArchivo(String ruta) {
        StringBuilder contenido = new StringBuilder();
        try (FileReader reader = new FileReader(ruta)) {
            int c;
            while ((c = reader.read()) != -1) {
                contenido.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.toString();
    }

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

    public static void cargarTextoADocumento(JTextPane textPane, String contenido) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            for (String linea : contenido.split("\n")) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split("\\|");
                if (partes.length < 4) continue;

                String texto = partes[0];
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attrs, partes[1]);
                StyleConstants.setFontSize(attrs, Integer.parseInt(partes[2]));
                StyleConstants.setForeground(attrs, Color.decode(partes[3]));

                doc.insertString(doc.getLength(), texto, attrs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //guarda en documento 
   public static void guardarArchivoRTF(String ruta, JTextPane textPane) {
        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            RTFEditorKit kit = new RTFEditorKit();
            kit.write(fos, textPane.getStyledDocument(), 0, textPane.getStyledDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cargarArchivoRTF(String ruta, JTextPane textPane) {
        try (FileInputStream fis = new FileInputStream(ruta)) {
            RTFEditorKit kit = new RTFEditorKit();
            StyledDocument doc = new DefaultStyledDocument();
            kit.read(fis, doc, 0);
            textPane.setStyledDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
