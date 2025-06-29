package ch.chaos.library;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.chaos.library.dialogs.ButtonGadget;
import ch.chaos.library.dialogs.CheckBoxGadget;
import ch.chaos.library.dialogs.CycleGadget;
import ch.chaos.library.dialogs.DialogGadget;
import ch.chaos.library.dialogs.Gadget;
import ch.chaos.library.dialogs.GroupGadget;
import ch.chaos.library.dialogs.LabelGadget;
import ch.chaos.library.dialogs.NumberGadget;
import ch.chaos.library.dialogs.ProgressGadget;
import ch.chaos.library.dialogs.ToggleGadget;
import ch.pitchtech.modula.runtime.Runtime;

public class Dialogs {

    private static Dialogs instance;


    private Dialogs() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Dialogs instance() {
        if (instance == null)
            new Dialogs(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int DialogOk = 0;
    public static final int DialogNoMem = -1;
    public static final int dDialog = 0;
    public static final int dGroup = 1;
    public static final int dButton = 10;
    public static final int dBool = 11;
    public static final int dCycle = 12;
    public static final int dChoice = 15;
    public static final int dCheckbox = 20;
    public static final int dRadio = 21;
    public static final int dSwitch = 22;
    public static final int dSlider = 30;
    public static final int dScroller = 31;
    public static final int dProgress = 35;
    public static final int dLabel = 40;
    public static final int dArea = 41;
    public static final int dImage = 42;
    public static final int dTextEdit = 50;
    public static final int dIntEdit = 51;
    public static final int dFLAGS = Memory.tagUser + 0;
    public static final int dRFLAGS = Memory.tagUser + 1;
    public static final int dMASK = Memory.tagUser + 2;
    public static final int dfACTIVE = 1;
    public static final int dbActive = 0;
    public static final int dfSELECT = 2;
    public static final int dbSelect = 1;
    public static final int dfBORDER = 8;
    public static final int dbBorder = 3;
    public static final int dfCLOSE = 8;
    public static final int dbClose = 3;
    public static final int dfDOWNEVENT = 16;
    public static final int dbDownEvent = 4;
    public static final int dfUPEVENT = 32;
    public static final int dbUpEvent = 5;
    public static final int dfAUTOLEFT = 64;
    public static final int dbAutoLeft = 6;
    public static final int dfAUTORIGHT = 128;
    public static final int dbAutoRight = 7;
    public static final int dfAUTOUP = 256;
    public static final int dbAutoUp = 8;
    public static final int dfAUTODOWN = 512;
    public static final int dbAutoDown = 9;
    public static final int dfAUTOWIDTH = dfAUTOLEFT + dfAUTORIGHT;
    public static final int dfAUTOHEIGHT = dfAUTOUP + dfAUTODOWN;
    public static final int dfJUSTIFY = dfAUTOWIDTH + dfAUTOHEIGHT;
    public static final int dfCENTER = 0;
    public static final int dfVDIR = 1024;
    public static final int dbVDir = 10;
    public static final int dfSCROLLX = 2048;
    public static final int dbScrollX = 11;
    public static final int dfSCROLLY = 4096;
    public static final int dbScrollY = 12;
    public static final int dfSCROLL = dfSCROLLX + dfSCROLLY;
    public static final int dfSIZEX = 8192;
    public static final int dbSizeX = 13;
    public static final int dfSIZEY = 16384;
    public static final int dbSizeY = 14;
    public static final int dfSIZE = dfSIZEX + dfSIZEY;
    public static final int dCOMM = Memory.tagUser + 4;
    public static final int dTEXT = Memory.tagUser + 10;
    public static final int dTXTLEN = Memory.tagUser + 11;
    public static final int dINTVAL = Memory.tagUser + 12;
    public static final int dTEXTCOLOR = Memory.tagUser + 13;
    public static final int dTEXTMODE = Memory.tagUser + 14;
    public static final int dFILL = Memory.tagUser + 20;
    public static final int dSPAN = Memory.tagUser + 21;
    public static final int dGAPSIZE = Memory.tagUser + 22;
    public static final int dXPOS = Memory.tagUser + 30;
    public static final int dYPOS = Memory.tagUser + 31;
    public static final int dWIDTH = Memory.tagUser + 32;
    public static final int dHEIGHT = Memory.tagUser + 33;
    public static final int dINNERWIDTH = Memory.tagUser + 34;
    public static final int dINNERHEIGHT = Memory.tagUser + 35;
    public static final int dSCROLLX = Memory.tagUser + 36;
    public static final int dSCROLLY = Memory.tagUser + 37;
    public static final int dAREA = Memory.tagUser + 40;
    public static final int dIMAGE = Memory.tagUser + 41;
    public static final int dUDATA = Memory.tagUser + 64;
    public static final int dUID = Memory.tagUser + 65;

    // TYPE


    public static interface GadgetPtr { // Opaque type
    }

    // VAR


    public GadgetPtr noGadget;
    public GadgetPtr tailGadget;


    public GadgetPtr getNoGadget() {
        return this.noGadget;
    }

    public void setNoGadget(GadgetPtr noGadget) {
        this.noGadget = noGadget;
    }

    public GadgetPtr getTailGadget() {
        return this.tailGadget;
    }

    public void setTailGadget(GadgetPtr tailGadget) {
        this.tailGadget = tailGadget;
    }

    // IMPL


    private JFrame mainFrame;
    private Runnable hideArea; // Stop rendering loop so that we can add dialogs to the screen
    private Runnable showArea; // Restart rendering loop
    private Image appImage;
    private List<Image> appImageList;


    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public JFrame getMainFrame() {
        return this.mainFrame;
    }

    public Runnable getHideArea() {
        return hideArea;
    }

    public void setHideArea(Runnable hideArea) {
        this.hideArea = hideArea;
    }

    public Runnable getShowArea() {
        return showArea;
    }

    public void setShowArea(Runnable showArea) {
        this.showArea = showArea;
    }
    
    public Image getAppImage() {
        return appImage;
    }

    public void setAppImage(Image appImage) {
        this.appImage = appImage;
    }
    
    public List<Image> getAppImageList() {
        return appImageList;
    }
    
    public void setAppImageList(List<Image> appImageList) {
        this.appImageList = appImageList;
    }

    private void invokeInSwing(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InvocationTargetException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private <E> E invokeInSwing(Supplier<E> task) {
        if (SwingUtilities.isEventDispatchThread()) {
            return task.get();
        } else {
            try {
                AtomicReference<E> result = new AtomicReference<>();
                SwingUtilities.invokeAndWait(() -> {
                    result.set(task.get());
                });
                return result.get();
            } catch (InvocationTargetException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void hideArea() {
        if (hideArea != null)
            invokeInSwing(hideArea);
    }

    public void showArea() {
        if (showArea != null)
            invokeInSwing(showArea);
    }

    private Gadget create(short type) {
        return switch ((int) type) {
            case dDialog -> new DialogGadget();
            case dProgress -> new ProgressGadget();
            case dGroup -> new GroupGadget();
            case dBool, dSwitch -> new ToggleGadget();
            case dCheckbox -> new CheckBoxGadget();
            case dButton -> new ButtonGadget();
            case dLabel -> new LabelGadget();
            case dIntEdit -> new NumberGadget();
            case dCycle -> new CycleGadget();
            default -> throw new IllegalArgumentException("Unhandled gadget type: " + (int) type);
        };
    }

    public GadgetPtr AllocGadget(short type) {
        return invokeInSwing(() -> {
            Gadget gadget = create(type);
            return gadget;
        });
    }

    public void ModifyGadget(GadgetPtr gadgetPtr, Memory.TagItem tags) {
        invokeInSwing(() -> {
            Gadget gadget = (Gadget) gadgetPtr;
            gadget.apply(tags);
            gadget.handlePlacement(tags);
        });
    }

    public GadgetPtr CreateGadget(short type, Memory.TagItem tags) {
        return invokeInSwing(() -> {
            Gadget gadget = create(type);
            gadget.apply(tags);
            gadget.handlePlacement(tags);
            return gadget;
        });
    }

    public void GetGadgetAttr(GadgetPtr gadget0, /* VAR */ Memory.TagItem what) {
        invokeInSwing(() -> {
            Gadget gadget = (Gadget) gadget0;
            gadget.getAttr(what);
        });
    }

    public void AddGadget(GadgetPtr parent0, GadgetPtr gadget0, GadgetPtr before0) {
        Gadget parent = (Gadget) parent0;
        Gadget gadget = (Gadget) gadget0;
//        Gadget before = (Gadget) before0;
        invokeInSwing(() -> {
            parent.addChild(gadget);
        });
    }

    public GadgetPtr AddNewGadget(GadgetPtr parent0, short type, Memory.TagItem tags) {
        return invokeInSwing(() -> {
            Gadget gadget = create(type);
            gadget.apply(tags);
            gadget.handlePlacement(tags);
            Gadget parent = (Gadget) parent0;
            parent.addChild(gadget);
            return gadget;
        });
    }

    public int RefreshGadget(GadgetPtr gadgetPtr) {
        invokeInSwing(() -> {
            Gadget gadget = (Gadget) gadgetPtr;
            gadget.refresh();
        });
        return Dialogs.DialogOk;
    }

    public void SetGadgetRect(GadgetPtr gadget, short sx, short sy, short sw, short sh) {
        // unused
        throw new UnsupportedOperationException("Not implemented: SetGadgetRect");
    }

    public int DrawGadget(GadgetPtr gadget) {
        // unused
        throw new UnsupportedOperationException("Not implemented: DrawGadget");
    }

    public void ModalDialog(GadgetPtr dialog, /* VAR */ Runtime.IRef<GadgetPtr> gadget, /* VAR */ Runtime.IRef<Short> bNum,
            /* VAR */ Runtime.IRef<Boolean> up) {
        // todo implement ModalDialog
        throw new UnsupportedOperationException("Not implemented: ModalDialog");
    }

    public void HideGadget(GadgetPtr gadget) {
        // todo implement HideGadget
        throw new UnsupportedOperationException("Not implemented: HideGadget");
    }

    public void ShowGadget(GadgetPtr gadget) {
        // todo implement ShowGadget
        throw new UnsupportedOperationException("Not implemented: ShowGadget");
    }

    public void BeginRefresh(GadgetPtr gadget, /* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y, /* VAR */ Runtime.IRef<Short> w,
            /* VAR */ Runtime.IRef<Short> h) {
        // todo implement BeginRefresh
        throw new UnsupportedOperationException("Not implemented: BeginRefresh");
    }

    public boolean EndRefresh(GadgetPtr gadget) {
        // todo implement EndRefresh
        throw new UnsupportedOperationException("Not implemented: EndRefresh");
    }

    public void RemoveGadget(GadgetPtr gadget) {
        // todo implement RemoveGadget
        throw new UnsupportedOperationException("Not implemented: RemoveGadget");
    }

    public void FreeGadget(/* VAR */ Runtime.IRef<GadgetPtr> gadget) {
        // todo implement FreeGadget
        throw new UnsupportedOperationException("Not implemented: FreeGadget");
    }

    public void DeepFreeGadget(/* VAR */ Runtime.IRef<GadgetPtr> gadget) {
        if (gadget.get() != null) {
            Gadget g = (Gadget) gadget.get();
            g.dispose();
            gadget.set(null);
        }
    }

    public Object GadgetToAddress(GadgetPtr gadget) {
        // todo implement GadgetToAddress
        throw new UnsupportedOperationException("Not implemented: GadgetToAddress");
    }

    public GadgetPtr AddressToGadget(Object addr) {
        // todo implement AddressToGadget
        throw new UnsupportedOperationException("Not implemented: AddressToGadget");
    }

    public void begin() {

    }

    public void close() {

    }
}
