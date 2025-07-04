package ch.chaos.library.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
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
import ch.chaos.library.graphics.xbrz.XbrzHelper;
import ch.chaos.library.settings.AppMode;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.settings.GfxDisplayMode;
import ch.chaos.library.settings.GfxPipelineType;
import ch.chaos.library.settings.SettingsStore;
import ch.chaos.library.settings.VsyncType;
import ch.chaos.library.utils.DoubleProperty;
import ch.chaos.library.utils.Platform;
import ch.chaos.library.utils.gui.DoubleInput;
import ch.chaos.library.utils.gui.Duplexer;
import ch.chaos.library.utils.gui.TableLayout;
import ch.pitchtech.modula.runtime.Runtime;


public class LauncherFrame extends JFrame { // TODO (0) add small "Reset" button, informations
    
    private final AppSettings appSettings;
    private final AppMode appMode;
    private final Consumer<AppSettings> onApply;
    
    private JPanel mainPanel;
    private JRadioButton rbWindow;
    private JRadioButton rbFullScreen;
    private JComboBox<String> cbDisplayMode;
    private JLabel txtScale;
    private JComboBox<GfxPipelineType> cbPipeline;
    private JComboBox<VsyncType> cbVsync;
    private JCheckBox chkDoNotAskAgain;
    
    private List<GfxDisplayMode> availableDisplayModes;
    
    private double quality;
    private double sizePercent = 100.0;
    private List<Runnable> settingChangeListeners = new ArrayList<>();
    private final Duplexer dpx = new Duplexer();
    
    
    public LauncherFrame(AppSettings appSettings, AppMode appMode, Consumer<AppSettings> onApply) {
        super(Runtime.getAppNameOrDefault() + " - Launcher");
        this.appSettings = appSettings;
        this.appMode = appMode;
        this.onApply = onApply;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(Dialogs.instance().getAppImageList());
        
        create();
        dpx.receive(this::applySettingsToGui);
    }

    public void create() {
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
        rbWindow = new JRadioButton("Window");
        grpMode.add(rbWindow);
        pnlMode.add(rbWindow);
        rbWindow.addActionListener(this::settingChangedInGui);
        rbFullScreen = new JRadioButton("Full Screen");
        grpMode.add(rbFullScreen);
        pnlMode.add(rbFullScreen);
        rbFullScreen.addActionListener(this::settingChangedInGui);
        
        JLabel lblDisplayMode = new JLabel("Display Mode: ");
        settingsPanel.add(lblDisplayMode);
        List<String> displayModes = new ArrayList<>();
        displayModes.add("< No change >");
        for (GfxDisplayMode mode : getAvailableDisplayModes()) {
            displayModes.add(mode.toDisplayString());
        }
        cbDisplayMode = new JComboBox<>(displayModes.toArray(String[]::new));
        cbDisplayMode.addActionListener(this::settingChangedInGui);
        settingsPanel.add(cbDisplayMode);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(8), gbc);

        JLabel lblQuality = new JLabel("Quality: ");
        settingsPanel.add(lblQuality);
        DoubleProperty qualityProperty = DoubleProperty.create(
                this::getQuality, this::setQuality,
                this::addSettingChangeListener, this::removeSettingChangeListener);
        DoubleInput diQuality = new DoubleInput(qualityProperty, "Quality_", 0.0, 1.0, 0.1, "0.0");
        settingsPanel.add(diQuality);
        
        JLabel lblSize = new JLabel("Size (% of screen): ");
        settingsPanel.add(lblSize);
        DoubleProperty sizeProperty = DoubleProperty.create(
                this::getSizePercent, this::setSizePercent,
                this::addSettingChangeListener, this::removeSettingChangeListener);
        DoubleInput diSize = new DoubleInput(sizeProperty, "Size_", 20.0, 100.0, 10.0, "#00");
        settingsPanel.add(diSize);
        
