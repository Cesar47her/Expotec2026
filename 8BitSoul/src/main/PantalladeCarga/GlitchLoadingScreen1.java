package main.PantalladeCarga;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;
import javax.swing.*;
import main.Util.WindowManager;

public class GlitchLoadingScreen1 extends JFrame {
    private final JFrame nextWindow; 

    public GlitchLoadingScreen1(JFrame nextWindow) {
        this.nextWindow = nextWindow;
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        setTitle("8BIT SOUL - Loading...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); 
        setSize(1024, 576); 
        setLocationRelativeTo(null);
        WindowManager.getInstance().register(this);
        
        // Apunta a la clase interna LoadingPanel1 modificada
        LoadingPanel1 loadingPanel = new LoadingPanel1(this, nextWindow);
        add(loadingPanel);
    }
}

class LoadingPanel1 extends JPanel implements ActionListener {
    private int progress = 0;
    private final Timer timer;
    private final Random random = new Random();
    private final JFrame parentFrame;
    private final JFrame nextWindow; 

    private Image backgroundImage;

    private boolean isGlitching = false;
    private int glitchDuration = 0;
    private int offsetX = 0;
    private int offsetY = 0;

    private final Color COLOR_BG = new Color(10, 15, 30);       
    private final Color COLOR_CYAN = new Color(0, 240, 255);    
    private final Color COLOR_MAGENTA = new Color(211, 0, 197); 

    public LoadingPanel1(JFrame parentFrame, JFrame nextWindow) {
        this.parentFrame = parentFrame;
        this.nextWindow = nextWindow;
        setBackground(COLOR_BG);
        
        backgroundImage = new ImageIcon("src/imagenes/Carga2.png").getImage();

        timer = new Timer(50, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (!isGlitching) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            offsetX = 0;
            offsetY = 0;
        } else {
            offsetX = random.nextInt(15) - 7;
            offsetY = random.nextInt(7) - 3;
        }

        int width = getWidth();
        int height = getHeight();

        if (backgroundImage != null && backgroundImage.getWidth(null) > 0) {
            g2d.drawImage(backgroundImage, offsetX, offsetY, width, height, this);
        } else {
            g2d.setColor(COLOR_BG);
            g2d.fillRect(0, 0, width, height);
        }

        int barWidth = 450;
        int barHeight = 35;
        int barX = (width - barWidth) / 2; 
        int barY = height - 160;           

        g2d.setFont(new Font("Pixel Emulator", Font.BOLD, 28));
        String loadingText = isGlitching && random.nextBoolean() ? "C@RG4ND0..." : "CARGANDO....";
        int textX = barX; 
        int textY = barY - 20; 
        
        if (isGlitching) {
            g2d.setColor(COLOR_MAGENTA);
            g2d.drawString(loadingText, textX - 4 + offsetX, textY + offsetY);
        }
        g2d.setColor(COLOR_CYAN);
        g2d.drawString(loadingText, textX, textY);

        g2d.setColor(COLOR_CYAN);
        g2d.setStroke(new BasicStroke(2f));
        g2d.draw(new RoundRectangle2D.Float(barX + offsetX, barY + offsetY, barWidth, barHeight, 10, 10));

        if (progress > 0) {
            int currentProgressWidth = (int) ((progress / 100.0) * (barWidth - 10));
            GradientPaint gradient = new GradientPaint(barX, barY, COLOR_CYAN, barX + barWidth, barY, COLOR_MAGENTA);
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(barX + 5 + offsetX, barY + 5 + offsetY, currentProgressWidth, barHeight - 10, 8, 8));
        }

        g2d.setFont(new Font("Pixel Emulator", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        String percentText = progress + "%";
        g2d.drawString(percentText, barX + barWidth - 65 + offsetX, barY + 23 + offsetY);

        g2d.setFont(new Font("Pixel Emulator", Font.PLAIN, 14));
        g2d.setColor(COLOR_CYAN.darker());
        String subText = "PREPARANDO LA MEJOR EXPERIENCIA PARA TI";
        int subTextX = (width - g2d.getFontMetrics().stringWidth(subText)) / 2;
        int subTextY = barY + barHeight + 40; 
        g2d.drawString(subText, subTextX + offsetX, subTextY + offsetY);

        if (isGlitching) {
            g2d.setColor(random.nextBoolean() ? COLOR_CYAN : COLOR_MAGENTA);
            for (int i = 0; i < 6; i++) {
                int bugY = random.nextInt(height);
                int bugH = random.nextInt(12) + 2;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.fillRect(0, bugY, width, bugH);
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (progress < 100) {
            if (random.nextInt(10) > 2) {
                progress += random.nextInt(3) + 1;
                if (progress > 100) progress = 100;
            }
        }

        if (!isGlitching) {
            if (random.nextInt(100) < 4) {
                isGlitching = true;
                glitchDuration = random.nextInt(4) + 2; 
            }
        } else {
            glitchDuration--;
            if (glitchDuration <= 0) isGlitching = false;
        }

        repaint(); 

        if (progress >= 100) {
            timer.stop();
            ejecutarTransicionFinal();
        }
    }

    private void ejecutarTransicionFinal() {
        isGlitching = true;
        repaint();

        Timer exitTimer = new Timer(400, e -> {
            parentFrame.dispose(); 
            if (nextWindow != null) {
                nextWindow.setVisible(true); 
            }
        });
        exitTimer.setRepeats(false);
        exitTimer.start();
    }
}