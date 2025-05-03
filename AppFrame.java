import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AppFrame extends JFrame {
    private static final int WIDTH = 1200, HEIGHT = 750, DRAW_PANEL_SIZE = 500;
    private final int GRID = 28;
    private BufferedImage canvas;
    private Graphics2D g2;
    private float[][] pixels = new float[GRID][GRID];
    private JRadioButton eRadio, lRadio, fRadio;
    private DrawingPanel drawingPanel;
    private AppFunc appFunc;

    public AppFrame() {
        super("MLP");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 245));

        canvas = new BufferedImage(DRAW_PANEL_SIZE, DRAW_PANEL_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2.setColor(Color.BLACK);

        drawingPanel = new DrawingPanel(canvas);
        drawingPanel.setPreferredSize(new Dimension(DRAW_PANEL_SIZE, DRAW_PANEL_SIZE));
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(drawingPanel);
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = createControlPanel();
        add(rightPanel, BorderLayout.CENTER);

        appFunc = new AppFunc(this, drawingPanel, canvas, g2, pixels, eRadio, lRadio, fRadio, GRID);
        setVisible(true);
    }


    private JPanel createControlPanel() {
        var control = new JPanel();
        control.setOpaque(false);
        control.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));

        Font btnFont = new Font("Arial", Font.BOLD, 18);
        Dimension btnSize = new Dimension(250, 80);
        JPanel actions = new JPanel(new GridLayout(3, 2, 30, 30));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton clearBtn = createStyledButton("Czysc", btnFont);
        clearBtn.setPreferredSize(btnSize);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> appFunc.clearCanvas());
        actions.add(clearBtn);

        JButton recognizeBtn = createStyledButton("Rozpoznać", btnFont);
        recognizeBtn.setPreferredSize(btnSize);
        recognizeBtn.setForeground(Color.WHITE);
        recognizeBtn.addActionListener(e -> appFunc.recognizeSymbol());
        actions.add(recognizeBtn);

        JButton saveBtn = createStyledButton("Zapisz dane", btnFont);
        saveBtn.setPreferredSize(btnSize);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> { appFunc.saveToCSV("dataset.csv"); appFunc.clearCanvas(); });
        actions.add(saveBtn);

        JButton trainBtn = createStyledButton("Ucz MLP", btnFont);
        trainBtn.setPreferredSize(btnSize);
        trainBtn.setForeground(Color.WHITE);
        trainBtn.addActionListener(e -> appFunc.trainModel());
        actions.add(trainBtn);

        JButton saveTestBtn = createStyledButton("Zapisz do testu", btnFont);
        saveTestBtn.setPreferredSize(btnSize);
        saveTestBtn.setForeground(Color.WHITE);
        saveTestBtn.addActionListener(e -> { appFunc.saveToCSV("dataset_test.csv"); appFunc.clearCanvas(); });
        actions.add(saveTestBtn);

        JButton testBtn = createStyledButton("Test Model", btnFont);
        testBtn.setPreferredSize(btnSize);
        testBtn.setForeground(Color.WHITE);
        testBtn.addActionListener(e -> appFunc.testAction());
        actions.add(testBtn);

        control.add(actions);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 20));
        radioPanel.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                "Wybierz litere", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 20)
        );
        border.setTitleColor(Color.BLACK);
        radioPanel.setBorder(border);

        Font radioFont = new Font("Arial", Font.BOLD, 36);
        eRadio = new JRadioButton("e");
        lRadio = new JRadioButton("l");
        fRadio = new JRadioButton("f");
        for (JRadioButton r : new JRadioButton[]{eRadio, lRadio, fRadio}) {
            r.setFont(radioFont);
            r.setOpaque(false);
            r.setForeground(Color.BLACK);
        }
        var group = new ButtonGroup();
        group.add(eRadio);
        group.add(lRadio);
        group.add(fRadio);
        radioPanel.add(eRadio);
        radioPanel.add(lRadio);
        radioPanel.add(fRadio);

        control.add(radioPanel);
        control.add(Box.createVerticalGlue());
        return control;
    }

    private JButton createStyledButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }
}

