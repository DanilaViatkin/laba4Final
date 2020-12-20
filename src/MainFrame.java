import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private JFileChooser fileChooser = null;
    // Пункты меню
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    // Компонент-отображатель графика
    private GraphicsDisplay display = new GraphicsDisplay();
    private boolean fileLoaded = false;

    public MainFrame() {
        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2,
                (kit.getScreenSize().height - HEIGHT) / 2);
        // Развѐртывание окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION)
                    readDataGraphics(fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(openGraphicsAction);
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        Action showAxisAction = new AbstractAction("Показывать оси координат") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        // Установить GraphicsDisplay в цент граничной компоновки
        getContentPane().add(display, BorderLayout.CENTER);
    }

    // Считывание данных графика из существующего файла
    protected void readDataGraphics(File selectedFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            List<Double[]> graphicsData = new ArrayList<>();
            while (bufferedReader.ready()) {
                String[] numbers = bufferedReader.readLine().split(" ");

                double x = Double.parseDouble(numbers[0]);
                double y = Double.parseDouble(numbers[1]);
                graphicsData.add(new Double[]{x,y});
            }
            int size = graphicsData.size();
            if (size > 0) {
                fileLoaded = true;
                Double[][] array = new Double[size][size];
                for (int i = 0; i < array.length; i++) {
                    array[i] = graphicsData.get(i);
                }
                display.showGraphics(array);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    // Класс-слушатель событий, связанных с отображением меню
    private class GraphicsMenuListener implements MenuListener {
        // Обработчик, вызываемый перед показом меню
        public void menuSelected(MenuEvent e) {
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}
