package ch.chaos.library.utils.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import ch.chaos.library.utils.ILabelDescription;


public class ComboBoxTooltipRenderer<T> extends DefaultListCellRenderer {

    private final T[] values;


    public ComboBoxTooltipRenderer(T[] values) {
        this.values = values;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ILabelDescription labelDescr)
            value = labelDescr.getLabel();
        String valueStr = String.valueOf(value);
        if (GuiUtils.isWindowsLaf())
            valueStr = " " + valueStr + " ";
        JComponent item = (JComponent) super.getListCellRendererComponent(list, valueStr, index, isSelected, cellHasFocus);
        if (index >= 0) {
            if (values[index] instanceof ILabelDescription labelDescr)
                item.setToolTipText(labelDescr.getDescription());
            else
                item.setToolTipText("TODO: tooltip for " + values[index].toString());
        }
        return item;
    }

}