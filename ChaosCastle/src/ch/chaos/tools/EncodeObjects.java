package ch.chaos.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EncodeObjects {

    private final static String PLAIN_FILE = "src/Objects_plain.txt";
    private final static String BINARY_FILE = "src/Objects";

    
    private final static Map<Character, String> ARGS = DecodeObjects.ARGS;
    
    
    public static void main(String[] unused) throws Exception {
        FileReader fReader = new FileReader(new File(PLAIN_FILE), StandardCharsets.ISO_8859_1);
        BufferedReader reader = new BufferedReader(fReader);
        FileOutputStream output = new FileOutputStream(BINARY_FILE);
        try (reader; output) {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                if (line.trim().isBlank())
                    continue;
                String[] tokens = line.split("\\s+");
                
                if (tokens.length < 1 || tokens[0].length() != 1)
                    throw new IllegalArgumentException("Invalid line: " + line);
                
                // Get command
                char command = tokens[0].charAt(0);
                if (!ARGS.containsKey(command))
                    throw new IllegalStateException("Unexpected char: " + command);
                
                String args = ARGS.get(command);
                if (command == 'T') {
                    if (args.length() + 2 != tokens.length)
                        throw new IllegalArgumentException("Wrong number of arguments for line: " + line);
                } else {
                    if (args.length() + 1 != tokens.length)
                        throw new IllegalArgumentException("Wrong number of arguments for line: " + line);
                }
                
                // Decode args
                List<Integer> argData = new ArrayList<>();
                boolean highx = false;
                boolean highy = false;
                boolean x = true;
                for (int k = 0; k < args.length(); k++) {
                    int value = Integer.parseInt(tokens[k + 1]);
                    if (args.charAt(k) == 'c') {
                        if (value >= 256) {
                            if (x)
                                highx = true;
                            else
                                highy = true;
                            value -= 256;
                        }
                        x = !x;
                    }
                    argData.add(value);
                }
                
                // Write command
                int cmdValue = command;
                if (highy)
                    cmdValue += 128;
                if (highx)
                    cmdValue += 32;
                output.write(cmdValue);
                
                // Write args
                for (int argValue : argData) {
                    output.write(argValue);
                }
                
                // Write any text
                if (command == 'T') {
                    String str = tokens[args.length() + 1];
                    if (!str.startsWith("\"") || !str.endsWith("\""))
                        throw new IllegalArgumentException("Text expected: " + line);
                    str = str.substring(1, str.length() - 1);
                    output.write(str.getBytes(StandardCharsets.ISO_8859_1));
                    if (str.length() < 4)
                        output.write('"');
                }
            }
        }
    }
}
