package ch.chaos.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import ch.pitchtech.modula.runtime.Runtime;

public class Terminal {

    private static Terminal instance;


    private Terminal() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Terminal instance() {
        if (instance == null)
            new Terminal(); // will set 'instance'
        return instance;
    }

    // VAR


    public boolean waitCloseGadget;


    public boolean isWaitCloseGadget() {
        return this.waitCloseGadget;
    }

    public void setWaitCloseGadget(boolean waitCloseGadget) {
        this.waitCloseGadget = waitCloseGadget;
    }

    public void Flush() {
        System.out.flush();
    }

    public void BusyRead(/* VAR */ Runtime.IRef<Character> ch) {
        try {
            if (System.in.available() < 1) {
                ch.set((char) 0);
            } else {
                Read(ch);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void Read(/* VAR */ Runtime.IRef<Character> chRef) {
        try {
            char ch = (char) System.in.read();
            chRef.set(ch);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void ReadLn(/* VAR */ Runtime.IRef<String> st, /* VAR */ Runtime.IRef<Short> len) {
        System.out.flush();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            st.set(line);
            len.set((short) line.length());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void Write(char ch) {
        System.out.print(ch);
    }

    public void WriteLn() {
        System.out.println();
    }

    public void WriteString(String string) {
        System.out.println(string);
    }

    public void Format(String str, Object dats) {
        // todo implement Format
        throw new UnsupportedOperationException("Not implemented: Format");
    }

    public void FormatS(String str, /* VAR */ Runtime.IRef<String> innerStr) {
        // todo implement FormatS
        throw new UnsupportedOperationException("Not implemented: FormatS");
    }

    public void FormatNr(String str, int nr) {
        // todo implement FormatNr
        throw new UnsupportedOperationException("Not implemented: FormatNr");
    }

    public void WriteInt(int x, short n) {
        if (n <= 0) {
            System.out.print(x);
        } else {
            NumberFormat format = new DecimalFormat("0".repeat(n));
            System.out.print(format.format(x));
        }
    }

    public void WriteHex(int x, short n) {
        if (n <= 0) {
            System.out.print(Integer.toHexString(x));
        } else {
            String result = Integer.toHexString(x);
            while (result.length() < n)
                result = "0" + result;
            System.out.print(result);
        }
    }

    public void begin() {

    }

    public void close() {

    }
}
