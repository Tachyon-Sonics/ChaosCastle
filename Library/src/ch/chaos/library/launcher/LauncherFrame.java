package ch.chaos.library.launcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import ch.chaos.library.Dialogs;
import ch.chaos.library.settings.AppMode;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.settings.GfxPipelineType;
import ch.chaos.library.settings.VsyncType;
import ch.chaos.library.utils.Platform;
import ch.chaos.library.utils.TableLayout;
import ch.pitchtech.modula.runtime.Runtime;


public class LauncherFrame extends JFrame {
    
    private final AppSettings appSettings;
    private final Consumer<AppSettings> onApply;
    
    private JPanel mainPanel;
    
    
    public LauncherFrame(AppSettings appSettings, AppMode appMode, Consumer<AppSettings> onApply) {
        super(Runtime.getAppNameOrDefault() + " - Launcher");
        this.appSettings = appSettings;
        this.onApply = onApply;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(Dialogs.instance().getAppImageList());
        
        mainPanel = new JPanel();
        getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        
        JPanel settingsPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.setLayout(new TableLayout(2, gbc));
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        
        // Settings
        JLabel lblMode = new JLabel("Mode: ");
        settingsPanel.add(lblMode);
        JPanel pnlMode = new JPanel(new GridLayout(1, 2));
        settingsPanel.add(pnlMode);
        ButtonGroup grpMode = new ButtonGroup();
        JRadioButton rbWindow = new JRadioButton("Window", !appMode.isFullScreen());
        grpMode.add(rbWindow);
        pnlMode.add(rbWindow);
        JRadioButton rbFullScreen = new JRadioButton("Full Screen", appMode.isFullScreen());
        grpMode.add(rbFullScreen);
        pnlMode.add(rbFullScreen);
        
        JLabel lblPipeline = new JLabel("Java2D Pipeline: ");
        settingsPanel.add(lblPipeline);
        JComboBox<GfxPipelineType> cbPipeline = new JComboBox<>(GfxPipelineType.getPlatformSupportedTypes().toArray(GfxPipelineType[]::new));
        settingsPanel.add(cbPipeline);
        
        JLabel lblVsync = new JLabel("V-Sync Mode: ");
        settingsPanel.add(lblVsync);
        JComboBox<VsyncType> cbVsync = new JComboBox<>(VsyncType.values());
        settingsPanel.add(cbVsync);
        
        // Buttons
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
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        headerPanel.add(new TitlePanel(), BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
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
