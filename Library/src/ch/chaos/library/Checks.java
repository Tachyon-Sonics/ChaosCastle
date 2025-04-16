package ch.chaos.library;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import ch.chaos.library.utils.Async;
import ch.chaos.library.utils.FullScreenUtils;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;

public class Checks {

    private static Checks instance;

    private final List<Runnable> termProcs = new ArrayList<>();


    private Checks() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Checks instance() {
        if (instance == null)
            new Checks(); // will set 'instance'
        return instance;
    }

    public void Check(boolean badCase, Runtime.IRef<String> s1, Runtime.IRef<String> s2) {
        if (badCase) {
            Ask(s1, s2, null, new Runtime.Ref<>("Ok"));
            throw new HaltException();
        }
    }

    public void Warn(boolean badCase, Runtime.IRef<String> s1, Runtime.IRef<String> s2) {
        if (badCase) {
            Ask(s1, s2, null, new Runtime.Ref<>("Ok"));
        }
    }

    public void CheckMem(Object adr) {
        CheckMemBool(adr == null);
    }

    public void CheckMemBool(boolean badCase) {
        if (badCase)
            throw new RuntimeException("CheckMem failed");
    }

    public boolean Ask(Runtime.IRef<String> s1, Runtime.IRef<String> s2, Runtime.IRef<String> pos, Runtime.IRef<String> neg) {
        String message = s1.get();
        if (s2 != null && s2.get() != null)
            message += "\n" + s2.get();
        String okText = (pos != null && pos.get() != null) ? pos.get() : null;
        String cancelText = neg.get();
        return ask(message, okText, cancelText);
    }

    private boolean ask(String message, String okText, String cancelText) { // TODO (3) does not work of Grotte (for instances 'Samples' missing) in Graphics.FULL_SCREEN because there is no MainFrame
        Async<Boolean> result = new Async<>();
        if (okText != null) {
            if (Graphics.FULL_SCREEN) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                    JButton okButton = new JButton(okText);
                    okButton.addActionListener((e) -> {
                        FullScreenUtils.removeFullScreenDialog(pane);
                        result.submit(true);
                    });
                    JButton cancelButton = new JButton(cancelText);
                    cancelButton.addActionListener((e) -> {
                        FullScreenUtils.removeFullScreenDialog(pane);
                        result.submit(false);
                    });
                    pane.setOptions(new JComponent[] { okButton, cancelButton });
                    FullScreenUtils.addFullScreenDialog(pane, "Question");
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    int option = JOptionPane.showConfirmDialog(Dialogs.instance().getMainFrame(), message, "Question", JOptionPane.YES_NO_OPTION);
                    boolean reply = (option == JOptionPane.YES_OPTION);
                    result.submit(reply);
                });
            }
        } else {
            if (Graphics.FULL_SCREEN) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
                    JButton closeButton = new JButton(cancelText);
                    closeButton.addActionListener((e) -> {
                        FullScreenUtils.removeFullScreenDialog(pane);
                        result.submit(false);
                    });
                    pane.setOptions(new JComponent[] { closeButton });
                    FullScreenUtils.addFullScreenDialog(pane, "Information");
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(Dialogs.instance().getMainFrame(), message);
                    result.submit(false);
                });
            }
        }
        return result.retrieve();
    }

    public void AddTermProc(Runnable proc) {
        termProcs.add(0, proc);
    }

    public void Terminate() {
        throw new HaltException();
    }

    public void begin() {
        if (System.getProperty("swing.defaultlaf") != null) {
            return; // Laf manually specified
        }
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    UIManager.setLookAndFeel(new NimbusLookAndFeel());
                } catch (UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
//                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
//                try {
//                    UIManager.setLookAndFeel(new MetalLookAndFeel());
//                } catch (UnsupportedLookAndFeelException ex) {
//                    ex.printStackTrace();
//                }
            });
        } catch (InvocationTargetException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() {
        for (Runnable termProc : termProcs) {
            termProc.run();
        }
    }

}
