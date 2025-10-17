package ch.chaos.castle;

import java.lang.Runnable;

import ch.chaos.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DialogTest {

    // Imports
    private final Checks checks = Checks.instance();
    private final Dialogs dialogs = Dialogs.instance();
    private final Input input = Input.instance();
    private final Memory memory = Memory.instance();
    private final Terminal terminal = Terminal.instance();


    // VAR

    private Dialogs.GadgetPtr dialog;
    private Dialogs.GadgetPtr button;
    private Dialogs.GadgetPtr bool;
    private Dialogs.GadgetPtr cycle;
    private Dialogs.GadgetPtr group;
    private Dialogs.GadgetPtr _switch;
    private Dialogs.GadgetPtr switch1;
    private Dialogs.GadgetPtr switch2;
    private Dialogs.GadgetPtr progress;
    private Dialogs.GadgetPtr cb;
    private Dialogs.GadgetPtr scroller;
    private Dialogs.GadgetPtr slider;
    private Dialogs.GadgetPtr te;
    private Dialogs.GadgetPtr _int;
    private Dialogs.GadgetPtr label;
    private Input.Event event = new Input.Event();
    private int fill;
    private long[] tags = new long[21];
    private String buffer = "";
    private int dummy;


    public Dialogs.GadgetPtr getDialog() {
        return this.dialog;
    }

    public void setDialog(Dialogs.GadgetPtr dialog) {
        this.dialog = dialog;
    }

    public Dialogs.GadgetPtr getButton() {
        return this.button;
    }

    public void setButton(Dialogs.GadgetPtr button) {
        this.button = button;
    }

    public Dialogs.GadgetPtr getBool() {
        return this.bool;
    }

    public void setBool(Dialogs.GadgetPtr bool) {
        this.bool = bool;
    }

    public Dialogs.GadgetPtr getCycle() {
        return this.cycle;
    }

    public void setCycle(Dialogs.GadgetPtr cycle) {
        this.cycle = cycle;
    }

    public Dialogs.GadgetPtr getGroup() {
        return this.group;
    }

    public void setGroup(Dialogs.GadgetPtr group) {
        this.group = group;
    }

    public Dialogs.GadgetPtr getSwitch() {
        return this._switch;
    }

    public void setSwitch(Dialogs.GadgetPtr _switch) {
        this._switch = _switch;
    }

    public Dialogs.GadgetPtr getSwitch1() {
        return this.switch1;
    }

    public void setSwitch1(Dialogs.GadgetPtr switch1) {
        this.switch1 = switch1;
    }

    public Dialogs.GadgetPtr getSwitch2() {
        return this.switch2;
    }

    public void setSwitch2(Dialogs.GadgetPtr switch2) {
        this.switch2 = switch2;
    }

    public Dialogs.GadgetPtr getProgress() {
        return this.progress;
    }

    public void setProgress(Dialogs.GadgetPtr progress) {
        this.progress = progress;
    }

    public Dialogs.GadgetPtr getCb() {
        return this.cb;
    }

    public void setCb(Dialogs.GadgetPtr cb) {
        this.cb = cb;
    }

    public Dialogs.GadgetPtr getScroller() {
        return this.scroller;
    }

    public void setScroller(Dialogs.GadgetPtr scroller) {
        this.scroller = scroller;
    }

    public Dialogs.GadgetPtr getSlider() {
        return this.slider;
    }

    public void setSlider(Dialogs.GadgetPtr slider) {
        this.slider = slider;
    }

    public Dialogs.GadgetPtr getTe() {
        return this.te;
    }

    public void setTe(Dialogs.GadgetPtr te) {
        this.te = te;
    }

    public Dialogs.GadgetPtr getInt() {
        return this._int;
    }

    public void setInt(Dialogs.GadgetPtr _int) {
        this._int = _int;
    }

    public Dialogs.GadgetPtr getLabel() {
        return this.label;
    }

    public void setLabel(Dialogs.GadgetPtr label) {
        this.label = label;
    }

    public Input.Event getEvent() {
        return this.event;
    }

    public void setEvent(Input.Event event) {
        this.event = event;
    }

    public int getFill() {
        return this.fill;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public long[] getTags() {
        return this.tags;
    }

    public void setTags(long[] tags) {
        this.tags = tags;
    }

    public String getBuffer() {
        return this.buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public int getDummy() {
        return this.dummy;
    }

    public void setDummy(int dummy) {
        this.dummy = dummy;
    }


    // PROCEDURE

    private void Close() {
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(this::getDialog, this::setDialog));
    }

    private final Runnable Close_ref = this::Close;


    // Life Cycle

    private void begin() {
        Memory.instance().begin();
        Dialogs.instance().begin();
        Input.instance().begin();
        Checks.instance().begin();
        Terminal.instance().begin();

        checks.AddTermProc(Close_ref);
        terminal.WriteString("Creating Dialog");
        terminal.WriteLn();
        dialog = dialogs.AllocGadget(Dialogs.dDialog);
        if (dialog == null)
            throw new HaltException();
        dialogs.ModifyGadget(dialog, (Memory.TagItem) memory.TAG3(Dialogs.dFLAGS, Dialogs.dfCLOSE + Dialogs.dfVDIR + Dialogs.dfSCROLLY + Dialogs.dfSIZE + Dialogs.dfDOWNEVENT, Dialogs.dHEIGHT, 50, Dialogs.dTEXT, "Dialog"));
        group = dialog;
        terminal.WriteString("Creating Button");
        terminal.WriteLn();
        button = dialogs.AllocGadget(Dialogs.dButton);
        if (button == null)
            throw new HaltException();
        dialogs.ModifyGadget(button, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Button", Dialogs.dFLAGS, Dialogs.dfDOWNEVENT));
        dialogs.AddGadget(group, button, dialogs.noGadget);
        bool = dialogs.CreateGadget(Dialogs.dBool, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Bool", Dialogs.dFLAGS, Dialogs.dfAUTOLEFT + Dialogs.dfAUTORIGHT));
        if (bool == null)
            throw new HaltException();
        dialogs.AddGadget(group, bool, dialogs.noGadget);
        cycle = dialogs.AddNewGadget(group, Dialogs.dCycle, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Cycle", Dialogs.dFLAGS, Dialogs.dfAUTOUP + Dialogs.dfAUTODOWN));
        if (cycle == null)
            throw new HaltException();
        _switch = dialogs.AddNewGadget(group, Dialogs.dSwitch, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Switch", Dialogs.dFLAGS, Dialogs.dfAUTOLEFT + Dialogs.dfAUTOUP + Dialogs.dfAUTODOWN));
        if (_switch == null)
            throw new HaltException();
        switch1 = dialogs.AddNewGadget(group, Dialogs.dSwitch, (Memory.TagItem) memory.TAG3(Dialogs.dTEXT, "Sub-Switch 1", Dialogs.dFLAGS, Dialogs.dfAUTOLEFT + Dialogs.dfAUTOUP + Dialogs.dfAUTODOWN, Dialogs.dFILL, 1));
        if (switch1 == null)
            throw new HaltException();
        switch2 = dialogs.AddNewGadget(group, Dialogs.dSwitch, (Memory.TagItem) memory.TAG3(Dialogs.dTEXT, "Sub-Switch 2", Dialogs.dFLAGS, Dialogs.dfAUTOLEFT + Dialogs.dfAUTOUP + Dialogs.dfAUTODOWN, Dialogs.dFILL, 1));
        if (switch2 == null)
            throw new HaltException();
        progress = dialogs.AddNewGadget(group, Dialogs.dProgress, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Progress", Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
        if (progress == null)
            throw new HaltException();
        cb = dialogs.AddNewGadget(group, Dialogs.dCheckbox, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, "Checkbox", Dialogs.dFLAGS, Dialogs.dfAUTOLEFT));
        if (cb == null)
            throw new HaltException();
        scroller = dialogs.AddNewGadget(group, Dialogs.dScroller, null);
        if (scroller == null)
            throw new HaltException();
        slider = dialogs.AddNewGadget(group, Dialogs.dSlider, null);
        if (slider == null)
            throw new HaltException();
        buffer = "Text Edit";
        te = dialogs.AddNewGadget(group, Dialogs.dTextEdit, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, new Runtime.FieldRef<>(this::getBuffer, this::setBuffer), Dialogs.dTXTLEN, 20));
        if (te == null)
            throw new HaltException();
        _int = dialogs.AddNewGadget(group, Dialogs.dIntEdit, (Memory.TagItem) memory.TAG1(Dialogs.dINTVAL, -142857));
        if (_int == null)
            throw new HaltException();
        label = dialogs.AddNewGadget(group, Dialogs.dLabel, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, "Label"));
        if (label == null)
            throw new HaltException();
        if (dialogs.RefreshGadget(dialog) != Dialogs.DialogOk)
            throw new HaltException();
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eKEYBOARD, Input.eGADGET));
        while (true) {
            input.WaitEvent();
            input.GetEvent(event);
            if (event.type == Input.eGADGET) {
                terminal.WriteString("Gadget Event: ");
                terminal.WriteInt((long) event.gadget, -1);
                terminal.WriteLn();
                if ((event.gadget == dialog) && (event.gadgetUp))
                    break;
            } else if ((event.type == Input.eKEYBOARD)) {
                terminal.WriteString("Keyboard");
                terminal.WriteLn();
                if (event.ch == 'r') {
                    if (dialogs.RefreshGadget(dialog) != Dialogs.DialogOk)
                        throw new HaltException();
                }
                if (event.ch == '+') {
                    dialogs.ModifyGadget(scroller, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfACTIVE));
                } else if (event.ch == '-') {
                    dialogs.ModifyGadget(scroller, (Memory.TagItem) memory.TAG1(Dialogs.dRFLAGS, Dialogs.dfACTIVE));
                } else if (event.ch == '*') {
                    dialogs.ModifyGadget(cycle, (Memory.TagItem) memory.TAG1(Dialogs.dFLAGS, Dialogs.dfSELECT));
                } else if (event.ch == '/') {
                    dialogs.ModifyGadget(cycle, (Memory.TagItem) memory.TAG1(Dialogs.dRFLAGS, Dialogs.dfSELECT));
                } else if (event.ch == 't') {
                    dialogs.ModifyGadget(cycle, (Memory.TagItem) memory.TAG1(Dialogs.dTEXT, "----- New Cycle -----"));
                    dummy = (int) dialogs.RefreshGadget(dialog);
                }
                if (event.ch == 'f') {
                    if (fill == 65535)
                        fill = 0;
                    else
                        fill += 4369;
                    dialogs.ModifyGadget(progress, (Memory.TagItem) memory.TAG1(Dialogs.dFILL, fill));
                }
                if (event.ch == 'h')
                    throw new HaltException();
                if (event.ch == 'q')
                    break;
            }
        }
        Close();
    }

    private void close() {
        Terminal.instance().close();
        Checks.instance().close();
        Input.instance().close();
        Dialogs.instance().close();
        Memory.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        DialogTest instance = new DialogTest();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}