        JLabel lblScale = new JLabel("Resulting Scalings: ");
        settingsPanel.add(lblScale);
        txtScale = new JLabel("<html><body>Inner <b>?</b>, Outer <b>?</b></body></html>");
        settingsPanel.add(txtScale);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(15), gbc);

        JLabel lblPipeline = new JLabel("Java2D Pipeline: ");
        settingsPanel.add(lblPipeline);
        cbPipeline = new JComboBox<>(GfxPipelineType.getPlatformSupportedTypes().toArray(GfxPipelineType[]::new));
        cbPipeline.addActionListener(this::settingChangedInGui);
        settingsPanel.add(cbPipeline);
        
        JLabel lblVsync = new JLabel("V-Sync Mode: ");
        settingsPanel.add(lblVsync);
        cbVsync = new JComboBox<>(VsyncType.values());
        cbVsync.addActionListener(this::settingChangedInGui);
        settingsPanel.add(cbVsync);
        
        gbc.gridwidth = 2;
        settingsPanel.add(Box.createVerticalStrut(15), gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        chkDoNotAskAgain = new JCheckBox("Use by default and do not ask again");
        chkDoNotAskAgain.addActionListener(this::settingChangedInGui);
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
    
    private void applySettingsToGui() {
        rbWindow.setSelected(!appMode.isFullScreen());
        rbFullScreen.setSelected(appMode.isFullScreen());
        if (appMode.isFullScreen()) {
            cbDisplayMode.setEnabled(true);
            List<GfxDisplayMode> displayModes = getAvailableDisplayModes();
            int index = displayModes.indexOf(appMode.getDisplayMode());
            cbDisplayMode.setSelectedIndex(index + 1);
        } else {
            cbDisplayMode.setSelectedIndex(0);
            cbDisplayMode.setEnabled(false);
        }
        
        Dimension screenSize = getSelectedScreenSize();
        int fullScale = Math.min(screenSize.width / AppMode.BASE_WIDTH, screenSize.height / AppMode.BASE_HEIGHT);
        fullScale = XbrzHelper.getNearestScale(fullScale);
        this.sizePercent = (double) (appMode.getInnerScale() * appMode.getOuterScale()) / (double) fullScale * 100.0;
        this.quality = Math.log(appMode.getInnerScale()) / Math.log(appMode.getOuterScale() * appMode.getInnerScale());
        
        cbPipeline.setSelectedItem(appMode.getGfxPipeline());
        cbVsync.setSelectedItem(appMode.getVsyncType());
        chkDoNotAskAgain.setSelected(appMode.isDoNotAskAgain());
        
        applyGuiToGuiChanges();
        for (Runnable listener : settingChangeListeners)
            listener.run();
    }
    
    private void settingChangedInGui(ActionEvent e) {
        dpx.send(this::updateSettingsFromGui);
    }
    
    private void updateSettingsFromGui() {
        boolean prevFullScreen = appMode.isFullScreen();
        appMode.setFullScreen(rbFullScreen.isSelected());
        if (appMode.isFullScreen() && prevFullScreen) {
            List<GfxDisplayMode> displayModes = getAvailableDisplayModes();
            int index = cbDisplayMode.getSelectedIndex();
            if (index == 0) {
                appMode.setDisplayMode(null);
            } else {
                appMode.setDisplayMode(displayModes.get(index - 1));
            }
        }
        
        // Scales
        updateScale();
        
        appMode.setGfxPipeline(cbPipeline.getItemAt(cbPipeline.getSelectedIndex()));
        appMode.setVsyncType(cbVsync.getItemAt(cbVsync.getSelectedIndex()));
        appMode.setDoNotAskAgain(chkDoNotAskAgain.isSelected());
        
        applyGuiToGuiChanges();
//        System.out.println(appMode);
    }
    
    private void updateScale() {
        Dimension screenSize = getSelectedScreenSize();
        screenSize = new Dimension(
                Math.max(AppMode.BASE_WIDTH, (int) (screenSize.width * sizePercent / 100.0 + 0.5)),
                Math.max(AppMode.BASE_HEIGHT, (int) (screenSize.height * sizePercent / 100.0 + 0.5)));
        
        int frameScale = Math.min(screenSize.width / AppMode.BASE_WIDTH, screenSize.height / AppMode.BASE_HEIGHT);
        int innerScale = 1;
        int steps = (int) (Math.exp(quality * Math.log(frameScale)) + 0.5);
        if (steps < 1)
            steps = 1;
        while (frameScale % steps != 0 && steps > 1)
            steps--;
        frameScale /= steps;
        innerScale *= steps;
        // Xbrz does not support all scaling factors. Round to nearest
        innerScale = XbrzHelper.getNearestScale(innerScale);
        
        appMode.setInnerScale(innerScale);
        appMode.setOuterScale(frameScale);
    }

    /**
     * Get screen size according to current {@link AppMode}, but without applying
     * scalings or quality / sizePercent
     */
    private Dimension getSelectedScreenSize() {
        Dimension screenSize;
        if (appMode.isFullScreen() && appMode.getDisplayMode() != null) {
            GfxDisplayMode displayMode = appMode.getDisplayMode();
            screenSize = new Dimension(displayMode.width(), displayMode.height());
        } else if (appMode.isFullScreen()) {
            GfxDisplayMode displayMode = GfxDisplayMode.current();
            screenSize = new Dimension(displayMode.width(), displayMode.height());
        } else {
            screenSize = getMaxWindowedSize();
        }
        return screenSize;
    }
    
    private Dimension getMaxWindowedSize() {
        Insets frameInsets = this.getInsets();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        AffineTransform screenTransform = graphicsConfiguration.getDefaultTransform();
        double scaleX = screenTransform.getScaleX();
        double scaleY = screenTransform.getScaleY();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        int availWidth = screenSize.width - screenInsets.left - screenInsets.right - frameInsets.left - frameInsets.right;
        int availHeight = screenSize.height - screenInsets.top - screenInsets.bottom - frameInsets.top - frameInsets.bottom;
        int screenWidth = (int) (availWidth * scaleX);
        int screenHeight = (int) (availHeight * scaleY);
        return new Dimension(screenWidth, screenHeight);
    }
    
    private void applyGuiToGuiChanges() {
        if (appMode.isFullScreen()) {
            cbDisplayMode.setEnabled(true);
            List<GfxDisplayMode> displayModes = getAvailableDisplayModes();
            int index = displayModes.indexOf(appMode.getDisplayMode());
            cbDisplayMode.setSelectedIndex(index + 1);
        } else {
            cbDisplayMode.setSelectedIndex(0);
            cbDisplayMode.setEnabled(false);
        }
        txtScale.setText("<html><body>Inner <b>" + appMode.getInnerScale() + "</b>,"
                + " Outer <b>" + appMode.getOuterScale() + "</b></body></html>");
    }
    
    private List<GfxDisplayMode> getAvailableDisplayModes() {
        if (availableDisplayModes == null) {
            availableDisplayModes = new ArrayList<>();
            GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (GraphicsDevice gDev : gEnv.getScreenDevices()) {
                for (DisplayMode displayMode : gDev.getDisplayModes()) {
                    if (displayMode.getWidth() >= 320 && displayMode.getHeight() >= 240) {
                        GfxDisplayMode gfxDisplayMode = GfxDisplayMode.from(displayMode);
                        if (!availableDisplayModes.contains(gfxDisplayMode))
                            availableDisplayModes.add(gfxDisplayMode);
                    }
                }
            }
        }
        return availableDisplayModes;
    }
    
    private double getQuality() {
        return quality;
    }
    
    private void setQuality(double quality) {
        this.quality = quality;
        settingChangedInGui(null);
    }
    
    private double getSizePercent() {
        return sizePercent;
    }
    
    private void setSizePercent(double sizePercent) {
        this.sizePercent = sizePercent;
        settingChangedInGui(null);
    }

    private void addSettingChangeListener(Runnable listener) {
        settingChangeListeners.add(listener);
    }
    
    private void removeSettingChangeListener(Runnable listener) {
        settingChangeListeners.remove(listener);
    }
    
    private void start(ActionEvent e) {
        try {
            SettingsStore.saveSettings(appSettings);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setVisible(false);
        dispose();
        onApply.accept(appSettings);
    }
    
    private void cancel(ActionEvent e) {
        setVisible(false);
        dispose();
    }

}
