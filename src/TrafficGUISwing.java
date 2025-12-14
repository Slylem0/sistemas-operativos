import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TrafficGUISwing extends JFrame implements TrafficListener {

    private ControladorTrafico controlador;
    private JPanel colaNortePanel;
    private JPanel colaSurPanel;
    private JPanel zonaCrucePanel;
    private JLabel direccionLabel;
    private JLabel statsLabel;

    private Map<Integer, JPanel> dronesVisuales = new HashMap<>();
    private int totalCruces = 0;
    private int droneIdCounter = 1;

    public TrafficGUISwing() {
        controlador = new ControladorTrafico();
        controlador.addListener(this);

        setTitle("Control de Tráfico de Drones");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(43, 43, 43));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(43, 43, 43));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("SISTEMA DE CONTROL DE TRÁFICO AÉREO");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        direccionLabel = new JLabel("Dirección Actual: NORTE");
        direccionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        direccionLabel.setForeground(Color.WHITE);
        direccionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsLabel = new JLabel("Total de cruces: 0");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setForeground(Color.LIGHT_GRAY);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(direccionLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statsLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(43, 43, 43));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createQueuePanel("COLA NORTE", new Color(30, 144, 255), true));
        panel.add(createCrossingPanel());
        panel.add(createQueuePanel("COLA SUR", new Color(255, 69, 0), false));

        return panel;
    }

    private JPanel createQueuePanel(String title, Color color, boolean isNorth) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(43, 43, 43));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JPanel queuePanel = new JPanel();
        queuePanel.setLayout(new BoxLayout(queuePanel, BoxLayout.Y_AXIS));
        queuePanel.setBackground(new Color(26, 26, 26));
        queuePanel.setBorder(BorderFactory.createLineBorder(color, 2));

        JScrollPane scrollPane = new JScrollPane(queuePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(new Color(26, 26, 26));

        if (isNorth) {
            colaNortePanel = queuePanel;
        } else {
            colaSurPanel = queuePanel;
        }

        container.add(label, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createCrossingPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(43, 43, 43));

        JLabel label = new JLabel("ZONA DE CRUCE", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.YELLOW);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        zonaCrucePanel = new JPanel();
        zonaCrucePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        zonaCrucePanel.setBackground(new Color(58, 58, 58));
        zonaCrucePanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        container.add(label, BorderLayout.NORTH);
        container.add(zonaCrucePanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(43, 43, 43));

        JButton addNorte = createButton("+ Drone Norte", new Color(30, 144, 255));
        addNorte.addActionListener(e -> addDrone(Direccion.NORTE));

        JButton addSur = createButton("+ Drone Sur", new Color(255, 69, 0));
        addSur.addActionListener(e -> addDrone(Direccion.SUR));

        JButton add10 = createButton("+ 10 Drones Aleatorios", new Color(147, 112, 219));
        add10.addActionListener(e -> addMultipleDrones(10));

        panel.add(addNorte);
        panel.add(addSur);
        panel.add(add10);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        return btn;
    }

    private void addDrone(Direccion dir) {
        int id = droneIdCounter++;
        new Drone(id, dir, controlador).start();
    }

    private void addMultipleDrones(int count) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                Direccion dir = (Math.random() < 0.5) ? Direccion.NORTE : Direccion.SUR;
                addDrone(dir);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    public void onDroneEnqueued(int id, Direccion dir, int northQueue, int southQueue) {
        SwingUtilities.invokeLater(() -> {
            JPanel drone = createDronePanel(id, dir);
            dronesVisuales.put(id, drone);

            if (dir == Direccion.NORTE) {
                colaNortePanel.add(drone);
                colaNortePanel.revalidate();
                colaNortePanel.repaint();
            } else {
                colaSurPanel.add(drone);
                colaSurPanel.revalidate();
                colaSurPanel.repaint();
            }
        });
    }

    @Override
    public void onDroneCrossing(int id, Direccion dir) {
        SwingUtilities.invokeLater(() -> {
            JPanel drone = dronesVisuales.get(id);
            if (drone != null) {
                if (dir == Direccion.NORTE) {
                    colaNortePanel.remove(drone);
                    colaNortePanel.revalidate();
                    colaNortePanel.repaint();
                } else {
                    colaSurPanel.remove(drone);
                    colaSurPanel.revalidate();
                    colaSurPanel.repaint();
                }

                zonaCrucePanel.add(drone);
                zonaCrucePanel.revalidate();
                zonaCrucePanel.repaint();
            }
        });
    }

    @Override
    public void onDroneExited(int id, Direccion dir) {
        SwingUtilities.invokeLater(() -> {
            JPanel drone = dronesVisuales.get(id);
            if (drone != null) {
                zonaCrucePanel.remove(drone);
                zonaCrucePanel.revalidate();
                zonaCrucePanel.repaint();
                dronesVisuales.remove(id);

                totalCruces++;
                statsLabel.setText("Total de cruces: " + totalCruces);
            }
        });
    }

    @Override
    public void onDirectionChanged(Direccion newDir) {
        SwingUtilities.invokeLater(() -> {
            direccionLabel.setText("Dirección Actual: " + newDir);
            Color color = (newDir == Direccion.NORTE) ?
                    new Color(30, 144, 255) : new Color(255, 69, 0);
            direccionLabel.setForeground(color);
        });
    }

    private JPanel createDronePanel(int id, Direccion dir) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = (dir == Direccion.NORTE) ?
                        new Color(30, 144, 255) : new Color(255, 69, 0);
                g2d.setColor(color);
                g2d.fillOval(5, 5, 40, 40);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(5, 5, 40, 40);

                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String idStr = String.valueOf(id);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (50 - fm.stringWidth(idStr)) / 2;
                int y = (50 + fm.getAscent()) / 2 - 2;
                g2d.drawString(idStr, x, y);
            }
        };

        panel.setPreferredSize(new Dimension(50, 50));
        panel.setBackground(new Color(26, 26, 26));
        panel.setToolTipText("Drone #" + id);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrafficGUISwing::new);
    }

}