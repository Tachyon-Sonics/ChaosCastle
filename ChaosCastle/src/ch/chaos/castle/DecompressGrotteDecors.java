package ch.chaos.castle;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Files.FilePtr;
import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.Ref;

public class DecompressGrotteDecors {
    
    private static final String COMPRESSED_FILE = "Decors";
    private static final String PLAIN_FILE = "Decors_Decompressed.txt";
    
    private static final Files files = Files.instance();


    
    public static void main(String[] args) throws IOException {
        Ref<String> posch = new Ref<>("");
        Ref<Character> ch = new Ref<>((char) 0);
        Ref<Character> tmp = new Ref<>((char) 0);
        
        FileWriter writer = new FileWriter(PLAIN_FILE, StandardCharsets.UTF_8);

        for (int level = 0; level < 10; level++) {
            for (int game = 0; game < 10; game++) {
                FilePtr fh = files.OpenFile(new Ref<>(COMPRESSED_FILE), EnumSet.of(AccessFlags.accessRead));
                int c1 = level * 10 + game + 1;
                int c2 = c1;
                int pos = 0;
                while (c1 > 0) {
                    files.ReadFileBytes(fh, posch, 4);
                    pos = ((Runtime.getChar(posch, 0) * 256
                            + Runtime.getChar(posch, 1)) * 256
                            + Runtime.getChar(posch, 2)) * 256
                            + Runtime.getChar(posch, 3);
                    c1--;
                }
                pos += 400 - c2 * 4;
                files.SkipFileBytes(fh, pos);
                int bcount = 0;
                for (c1 = 0; c1 <= 19; c1++) {
                    for (c2 = 0; c2 <= 71; c2++) {
                        if (bcount == 0) {
                            files.ReadFileBytes(fh, ch, 1);
                            if (ch.get() > ((char) 0177)) {
                                ch.set((char) ((char) ch.get() - 128));
                                files.ReadFileBytes(fh, tmp, 1);
                                bcount = (short) (char) tmp.get();
                            } else {
                                bcount = 1;
                            }
                        }
                        bcount--;
                        if (ch.get() == 'g')
                            ch.set('±');
                        System.out.print(ch.get());
                        writer.write(ch.get());
                    }
                    if (c1 < 19) {
                        files.ReadFileBytes(fh, ch, 1);
                        assert bcount == 0 && ch.get() == (char) 012;
                        System.out.println();
                        writer.write("\n");
                    }
                }
                files.CloseFile(new Ref<>(fh));
                System.out.println();
                writer.write("\n");
            }
        }
        writer.close();
    }

}
