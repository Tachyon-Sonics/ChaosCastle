package ch.chaos.library.launcher;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

/**
 * TODO (1) continue
 * - Problem with DoubleInput, when on spinner
 * - Document's background color?
 */
public class InfoHandler {
    
    private final JEditorPane target;
    private JComponent currentComponent = null;
    private String currentName = null;
    
    
    public InfoHandler(JEditorPane target) {
        this.target = target;
    }
    
    public void add(String name, JComponent... components) {
        for (JComponent component : components) {
            component.addMouseListener(new MouseAdapter() {
    
                @Override
                public void mouseEntered(MouseEvent e) {
                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == 0 || currentName == null) {
                        showInfo(component, name);
                    }
                }
    
                @Override
                public void mouseExited(MouseEvent e) {
                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == 0) {
                        if (!component.contains(e.getPoint())) {
                            hideInfo(component, name);
                        }
                    }
                }
                
            });
        }
    }
    
    private void showInfo(JComponent component, String name) {
        currentComponent = component;
        if (!Objects.equals(name, currentName)) {
            currentName = name;
            InputStream input = getClass().getResourceAsStream(name + ".xhtml");
            if (input != null) {
                try (input) {
                    byte[] data = input.readAllBytes();
                    String html = new String(data, StandardCharsets.UTF_8);
                    target.setText(html);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                target.setText(name);
            }
            target.setCaretPosition(0);
        }
    }
    
    private void hideInfo(JComponent component, String name) {
        if (name.equals(currentName) && component == currentComponent) {
            currentName = null;
            currentComponent = null;
            target.setText("");
        }
    }

}
