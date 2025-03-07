package ch.chaos.castle;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CompressGrotteDecors {

    private static final String COMPRESSED_FILE = "Decors";
    private static final String PLAIN_FILE = "Decors_Decompressed.txt";
    
    private static int curCh;
    private static int curCount;
    private static int currentPos;

    
    public static void main(String[] args) throws IOException {
        FileReader reader = new FileReader(PLAIN_FILE, StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int[] posTable = new int[100];
        try (reader; output) {
            currentPos = 0;
            
            for (int level = 0; level < 10; level++) {
                for (int game = 0; game < 10; game++) {
                    posTable[level * 10 + game] = currentPos;
                    
                    int ch;
                    for (int y = 0; y < 20; y++) {
                        curCh = 0;
                        curCount = 0;
                        for (int x = 0; x < 72; x++) {
                            ch = reader.read();
                            if (ch == '±')
                                ch = 'g';
                            if (ch == curCh && curCount < 255) {
                                curCount++;
                            } else {
                                // Output previous
                                writePrevious(output);
                                // Restart counter
                                curCount = 1;
                                curCh = ch;
                            }
                        }
                        writePrevious(output);
                        ch = reader.read();
                        if (ch != 012) {
                            throw new IllegalStateException("Expected new line at line " + (level * 10 + game + y));
                        }
                        if (y < 19) {
                            output.write(012);
                            currentPos++;
                        }
                    }
                }
            }
        }
        
        FileOutputStream fOutput = new FileOutputStream(COMPRESSED_FILE);
        try (fOutput) {
            for (int pos : posTable) {
                fOutput.write((byte) (pos >>> 24));
                fOutput.write((byte) ((pos >>> 16) & 0xff));
                fOutput.write((byte) ((pos >>> 8) & 0xff));
                fOutput.write((byte) (pos & 0xff));
            }
            fOutput.write(output.toByteArray());
        }
    }

    private static void writePrevious(ByteArrayOutputStream output) {
        if (curCount > 0) {
            if (curCount == 1) {
                output.write(curCh);
                currentPos++;
            } else {
                output.write((byte) (curCh + 128));
                output.write((byte) curCount);
                currentPos+= 2;
            }
        }
    }
    
}
