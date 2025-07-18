package ch.chaos.library;

import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.Ref;

public class Registration {

    private static Registration instance;


    private Registration() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Registration instance() {
        if (instance == null)
            new Registration(); // will set 'instance'
        return instance;
    }

    // VAR


    public Runtime.IRef<String> userName /* POINTER */;
    public Runtime.IRef<String> userAddress /* POINTER */;
    public Runtime.IRef<String> userLoc /* POINTER */;
    public boolean registered = true;


    public Runtime.IRef<String> getUserName() {
        return this.userName;
    }

    public void setUserName(Runtime.IRef<String> userName) {
        this.userName = userName;
    }

    public Runtime.IRef<String> getUserAddress() {
        return this.userAddress;
    }

    public void setUserAddress(Runtime.IRef<String> userAddress) {
        this.userAddress = userAddress;
    }

    public Runtime.IRef<String> getUserLoc() {
        return this.userLoc;
    }

    public void setUserLoc(Runtime.IRef<String> userLoc) {
        this.userLoc = userLoc;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public void begin() {
        this.userName = new Ref<>("Community (Open Source)");
        this.userAddress = new Ref<>("Java port (C) 2025 Nicolas Juillerat");
        this.userLoc = new Ref<>("Eclipse Public License - v 2.0");
    }

    public void close() {

    }
}
