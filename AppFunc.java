import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.swing.JOptionPane;


public class AppFunc {
    private final AppFrame ui;
    private final DrawingPanel panel;
    private Siec net;
    private static final int GRID = 24;

    public AppFunc(AppFrame ui, DrawingPanel panel) {
        this.ui = ui;
        this.panel = panel;
    }

    public void clearCanvas() {
        panel.clear();
    }

    private void loadIfNeeded() {
        if (net != null) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("weights.bin"))) {
            net = (Siec) in.readObject();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ui, "Нет выученной модели! Сначала нажмите «Ucz MLP»");
        }
    }

    public void recognizeSymbol() {
        loadIfNeeded();
        if (net == null) return;
        double[] out = net.oblicz_wyjscie(panel.capture14());
        double niepewnosc = Math.max(out[0], Math.max(out[1], out[2]));
        if(niepewnosc < 0.70){
            JOptionPane.showMessageDialog(ui, String.format("Nie mogę rozpoznać symbolu"));
            return;
        }
        int idx = IntStream.range(0, 3).boxed().max(Comparator.comparingDouble(i -> out[i])).orElse(0);
        char c = "ELF".charAt(idx);
        JOptionPane.showMessageDialog(ui,
            String.format("Sieć myśli: %c (%.2f  %.2f  %.2f)", c, out[0], out[1], out[2]));
    }

    public void saveTrainSample() {
        saveRow("dataset.csv");
    }

    public void saveTestSample() {
        saveRow("test_dataset.csv");
    }


    private void saveRow(String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            StringBuilder sb = new StringBuilder();
            for (double d : panel.capture14()) {
                sb.append(d).append(' ');
            }
            char c = selected();
            double[] t = oneHot(selected());
            sb.append('[');
            for(int i = 0; i < t.length; i++) {
                sb.append(t[i]);
                if (i < t.length - 1) sb.append(' ');
            }
            sb.append("] ");
            sb.append('\n');
            Files.writeString(path,
                              sb.toString(),
                              StandardOpenOption.CREATE,
                              StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void trainModel() {
        List<double[]> X = new ArrayList<>();
        List<double[]> T = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(Paths.get("dataset.csv"))) {
                String[] s = line.trim().split("\\s+");
                if (s.length != GRID * GRID + 3) continue;
                double[] in = new double[GRID * GRID];
                for (int i = 0; i < in.length; i++)
                    in[i] = Double.parseDouble(s[i]);
                double[] tg = new double[3];
                for (int j = 0; j < 3; j++) {
                    String token = s[GRID * GRID + j].replace("[", "").replace("]", "");
                    tg[j] = Double.parseDouble(token);
                }
                X.add(in);
                T.add(tg);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ui, "Ошибка чтения dataset.csv");
            return;
        }

        net = new Siec(GRID * GRID, 2, new int[]{64, 3});
        net.uczBatch(X, T, 600, 0.05);

        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("weights.bin"))) {
            o.writeObject(net);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ui, "Ошибка сохранения weights.bin");
            return;
        }
        JOptionPane.showMessageDialog(ui, "Model trained and saved!");
    }

    public void testAction() {
        loadIfNeeded();
        if (net == null) return;
        Path p = Paths.get("test_dataset.csv");
        if (!Files.exists(p)) {
            JOptionPane.showMessageDialog(ui, "Brak test_dataset.csv");
            return;
        }
        int ok = 0, total = 0;
        try {
            for (String line : Files.readAllLines(p)) {
                String[] s = line.trim().split("\\s+");
                if (s.length != GRID * GRID + 3) continue;
                double[] in = new double[GRID * GRID];
                double[] tg = new double[3];
                for (int i = 0; i < in.length; i++) in[i] = Double.parseDouble(s[i]);
                for (int j = 0; j < 3; j++) tg[j] = Double.parseDouble(s[GRID * GRID + j]);
                double[] out = net.oblicz_wyjscie(in);
                int pred = IntStream.range(0, 3)
                             .reduce((a, b) -> out[a] > out[b] ? a : b).orElse(0);
                int real = IntStream.range(0, 3)
                             .reduce((a, b) -> tg[a] > tg[b] ? a : b).orElse(0);
                if (pred == real) ok++;
                total++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        double acc = total == 0 ? 0 : (ok * 100.0 / total);
        JOptionPane.showMessageDialog(ui,
            String.format("Accuracy: %.2f%% (%d/%d)", acc, ok, total));
    }

    private char selected() {
        if (ui.getERadio().isSelected()) return 'E';
        if (ui.getLRadio().isSelected()) return 'L';
        return 'F';
    }

    private double[] oneHot(char c) {
        switch (c) {
            case 'E': return new double[]{1.0, 0.0, 0.0};
            case 'L': return new double[]{0.0, 1.0, 0.0};
            default : return new double[]{0.0, 0.0, 1.0};
        }
    }
}

