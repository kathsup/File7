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
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class EditorTexto extends JFrame {

    private JTextPane textPane;
    private JComboBox<String> fontBox;
    private JComboBox<Integer> sizeBox;
    private JPanel colorPanel;
    private JList<String> listaArchivos;
    private DefaultListModel<String> modeloListaArchivos;
    private final Queue<Color> coloresRecientes = new LinkedList<>();
    private final int MAX_COLORES = 15;
    private final File carpetaArchivos = new File("archivos_guardados");

    public EditorTexto() {
        setTitle("Editor de Texto");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        if (!carpetaArchivos.exists()) carpetaArchivos.mkdir();
        setLocationRelativeTo(null);

        modeloListaArchivos = new DefaultListModel<>();
        listaArchivos = new JList<>(modeloListaArchivos);
        listaArchivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaArchivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                abrirArchivoDesdeLista(listaArchivos.getSelectedValue());
            }
        });

        cargarArchivosEnLista();

        
        JScrollPane panelLista = new JScrollPane(listaArchivos);
        panelLista.setPreferredSize(new Dimension(200, getHeight()));
        add(panelLista, BorderLayout.WEST);

        textPane = new JTextPane();
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(crearBarraHerramientas(), BorderLayout.NORTH);
    }

    private JPanel crearBarraHerramientas() {
        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        barraSuperior.add(new JLabel("Fuente:"));
        fontBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontBox.setPreferredSize(new Dimension(150, 25));
        fontBox.addActionListener(e -> aplicarEstilo());
        barraSuperior.add(fontBox);

        barraSuperior.add(new JLabel("Tamaño:"));
        sizeBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 24, 32, 48});
        sizeBox.setPreferredSize(new Dimension(60, 25));
        sizeBox.addActionListener(e -> aplicarEstilo());
        barraSuperior.add(sizeBox);

        JButton btnColorChooser = new JButton("Elegir Color");
        btnColorChooser.setPreferredSize(new Dimension(120, 25));
        btnColorChooser.addActionListener(e -> elegirColor());
        barraSuperior.add(btnColorChooser);

        JPanel panelColores = new JPanel(new BorderLayout());
        panelColores.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Colores utilizados",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)
        ));
        colorPanel = new JPanel(new GridLayout(2, 8, 2, 2));
        panelColores.add(colorPanel, BorderLayout.CENTER);
        barraSuperior.add(panelColores);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(100, 25));
        btnGuardar.addActionListener(e -> guardarArchivo());
        barraSuperior.add(btnGuardar);

        JButton btnAbrir = new JButton("Abrir");
        btnAbrir.setPreferredSize(new Dimension(100, 25));
        btnAbrir.addActionListener(e -> abrirArchivoManual());
        barraSuperior.add(btnAbrir);

        actualizarPanelColores();
        return barraSuperior;
    }

    private void aplicarEstilo() {
        String fuente = (String) fontBox.getSelectedItem();
        int tamano = (int) sizeBox.getSelectedItem();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, fuente);
        StyleConstants.setFontSize(attrs, tamano);
        textPane.setCharacterAttributes(attrs, false);
    }

    private void elegirColor() {
        Color color = JColorChooser.showDialog(this, "Seleccione un color", Color.BLACK);
        if (color != null) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            textPane.setCharacterAttributes(attrs, false);
            agregarColorReciente(color);
        }
    }

    private void agregarColorReciente(Color color) {
        if (coloresRecientes.contains(color)) return;
        if (coloresRecientes.size() >= MAX_COLORES) coloresRecientes.poll();
        coloresRecientes.add(color);
        actualizarPanelColores();
    }

    private void actualizarPanelColores() {
        colorPanel.removeAll();
        for (Color color : coloresRecientes) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(25, 25));
            colorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            colorButton.setFocusPainted(false);
            colorButton.addActionListener(e -> aplicarColor(color));
            colorPanel.add(colorButton);
        }
        while (colorPanel.getComponentCount() < MAX_COLORES) {
            JButton emptyButton = new JButton();
            emptyButton.setEnabled(false);
            emptyButton.setBackground(Color.LIGHT_GRAY);
            emptyButton.setPreferredSize(new Dimension(25, 25));
            colorPanel.add(emptyButton);
        }
        colorPanel.revalidate();
        colorPanel.repaint();
    }

    private void aplicarColor(Color color) {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        textPane.setCharacterAttributes(attrs, false);
    }

    private void abrirEnWord(File archivo) {
    try {
        Desktop.getDesktop().open(archivo); 
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo en Word.");
    }
}

    
   private void guardarArchivo() {
    String nombreArchivo = JOptionPane.showInputDialog(this, "Nombre del archivo:");
    if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) return;

    File archivo = new File(carpetaArchivos, nombreArchivo + ".rtf");

    try {
        GestorArchivo.guardarArchivoRTF(archivo.getAbsolutePath(), textPane);
        JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.");
        cargarArchivosEnLista();
        abrirEnWord(archivo); 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage());
    }
}


    private void abrirArchivoManual() {
        JFileChooser selector = new JFileChooser();
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            String contenido = GestorArchivo.cargarArchivo(archivo.getAbsolutePath());
            GestorArchivo.cargarTextoADocumento(textPane, contenido);
            JOptionPane.showMessageDialog(this, "Archivo abierto correctamente.");
        }
    }

    private void cargarArchivosEnLista() {
    modeloListaArchivos.clear(); 
    
    File[] archivos = carpetaArchivos.listFiles((dir, name) -> name.toLowerCase().endsWith(".rtf"));

    if (archivos != null) {
        for (File archivo : archivos) {
            modeloListaArchivos.addElement(archivo.getName()); 
        }
    }
}

   private void abrirArchivo() {
    JFileChooser selector = new JFileChooser();
    selector.setDialogTitle("Abrir archivo");
    selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Rich Text Format (*.txt)", "rtf"));

    int opcion = selector.showOpenDialog(this);
    if (opcion == JFileChooser.APPROVE_OPTION) {
        File archivo = selector.getSelectedFile();
        try {
            GestorArchivo.cargarArchivoRTF(archivo.getAbsolutePath(), textPane);
            JOptionPane.showMessageDialog(this, "Archivo cargado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

    
   private void abrirArchivoDesdeLista(String nombreArchivo) {
    if (nombreArchivo == null) return;
    
    File archivo = new File(carpetaArchivos, nombreArchivo);

    try {
        GestorArchivo.cargarArchivoRTF(archivo.getAbsolutePath(), textPane);
        JOptionPane.showMessageDialog(this, "Archivo cargado correctamente desde la lista.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + e.getMessage());
    }
}

}
