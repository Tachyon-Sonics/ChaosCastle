package ch.chaos.library.utils.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import ch.chaos.library.utils.DoubleProperty;

public class DoubleInput extends JPanel {

    public static final String SLIDER = "Slider";
    public static final String SPINNER = "Spinner";

    private final double minValue;
    private final double maxValue;

    private final DoubleProperty property;
    private JSlider slider;
    private JSpinner spinner;

    private double curValue;
    private boolean isAdjusting = false;


    public DoubleInput(DoubleProperty property, String namePrefix, double minValue, double maxValue, double tickSize) {
        this.property = property;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.curValue = property.getValue();
        property.addListener(this::propertyUpdated);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        setLayout(new TableLayout(2, gbc));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        slider = new JSlider(JSlider.HORIZONTAL, toSlider(minValue), toSlider(maxValue), toSlider(curValue)) {

            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                return new Dimension(dimension.width / 2, dimension.height);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

        };
        slider.setName(namePrefix + SLIDER);
        add(slider);
        if (GuiUtils.isWindowsLaf())
            slider.setPaintTicks(true);
        slider.addChangeListener((ChangeEvent e) -> {
            if (isAdjusting)
                return;
            isAdjusting = true;
            sliderUpdated();
            isAdjusting = false;
        });
        slider.setFocusable(false);

        gbc.weightx = 0.0;
        spinner = new JSpinner();
        spinner.setName(namePrefix + SPINNER);
        add(spinner, gbc);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(curValue, minValue, maxValue, tickSize) {

            @Override
            public Object getNextValue() {
                double curValue = ((Number) super.getValue()).doubleValue(); // TODO (0) to audio code too
                double result = curValue + super.getStepSize().doubleValue();
                if (result > ((Number) getMaximum()).doubleValue())
                    result = ((Number) getMaximum()).doubleValue();
                return result;
            }

            @Override
            public Object getPreviousValue() {
                double curValue = ((Number) super.getValue()).doubleValue();
                double result = curValue - super.getStepSize().doubleValue();
                if (result < ((Number) getMinimum()).doubleValue())
                    result = ((Number) getMinimum()).doubleValue();
                return result;
            }

        };
        spinner.setModel(spinnerModel);
        final JFormattedTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        textField.setColumns(3);
        textField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("0.0")))); // Damn!
        SelectAllOnFocus.installSelectAllOnFocus(textField);
        spinner.addChangeListener((ChangeEvent e) -> {
            if (isAdjusting)
                return;
            isAdjusting = true;
            spinnerUpdated();
            isAdjusting = false;
        });
    }

    private void sliderUpdated() {
        curValue = fromSlider(slider.getValue());
        updateSpinner();
        updateSettings();
    }

    private void spinnerUpdated() {
        curValue = ((Number) spinner.getValue()).floatValue();
        updateSlider();
        updateSettings();
    }

    private void propertyUpdated() {
        curValue = property.getValue();
        updateSlider();
        updateSpinner();
    }

    private void updateSettings() {
        property.setValue(curValue);
    }

    private void updateSlider() {
        double boundedValue = curValue;
        if (boundedValue < minValue)
            boundedValue = minValue;
        else if (boundedValue > maxValue)
            boundedValue = maxValue;
        slider.setValue(toSlider(boundedValue));
    }

    private void updateSpinner() {
        spinner.setValue(curValue);
    }

    private int toSlider(double value) {
        return (int) (1000.0 * (value - minValue) / (maxValue - minValue) + 0.5);
    }

    private double fromSlider(int value) {
        return (double) value * (maxValue - minValue) / 1000.0 + minValue;
    }

    public JSlider getSlider() {
        return slider;
    }

    public JSpinner getSpinner() {
        return spinner;
    }

}
