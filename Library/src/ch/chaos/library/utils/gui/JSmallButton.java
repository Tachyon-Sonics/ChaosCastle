package ch.chaos.library.utils.gui;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;


public class JSmallButton extends JButton {

    private boolean manual = true;


    public JSmallButton() {
        super();
        init();
    }

    public JSmallButton(Action a) {
        super(a);
        init();
    }

    public JSmallButton(Icon icon) {
        super(icon);
        init();
    }

    public JSmallButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    public JSmallButton(String text) {
        super(text);
        init();
    }

    private void init() {
        if (GuiUtils.isAppleLaf()) {
            putClientProperty("JComponent.sizeVariant", "small");
            manual = false;
        }
    }

    @Override
    public Insets getInsets() {
        Insets i = super.getInsets();
        if (!manual)
            return i;
        return new Insets((i.top + 1) / 2, (i.left + 1) / 2, (i.bottom + 1) / 2, (i.right + 1) / 2);
    }

    @Override
    public Insets getInsets(Insets insets) {
        Insets i = super.getInsets(insets);
        if (!manual)
            return i;
        insets.set((i.top + 1) / 2, (i.left + 1) / 2, (i.bottom + 1) / 2, (i.right + 1) / 2);
        return insets;
    }

    @Override
    public Font getFont() {
        Font base = super.getFont();
        if (base == null || !manual)
            return base;
        return base.deriveFont(base.getSize2D() * 0.95f);
    }

}
