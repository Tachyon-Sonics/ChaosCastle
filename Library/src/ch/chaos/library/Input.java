package ch.chaos.library;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.RangeSet;

public class Input {

    private static Input instance;


    private Input() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Input instance() {
        if (instance == null)
            new Input(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int LeftButton = 0;
    public static final int RightButton = 1;
    public static final int MiddleButton = 2;
    public static final int SysEvent = 3;
    public static final int eNUL = 0;
    public static final int eKEYBOARD = 1;
    public static final int eMOUSE = 2;
    public static final int eMENU = 4;
    public static final int eGADGET = 5;
    public static final int eREFRESH = 8;
    public static final int eSYS = 9; // TODO implement "Quit" / "Kill", when closing window, and using Desktop
    public static final int eTIMER = 12;
    public static final int JoyLeft = 3;
    public static final int JoyRight = 2;
    public static final int JoyUp = 0;
    public static final int JoyDown = 1;
    public static final int Joy1 = 4;
    public static final int Joy2 = 5;
    public static final int Joy3 = 6;
    public static final int Joy4 = 7;
    public static final int Joy5 = 8;
    public static final int Joy6 = 9;
    public static final int Joy7 = 10;
    public static final int Joy8 = 11;
    public static final int JoyPause = 12;
    public static final int JoyReverse = 13;
    public static final int JoyForward = 14;
    public static final int JoyShift = 15;
    public static final int statWaiting = 0;
    public static final int statReady = 1;
    public static final int statWorking = 2;
    public static final int statBusy = 3;

    // TYPE


    public static enum SysMsg {
        pActivate,
        pDeactivate,
        pQuit,
        pKill;
    }

    public static enum Modifiers {
        emSHIFT,
        emCTRL,
        emALT,
        emCOMMAND,
        emDOUBLE;
    }


    public static final Runtime.Range EventTypes = new Runtime.Range(eNUL, eTIMER);


    public static class Event { // RECORD

        public int type;
        // CASE "type" {
        public char ch; // eKEYBOARD
        public short button; // eMOUSE
        public boolean mouseUp; // eMOUSE
        public short mx; // eMOUSE
        public short my; // eMOUSE
        public Object menu; // eMENU
        public Object gadget; // eGADGET
        public short bNum; // eGADGET
        public boolean gadgetUp; // eGADGET
        public Object refreshGadget; // eREFRESH
        public SysMsg msg; // eSYS
        // }
        public Object dialog;
        public EnumSet<Modifiers> modifiers = EnumSet.noneOf(Modifiers.class);


        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public char getCh() {
            return this.ch;
        }

        public void setCh(char ch) {
            this.ch = ch;
        }

        public short getButton() {
            return this.button;
        }

        public void setButton(short button) {
            this.button = button;
        }

        public boolean isMouseUp() {
            return this.mouseUp;
        }

        public void setMouseUp(boolean mouseUp) {
            this.mouseUp = mouseUp;
        }

        public short getMx() {
            return this.mx;
        }

        public void setMx(short mx) {
            this.mx = mx;
        }

        public short getMy() {
            return this.my;
        }

        public void setMy(short my) {
            this.my = my;
        }

        public Object getMenu() {
            return this.menu;
        }

        public void setMenu(Object menu) {
            this.menu = menu;
        }

        public Object getGadget() {
            return this.gadget;
        }

        public void setGadget(Object gadget) {
            this.gadget = gadget;
        }

        public short getBNum() {
            return this.bNum;
        }

        public void setBNum(short bNum) {
            this.bNum = bNum;
        }

        public boolean isGadgetUp() {
            return this.gadgetUp;
        }

        public void setGadgetUp(boolean gadgetUp) {
            this.gadgetUp = gadgetUp;
        }

        public Object getRefreshGadget() {
            return this.refreshGadget;
        }

        public void setRefreshGadget(Object refreshGadget) {
            this.refreshGadget = refreshGadget;
        }

        public SysMsg getMsg() {
            return this.msg;
        }

        public void setMsg(SysMsg msg) {
            this.msg = msg;
        }

        public Object getDialog() {
            return this.dialog;
        }

        public void setDialog(Object dialog) {
            this.dialog = dialog;
        }

        public EnumSet<Modifiers> getModifiers() {
            return this.modifiers;
        }

        public void setModifiers(EnumSet<Modifiers> modifiers) {
            this.modifiers = modifiers;
        }

        public void copyFrom(Event other) {
            this.type = other.type;
            this.ch = other.ch;
            this.button = other.button;
            this.mouseUp = other.mouseUp;
            this.mx = other.mx;
            this.my = other.my;
            this.menu = other.menu;
            this.gadget = other.gadget;
            this.bNum = other.bNum;
            this.gadgetUp = other.gadgetUp;
            this.refreshGadget = other.refreshGadget;
            this.msg = other.msg;
            this.dialog = other.dialog;
            this.modifiers = EnumSet.copyOf(other.modifiers);
        }

        public Event newCopy() {
            Event copy = new Event();
            copy.copyFrom(this);
            return copy;
        }

    }

    // IMPL


    private final BitSet currentEventTypes = new BitSet();
    private final Queue<Event> eventQueue = new LinkedList<>();

    private JFrame mainFrame;
    private BitSet currentStick = new BitSet();
    private int mouseX = 0;
    private int mouseY = 0;
    private ControllerManager controllers;

    private Cursor blankCursor;
    private short busyStat;


    public void registerMainFrame(JFrame frame) {
        this.mainFrame = frame;
        frame.addMouseListener(new MouseListenerImpl());
        frame.addMouseMotionListener(new MouseListenerImpl());
        frame.addKeyListener(new KeyListenerImpl());
        SetBusyStat(busyStat);
    }

    void queueEvent(Event event) {
        synchronized (eventQueue) {
            eventQueue.add(event);
            eventQueue.notify();
        }
    }

    private void notifyStickChange() {
        Event dummy = new Event();
        dummy.setType(eNUL);
        queueEvent(dummy);
    }


    class MouseListenerImpl extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            mouseClick(e, false);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseClick(e, true);
        }

        private void mouseClick(MouseEvent e, boolean released) {
            if (currentEventTypes.get(eMOUSE)) {
                Event event = new Event();
                event.type = eMOUSE;
                event.button = (short) e.getButton();
                event.mouseUp = released;
                event.mx = (short) Graphics.unscale(e.getX());
                event.my = (short) Graphics.unscale(e.getY());
                queueEvent(event);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int newX = Graphics.unscale(e.getX());
            int newY = Graphics.unscale(e.getY());
            mouseX = newX;
            mouseY = newY;
        }

    }

    class KeyListenerImpl extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (handleStickKeys(e, true))
                return;
            if (currentEventTypes.get(eKEYBOARD)) {
                char ch = e.getKeyChar();
                if (ch != KeyEvent.CHAR_UNDEFINED) {
                    Event event = new Event();
                    event.type = eKEYBOARD;
                    event.ch = ch;
                    queueEvent(event);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            handleStickKeys(e, false);
        }

        private boolean handleStickKeys(KeyEvent e, boolean down) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
                stick(JoyLeft, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
                stick(JoyRight, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
                stick(JoyUp, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
                stick(JoyDown, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
                sticks(down, e, JoyDown, JoyLeft);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3) {
                sticks(down, e, JoyDown, JoyRight);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
                sticks(down, e, JoyUp, JoyLeft);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD9) {
                sticks(down, e, JoyUp, JoyRight);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD5) {
                // Pressing [5] will stop all directions. Safety for occasional missed keyup
                if (down) {
                    sticks(false, e, JoyUp, JoyDown, JoyLeft, JoyRight);
                    return true;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE
                    || e.getKeyCode() == KeyEvent.VK_CONTROL
                    || e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
                stick(Joy1, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP
                    || e.getKeyCode() == KeyEvent.VK_MINUS
                    || e.getKeyChar() == '-') {
                stick(JoyReverse, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                stick(Joy2, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                stick(Joy3, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                stick(Joy4, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F4) {
                stick(Joy5, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                stick(Joy6, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F6) {
                stick(Joy7, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_F7) {
                stick(Joy8, down, e);
                return true;
            } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN
                    || e.getKeyCode() == KeyEvent.VK_PLUS
                    || e.getKeyChar() == '+') {
                stick(JoyForward, down, e);
                return true;
            }
            return false;
        }

        private void stick(int value, boolean set, KeyEvent e) {
            if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                currentStick.set(JoyShift, set);
            }
            currentStick.set(value, set);
            notifyStickChange();
        }

        private void sticks(boolean set, KeyEvent e, int... values) {
            if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                currentStick.set(JoyShift, set);
            }
            for (int value : values)
                currentStick.set(value, set);
            notifyStickChange();
        }

    }


    public void AddEvents(Runtime.RangeSet types) {
        currentEventTypes.or(types.getTarget());
    }

    public void RemEvents(Runtime.RangeSet types) {
        currentEventTypes.andNot(types.getTarget());
    }

    public void WaitEvent() {
        synchronized (eventQueue) {
            BitSet prevGamePad = new BitSet();
            fillGamePadState(prevGamePad);
            BitSet nextGamePad = new BitSet();
            nextGamePad.or(prevGamePad);
            while (eventQueue.isEmpty() && prevGamePad.equals(nextGamePad)) {
                try {
                    eventQueue.wait(16);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                nextGamePad.clear();
                fillGamePadState(nextGamePad);
            }
        }
    }

    public void GetEvent(/* VAR */ Event e) {
        synchronized (eventQueue) {
            if (eventQueue.isEmpty()) {
                e.setType(eNUL);
                return;
            }
            Event event = eventQueue.remove();
            e.copyFrom(event);
        }
    }

    public void SendEvent(Event e) {
        queueEvent(e);
    }

    public void FlushEvents() {
        synchronized (eventQueue) {
            eventQueue.clear();
        }
    }

    public void BeginRefresh() {
        mainFrame.repaint();
    }

    public boolean EndRefresh() {
        return true;
    }

    public void SetBusyStat(short stat) {
        this.busyStat = stat;
        if (mainFrame == null)
            return;
        SwingUtilities.invokeLater(() -> {
            if (stat == statReady) { // No cursor
                mainFrame.setCursor(blankCursor);
            } else if (stat == statBusy) {
                mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            } else if (stat == statWaiting) { // Normal cursor (waiting for user input)
                mainFrame.setCursor(null);
            } else if (stat == statWorking) {
                // Seems not used. Could be the Windows' busy + arrow cursor
                mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
    }

    public boolean isReadyStat() {
        return (busyStat == statWaiting);
    }

    public void Beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    public void GetMouse(/* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y) {
        x.set((short) mouseX);
        y.set((short) mouseY);
    }

    public Runtime.RangeSet GetStick() {
        RangeSet result = new RangeSet(0, 15);

        // Add state from keyboard:
        result.getTarget().or(currentStick);

        // Add state from gamepad controller:
        BitSet target = result.getTarget();
        fillGamePadState(target);
        return result;
    }

    private void fillGamePadState(BitSet target) {
        if (controllers == null)
            return;
        ControllerState state = controllers.getState(0);
        if (mainFrame == null || !mainFrame.isFocused())
            return;

        addStickIf(state.dpadLeft || state.dpadLeftJustPressed, JoyLeft, target);
        addStickIf(state.dpadRight || state.dpadRightJustPressed, JoyRight, target);
        addStickIf(state.dpadUp || state.dpadUpJustPressed, JoyUp, target);
        addStickIf(state.dpadDown || state.dpadDownJustPressed, JoyDown, target);
        addStickIf(state.start, JoyPause, target);
        addStickIf(state.a || state.aJustPressed, Joy1, target);
        addStickIf(state.b || state.bJustPressed, Joy2, target);
        addStickIf(state.x || state.xJustPressed, Joy3, target);
        addStickIf(state.y || state.yJustPressed, Joy4, target);
        addStickIf(state.rb || state.rbJustPressed, JoyForward, target);
        addStickIf(state.lb || state.lbJustPressed, JoyReverse, target);
        addStickIf(state.leftStickClick || state.leftStickJustClicked, Joy5, target);
        addStickIf(state.rightStickClick || state.rightStickJustClicked, Joy6, target);
        if (Math.abs(state.leftStickX) >= 0.25f || Math.abs(state.leftStickY) >= 0.25f)
            target.set(Joy5);
        if (Math.abs(state.rightStickX) >= 0.25f || Math.abs(state.rightStickY) >= 0.25f)
            target.set(Joy6);
        if (Math.abs(state.leftTrigger) >= 0.25f)
            target.set(Joy7);
        if (Math.abs(state.rightTrigger) >= 0.25f)
            target.set(Joy8);
    }

    private void addStickIf(boolean condition, int value, BitSet target) {
        if (condition) {
            target.set(value);
        }
    }

    public void begin() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        try {
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
        } catch (Exception | UnsatisfiedLinkError ex) {
            ex.printStackTrace();
            System.out.println("Could not load Game Controller libraries. Game Controller disabled.");
            controllers = null;
        }
    }

    public void close() {

    }
}
