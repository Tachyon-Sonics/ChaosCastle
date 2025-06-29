package ch.chaos.library.launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.chaos.library.Dialogs;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.utils.Platform;
import ch.chaos.library.utils.TableLayout;
import ch.pitchtech.modula.runtime.Runtime;


public class LauncherFrame extends JFrame {
    
    private final AppSettings appSettings;
    private final Consumer<AppSettings> onApply;
    
    private JPanel mainPanel;
    
    
    public LauncherFrame(AppSettings appSettings, Consumer<AppSettings> onApply) {
        super(Runtime.getAppNameOrDefault());
        this.appSettings = appSettings;
        this.onApply = onApply;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(Dialogs.instance().getAppImageList());
        
        mainPanel = new JPanel();
        getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new TableLayout(2));
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel();
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.setLayout(new GridLayout(1, 2));
        JPanel startPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start");
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD));
        startPanel.add(startButton);
        startButton.addActionListener(this::start);
        JPanel cancelPanel = new JPanel(new FlowLayout());
        JButton cancelButton = new JButton("Cancel");
        cancelPanel.add(cancelButton);
        cancelButton.addActionListener(this::cancel);
        if (Platform.isWindows()) {
            buttonsPanel.add(startPanel);
            buttonsPanel.add(cancelPanel);
        } else {
            buttonsPanel.add(cancelPanel);
            buttonsPanel.add(startPanel);
        }
    }
    
    private void start(ActionEvent e) {
        setVisible(false);
        dispose();
        onApply.accept(appSettings);
    }
    
    private void cancel(ActionEvent e) {
        setVisible(false);
        dispose();
    }

}
