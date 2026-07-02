package main.Util;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.*;

public class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();
    private final Set<JFrame> windows = Collections.newSetFromMap(new WeakHashMap<>());
    private volatile boolean updating = false;
    private Rectangle lastKnownBounds = null;
    private int lastKnownState = Frame.NORMAL;

    private WindowManager() {}

    public static WindowManager getInstance() {
        return INSTANCE;
    }

    public synchronized void register(JFrame frame) {
        if (frame == null) return;
        Object registered = frame.getRootPane().getClientProperty("windowManager.registered");
        if (registered != null && (boolean) registered) return;

        windows.add(frame);
        frame.getRootPane().putClientProperty("windowManager.registered", true);

        // Guardamos las dimensiones que tú le pusiste en el constructor (ej: 1200x675)
        final int originalWidth = frame.getWidth();
        final int originalHeight = frame.getHeight();

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                syncSizes(frame);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (!updating && frame.getExtendedState() == Frame.NORMAL) {
                    lastKnownBounds = frame.getBounds();
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            boolean sizeApplied = false;

            // 1. Intentar heredar de otra ventana que esté visible en este momento
            for (JFrame other : windows) {
                if (other == null || other == frame) continue;
                if (!other.isDisplayable() || !other.isVisible()) continue;
                try {
                    int state = other.getExtendedState();
                    Rectangle b = other.getBounds();
                    
                    // REGLA INTELIGENTE: Solo heredamos el tamaño si es IGUAL o MAYOR al tamaño propio de esta ventana
                    if (b.width >= originalWidth && b.height >= originalHeight) {
                        if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                        } else {
                            frame.setExtendedState(Frame.NORMAL);
                            frame.setBounds(b);
                        }
                        sizeApplied = true;
                        break;
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
            
            // 2. Si no hay ventanas visibles, mirar el historial (lastKnownBounds)
            if (!sizeApplied && lastKnownBounds != null) {
                try {
                    // Solo aplicamos el historial si no encoge las dimensiones nativas de la ventana
                    if (lastKnownBounds.width >= originalWidth && lastKnownBounds.height >= originalHeight) {
                        if ((lastKnownState & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                        } else {
                            frame.setExtendedState(Frame.NORMAL);
                            frame.setBounds(lastKnownBounds);
                        }
                        sizeApplied = true;
                    }
                } catch (Exception ignored) {}
            }

            // 3. Si el tamaño guardado era más chico (960x540) y esta ventana quiere ser más grande (1200x675),
            // ignoramos el historial, forzamos sus 1200x675 y la centramos de nuevo.
            if (!sizeApplied) {
                frame.setSize(originalWidth, originalHeight);
                frame.setLocationRelativeTo(null);
                
                // Actualizamos el historial para que las SIGUIENTES ventanas hereden este nuevo tamaño grande
                if (frame.getExtendedState() == Frame.NORMAL) {
                    lastKnownBounds = frame.getBounds();
                }
            }
        });
    }

    private void syncSizes(JFrame source) {
        if (updating) return;
        try {
            updating = true;
            Rectangle bounds = source.getBounds();
            int state = source.getExtendedState();

            lastKnownBounds = bounds;
            lastKnownState = state;

            for (JFrame f : windows) {
                if (f == null || f == source) continue;
                if (!f.isDisplayable() || !f.isVisible()) continue;
                
                SwingUtilities.invokeLater(() -> {
                    try {
                        if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                            f.setExtendedState(Frame.MAXIMIZED_BOTH);
                        } else {
                            // Solo redimensionar las otras ventanas si la ventana origen es más grande o igual
                            if (bounds.width >= f.getWidth() && bounds.height >= f.getHeight()) {
                                f.setExtendedState(Frame.NORMAL);
                                f.setBounds(bounds);
                            }
                        }
                    } catch (Exception ex) {
                        // ignore
                    }
                });
            }
        } finally {
            updating = false;
        }
    }
}