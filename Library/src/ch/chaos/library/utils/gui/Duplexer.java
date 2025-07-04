package ch.chaos.library.utils.gui;

import javax.swing.SwingUtilities;

/**
 * Helper for Swing components that communicate with a model.
 * <p>
 * This class prevent infinite loops when:
 * <ul>
 * <li>A user action result in calling a model method (sending)
 * <li>The model method calls one of our listener (receiving)
 * <li>Our listener updates the GUI, which acts like a user action and calls the model again
 * </ul>
 * Or when
 * <ul>
 * <li>The model calls one of our listener (receiving)
 * <li>Out listener updates the GUI, which acts like a user action and calls the model (sending)
 * <li>The model calls one of our listener again
 * </ul>
 * This is done by maintaining two states, <i>sending</i> and <i>receiving</i>.
 * <ul>
 * <li><i>sending</i> is active when we call the model, and makes us ignore the controller calling us while active
 * <li><i>receiving</i> is active when the model calls us, and disables us from calling it back
 * </ul>
 */
public class Duplexer {

    private boolean sending;
    private boolean receiving;


    /**
     * Start updating model from GUI
     */
    public synchronized void markSending() {
        sending = true;
    }

    /**
     * Finished updating model from GUI
     */
    public synchronized void clearSending() {
        sending = false;
    }

    /**
     * @return whether model is being updated from GUI
     */
    public synchronized boolean isSending() {
        return sending;
    }

    /**
     * Start updating GUI from model
     */
    public synchronized void markReceiving() {
        receiving = true;
    }

    /**
     * Start updating GUI from model
     */
    public synchronized void clearReceiving() {
        receiving = false;
    }

    /**
     * @return whether GUI is being updated from model
     */
    public synchronized boolean isReceiving() {
        return receiving;
    }
    
    /**
     * Perform the given actions to update the model from the GUI.
     * <p>
     * The actions will be surrounded by {@link #markSending()} and {@link #clearSending()}.
     * They won't be executed if the GUI is being updated from the model as per {@link #isReceiving()}.
     */
    public synchronized void send(Runnable actions) {
        if (isReceiving())
            return;
        markSending();
        try {
            actions.run();
        } finally {
            SwingUtilities.invokeLater(this::clearSending);
        }
    }

    /**
     * Perform the given actions to update the GUI from the model
     * <p>
     * The actions will be surrounded by {@link #markReceiving()} and {@link #clearReceiving()}.
     * They won't be executed if the model is being updated from the GUI as per {@link #isSending()}.
     */
    public synchronized void receive(Runnable actions) {
        if (isSending())
            return;
        markReceiving();
        try {
            actions.run();
        } finally {
            SwingUtilities.invokeLater(this::clearReceiving);
        }
    }
    
}
