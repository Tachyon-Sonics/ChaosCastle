package ch.chaos.tools;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.util.Map;

public class DecodeObjects {
    
    private final static Map<Character, String> ARGS = Map.of(
            'C', "vvv", // Color, pen, pat
            'P', "vv", // Pat, bpen
            'X', "vv", // Color (light), Color (drak)
            'M', "v", // Copy/Trans/Xor
            'R', "cccc",
            'E', "cccc",
            'T', "ccvvv", // x,y,?,size,pen Followed by text, 0-terminated, max 4
            'L', "ccv", // x,y,?
            'F', "");
    
    public static void main(String[] unused) throws Exception {
        InputStream input = DecodeObjects.class.getResourceAsStream("/Objects");
        DataInputStream dInput = new DataInputStream(input);
        try (dInput) {
            boolean highx = false;
            boolean highy = false;
            boolean x = true;
            
            while (true) {
                char ch;
                try {
                    int data = dInput.readByte() & 0xff;
                    if (data >= 0200) {
                        highy = true;
                        data -= 128;
                    } else {
                        highy = false;
                    }
                    if (data >= 0140) {
                        highx = true;
                        data -= 32;
                    } else {
                        highx = false;
                    }
                    ch = (char) data;
                } catch (EOFException ex) {
                    break;
                }
                if (!ARGS.containsKey(ch))
                    throw new IllegalStateException("Unexpected char: " + ch);
                System.out.print(ch + " ");
                String args = ARGS.get(ch);
                for (int k = 0; k < args.length(); k++) {
                    int arg = dInput.readByte() & 0xff;
                    if (args.charAt(k) == 'c') {
                        if (x) {
                            if (highx && arg < 64)
                                arg += 256;
                        } else {
                            if (highy && arg < 64)
                                arg += 256;
                        }
                        x = !x;
                    }
                    System.out.print(arg + " ");
                }
                if (ch == 'T') {
                    System.out.print("\"");
                    for (int i = 0; i < 4; i++) {
                        char txt = (char) dInput.readByte();
                        if (txt == '"')
                            break;
                        System.out.print(txt);
                    }
                    System.out.print("\"");
                }
                System.out.println();
            }
        }
    }

}
