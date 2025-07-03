package ch.chaos.library.launcher;

import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ch.chaos.library.Dialogs;
import ch.chaos.library.settings.AppMode;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.settings.GfxDisplayMode;
import ch.chaos.library.settings.GfxPipelineType;
import ch.chaos.library.settings.VsyncType;
import ch.chaos.library.utils.DoubleProperty;
import ch.chaos.library.utils.Platform;
import ch.chaos.library.utils.gui.DoubleInput;
import ch.chaos.library.utils.gui.TableLayout;
import ch.pitchtech.modula.runtime.Runtime;


public class LauncherFrame extends JFrame {
    
    private final AppSettings appSettings;
    private final Consumer<AppSettings> onApply;
    
    private JPanel mainPanel;
    
    private double quality;
    private double sizePercent;
    private List<Runnable> settingChangeListeners = new ArrayList<>();
    
    
    public LauncherFrame(AppSettings appSettings, AppMode appMode, Consumer<AppSettings> onApply) {
        super(Runtime.getAppNameOrDefault() + " - Launcher");
        this.appSettings = appSettings;
        this.onApply = onApply;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(Dialogs.instance().getAppImageList());
        
        mainPanel = new JPanel();
        getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridLayout(0, 2));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel settingsPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsPanel.setLayout(new TableLayout(2, gbc));
        settingsPanel.setBorder(new TitledBorder("Graphics Settings"));
        centerPanel.add(settingsPanel);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new TitledBorder("Informations"));
        centerPanel.add(infoPanel);
        
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
        
        JLabel lblDisplayMode = new JLabel("Display Mode: ");
        settingsPanel.add(lblDisplayMode);
        List<String> displayModes = new ArrayList<>();
        displayModes.add("< No change >");
        for (GfxDisplayMode mode : getAvailableDisplayModes()) {
            displayModes.add(mode.toString());
        }
        JComboBox<String> cbDisplayMode = new JComboBox<>(displayModes.toArray(String[]::new));
        cbDisplayMode.setEnabled(false);
        settingsPanel.add(cbDisplayMode);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(8), gbc);

        JLabel lblQuality = new JLabel("Quality: ");
        settingsPanel.add(lblQuality);
        DoubleProperty qualityProperty = DoubleProperty.create(
                this::getQuality, this::setQuality,
                this::addSettingChangeListener, this::removeSettingChangeListener);
        DoubleInput diQuality = new DoubleInput(qualityProperty, "Quality_", 0.0, 1.0, 0.1);
        settingsPanel.add(diQuality);
        
        JLabel lblSize = new JLabel("Size (% of screen): ");
        settingsPanel.add(lblSize);
        DoubleProperty sizeProperty = DoubleProperty.create(
                this::getSizePercent, this::setSizePercent,
                this::addSettingChangeListener, this::removeSettingChangeListener);
        DoubleInput diSize = new DoubleInput(sizeProperty, "Size_", 0.0, 1.0, 0.1);
        settingsPanel.add(diSize);
        
        JLabel lblScale = new JLabel("Resulting Scaling: ");
        settingsPanel.add(lblScale);
        JLabel txtScale = new JLabel("<html><body>Inner <b>4</b>, Outer <b>1</b></body></html>");
        settingsPanel.add(txtScale);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(15), gbc);

        JLabel lblPipeline = new JLabel("Java2D Pipeline: ");
        settingsPanel.add(lblPipeline);
        JComboBox<GfxPipelineType> cbPipeline = new JComboBox<>(GfxPipelineType.getPlatformSupportedTypes().toArray(GfxPipelineType[]::new));
        settingsPanel.add(cbPipeline);
        
        JLabel lblVsync = new JLabel("V-Sync Mode: ");
        settingsPanel.add(lblVsync);
        JComboBox<VsyncType> cbVsync = new JComboBox<>(VsyncType.values());
        settingsPanel.add(cbVsync);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(15), gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        JCheckBox chkDoNotAskAgain = new JCheckBox("Use by default and do not ask again");
        settingsPanel.add(chkDoNotAskAgain, gbc);

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
    
    private List<GfxDisplayMode> getAvailableDisplayModes() {
        List<GfxDisplayMode> result = new ArrayList<>();
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gDev : gEnv.getScreenDevices()) {
            for (DisplayMode displayMode : gDev.getDisplayModes()) {
                if (displayMode.getWidth() >= 320 && displayMode.getHeight() >= 240) {
                    result.add(GfxDisplayMode.from(displayMode));
                }
            }
        }
        return result;
    }
    
    private double getQuality() {
        return quality;
    }
    
    private void setQuality(double quality) {
        this.quality = quality;
    }
    
    private double getSizePercent() {
        return sizePercent;
    }
    
    private void setSizePercent(double sizePercent) {
        this.sizePercent = sizePercent;
    }

    private void addSettingChangeListener(Runnable listener) {
        settingChangeListeners.add(listener);
    }
    
    private void removeSettingChangeListener(Runnable listener) {
        settingChangeListeners.remove(listener);
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
