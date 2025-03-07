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
import java.awt.event.*;
import java.io.File;

public class EditorTexto extends JFrame {
    private JTextPane textPane;
    private JComboBox<String> fontBox;
    private JComboBox<Integer> sizeBox;
    private JButton colorButton, saveButton, openButton;

    public EditorTexto() {
        setTitle("Editor de Texto");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        add(new JScrollPane(textPane), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout());

        fontBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontBox.addActionListener(e -> applyStyle());
        toolbar.add(fontBox);

        sizeBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 24, 32, 48});
        sizeBox.addActionListener(e -> applyStyle());
        toolbar.add(sizeBox);

        colorButton = new JButton("Color");
        colorButton.addActionListener(e -> chooseColor());
        toolbar.add(colorButton);

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> guardarArchivo());
        toolbar.add(saveButton);

        openButton = new JButton("Abrir");
        openButton.addActionListener(e -> abrirArchivo());
        toolbar.add(openButton);

        add(toolbar, BorderLayout.NORTH);
    }

    private void applyStyle() {
        String fontFamily = (String) fontBox.getSelectedItem();
        int fontSize = (int) sizeBox.getSelectedItem();
///para fonts
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, fontFamily);
        StyleConstants.setFontSize(attrs, fontSize);

        textPane.setCharacterAttributes(attrs, false);
    }
    
      private void chooseColor() {
          ///para colores
        Color color = JColorChooser.showDialog(this, "Seleccione un color", Color.BLACK);
        if (color != null) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            textPane.setCharacterAttributes(attrs, false);
        }
    }
      
    private void guardarArchivo() {
        String contenido = GestorArchivo.convertirDocumentoATexto(textPane.getStyledDocument());
        GestorArchivo.guardarArchivo("archivo.txt", contenido);
        JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.");
    }

    private void abrirArchivo() {
    JFileChooser selector = new JFileChooser();
    int opcion = selector.showOpenDialog(this);
    
    if (opcion == JFileChooser.APPROVE_OPTION) {
        File archivo = selector.getSelectedFile();
        try {
            String contenido = GestorArchivo.cargarArchivo(archivo.getAbsolutePath());
            GestorArchivo.cargarTextoADocumento(textPane, contenido);
            JOptionPane.showMessageDialog(this, "Archivo cargado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + ex.getMessage());
            ex.printStackTrace(); // Para depurar el error en la consola
        }
    }
}


}
