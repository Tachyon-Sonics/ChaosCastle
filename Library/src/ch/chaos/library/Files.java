package ch.chaos.library;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import ch.chaos.library.launcher.LauncherFrame;
import ch.chaos.library.settings.AppSettings;
import ch.chaos.library.settings.Settings;
import ch.chaos.library.utils.Async;
import ch.chaos.library.utils.FullScreenUtils;
import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Files {

    private static Files instance;


    private Files() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Files instance() {
        if (instance == null)
            new Files(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int fNAME = Memory.tagUser + 0;
    public static final int fLENGTH = Memory.tagUser + 1;
    public static final int fTEXT = Memory.tagUser + 16;
    public static final int fFLAGS = Memory.tagUser + 8;
    public static final int afFILE = 1;
    public static final int bfFile = 0;
    public static final int afNEWFILE = 2;
    public static final int bfNewFile = 1;
    public static final int afMODULE = 4;
    public static final int bfModule = 2;
    public static final int afPARAM = 8;
    public static final int bfParam = 3;
    public static final int msGraphic = 0;
    public static final int msSound = 1;
    public static final int msClock = 2;
    public static final int msInput = 3;
    public static final int msDialogs = 4;
    public static final int msMenus = 5;

    // TYPE


    public static interface FilePtr { // Opaque type
    }

    public static interface DirectoryPtr { // Opaque type
    }

    public static enum AccessFlags {
        accessRead,
        accessWrite;
    }

    // VAR


    public FilePtr noFile;
    public DirectoryPtr noDir;


    public FilePtr getNoFile() {
        return this.noFile;
    }

    public void setNoFile(FilePtr noFile) {
        this.noFile = noFile;
    }

    public DirectoryPtr getNoDir() {
        return this.noDir;
    }

    public void setNoDir(DirectoryPtr noDir) {
        this.noDir = noDir;
    }

    // IMPL


    static class File implements FilePtr {

        RandomAccessFile file;

    }


    private String lastErrorMsg;

    private JFileChooser chooser;


    public static java.io.File appDataDirectory() {
        String appName = Runtime.getAppNameOrDefault();
        java.io.File baseDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        java.io.File appDir = new java.io.File(baseDir, appName);
        appDir.mkdir();
        return appDir;
    }

    public Runtime.IRef<String> AskFile(Memory.TagItem tags) {
        String fileName = Memory.tagString(tags, fNAME, null);
        String title = Memory.tagString(tags, fTEXT, "Open...");
        int flags = Memory.tagInt(tags, fFLAGS, 0);
        boolean saveDialog = (flags & Files.afNEWFILE) != 0;

        AtomicReference<java.io.File> chosenFile = new AtomicReference<>();
        AtomicBoolean approved = new AtomicBoolean(false);

        while (!approved.get()) {
            // Create file chooser
            try {
                SwingUtilities.invokeAndWait(() -> {
                    UIManager.put("FileChooser.readOnly", Boolean.TRUE);
                    if (chooser == null) {
                        chooser = new JFileChooser();
                        chooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "Saved Items";
                            }

                            @Override
                            public boolean accept(java.io.File f) {
                                if (f.getName().startsWith("."))
                                    return false;
                                return true;
                            }
                        });
                        chooser.setCurrentDirectory(appDataDirectory());
                    }
                    chooser.setDialogTitle(title);
                    if (fileName != null && !fileName.isBlank()) {
                        chooser.setSelectedFile(new java.io.File(fileName));
                    }
                });
            } catch (InvocationTargetException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            // Display file chooser
            Async<Integer> result = new Async<>();
            if (Checks.instance().isFullScreenActive()) {
                SwingUtilities.invokeLater(() -> {
                    if (saveDialog) {
                        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    } else {
                        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                    }
                    chooser.addActionListener(e -> {
                        Integer reply = null;
                        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                            reply = JFileChooser.APPROVE_OPTION;
                            chosenFile.set(chooser.getSelectedFile());
                        } else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                            reply = JFileChooser.CANCEL_OPTION;
                        }
                        if (reply != null) {
                            FullScreenUtils.removeFullScreenDialog(chooser);
                            chooser = null;
                            result.submit(reply);
                        }
                    });
                    FullScreenUtils.addFullScreenDialog(chooser, title);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    int reply;
                    if (saveDialog) {
                        reply = chooser.showSaveDialog(Checks.instance().owner());
                    } else {
                        reply = chooser.showOpenDialog(Checks.instance().owner());
                    }
                    if (reply == JFileChooser.APPROVE_OPTION) {
                        chosenFile.set(chooser.getSelectedFile());
                    }
                    result.submit(reply);
                });
            }

            // Check for cancel
            int reply = result.retrieve();
            if (reply != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            // Check for confirm overwrite
            if (saveDialog) {
                java.io.File file = chosenFile.get();
                if (file.exists()) {
                    // File already exists, ask for confirmation
                    boolean overwrite = Checks.instance().ask(
                            "File already exists. Overwrite?",
                            "Yes", "No");
                    if (overwrite) {
                        approved.set(true);
                    } // else, open file chooser again to choose another file, or to cancel
                } else {
                    approved.set(true);
                }
            } else {
                approved.set(true); // No confirmation on load
            }
        } // while (!approved.get())

        java.io.File file = chosenFile.get();
        return new Runtime.Ref<>(file.getPath());
    }

    public Object FileToAddress(FilePtr file) {
        return file;
    }

    public FilePtr AddressToFile(Object addr) {
        return (File) addr;
    }

    public FilePtr OpenFile(Runtime.IRef<String> name, EnumSet<AccessFlags> flags) {
        String fileName = name.get();
        // First try resource
        InputStream input = Files.class.getResourceAsStream("/" + fileName);
        if (input != null) {
            // Copy to temporary file
            try (input) {
                Path tmpPath = java.nio.file.Files.createTempFile(fileName, ".tmp");
                java.nio.file.Files.copy(input, tmpPath, StandardCopyOption.REPLACE_EXISTING);
                fileName = tmpPath.toString();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if (!fileName.contains(java.io.File.separator)) {
            String appName = Runtime.getAppNameOrDefault();
            java.io.File baseDir = FileSystemView.getFileSystemView().getDefaultDirectory();
            java.io.File appDir = new java.io.File(baseDir, appName);
            appDir.mkdir();
            java.io.File binDir = new java.io.File(appDir, ".data");
            binDir.mkdir();
            fileName = binDir.getPath() + java.io.File.separator + fileName;
        }

        File result = new File();
        String mode = "";
        if (flags.contains(AccessFlags.accessRead))
            mode += "r";
        if (flags.contains(AccessFlags.accessWrite)) {
            if (mode.equals(""))
                mode += "r";
            mode += "w";
        }
        try {
            if (flags.contains(AccessFlags.accessWrite) && !flags.contains(AccessFlags.accessRead)) {
                // Delete first so that a new file is created (else, RandomAccessFile won't truncate the existing file)
                java.nio.file.Files.deleteIfExists(Path.of(fileName));
            }
            result.file = new RandomAccessFile(fileName, mode);
            return result;
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            return null;
        }
    }

    public int FileLength(FilePtr f) {
        File file = (File) f;
        try {
            return (int) file.file.length();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int ReadFileBytes(FilePtr f, Object data, int length) {
        File file = (File) f;
        try {
            if (data instanceof IRef<?> ref) {
                Class<?> dataType = ref.getDataType();
                if (dataType.equals(Character.class)
                        || dataType.equals(Long.class)
                        || dataType.equals(Integer.class)
                        || dataType.equals(Short.class)
                        || dataType.equals(Float.class)
                        || dataType.equals(Double.class)
                        || dataType.equals(Boolean.class)
                        || dataType.isEnum()) {
                    return readRef(file, ref, dataType, length);
                } else if (dataType.equals(String.class)) {
                    return readString(file, ref, length);
                } else if (dataType.equals(byte[].class)) {
                    @SuppressWarnings("unchecked")
                    IRef<byte[]> byteRef = (IRef<byte[]>) ref;
                    byte[] bytes = byteRef.get();
                    int result = readBytes(file, bytes, length);
                    byteRef.set(bytes);
                    return result;
                } else {
                    throw new UnsupportedOperationException("Not implemented datatype " + dataType.getName());
                }
            } else if (data instanceof byte[] bytes) {
                return readBytes(file, bytes, length);
            } else if (data instanceof short[] shorts) {
                return readShorts(file, shorts, length);
            } else {
                // todo implement ReadFileBytes
                throw new UnsupportedOperationException("Not implemented: ReadFileBytes " + data.getClass().getName());
            }
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private int readRef(File file, @SuppressWarnings("rawtypes") IRef ref, Class<?> type, int length) throws IOException {
        byte[] data = new byte[length];
        file.file.readFully(data);
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        Object value = Runtime.fromByteBuffer(byteBuffer, type);
        ref.set(value);
        return length;
    }

    private int readString(File file, IRef<?> ref, int length) throws IOException {
        @SuppressWarnings("unchecked")
        IRef<String> stringRef = (IRef<String>) ref;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int nextByte = file.file.read();
            if (nextByte >= 0) {
                result.append((char) nextByte);
            } else {
                lastErrorMsg = "End of File reached";
                return i;
            }
        }
        stringRef.set(result.toString());
        return length;
    }

    private int readBytes(File file, byte[] bytes, int length) throws IOException {
        int result = file.file.read(bytes, 0, length);
        if (result < length)
            lastErrorMsg = "End of File reached";
        return result;
    }

    private int readShorts(File file, short[] shorts, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            int nextByte = file.file.read();
            if (nextByte >= 0) {
                shorts[i] = (short) nextByte;
            } else {
                lastErrorMsg = "End of File reached";
                return i;
            }
        }
        return length;
    }

    public int WriteFileBytes(FilePtr f, Object data, int length) {
        File file = (File) f;
        try {
            if (data instanceof byte[] bytes) {
                file.file.write(bytes, 0, length);
                return length;
            } else if (data instanceof IRef<?> ref) {
                Class<?> dataType = ref.getDataType();
                if (dataType.equals(Character.class)
                        || dataType.equals(Long.class)
                        || dataType.equals(Integer.class)
                        || dataType.equals(Short.class)
                        || dataType.equals(Float.class)
                        || dataType.equals(Double.class)
                        || dataType.equals(Boolean.class)
                        || dataType.isEnum()) {
                    return writeRef(file, ref, dataType, length);
                } else if (dataType.equals(String.class)) {
                    return writeString(file, ref, length);
                } else {
                    throw new UnsupportedOperationException("Not implemented datatype " + dataType.getName());
                }
            }
            throw new UnsupportedOperationException("Not implemented: WriteFileBytes " + data);
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            return 0;
        }
    }

    private int writeRef(File file, IRef<?> ref, Class<?> type, int length) throws IOException {
        byte[] data = Runtime.toByteArray(ref.get(), length);
        file.file.write(data);
        return length;
    }

    private int writeString(File file, IRef<?> ref, int length) throws IOException {
        @SuppressWarnings("unchecked")
        IRef<String> stringRef = (IRef<String>) ref;
        String str = stringRef.get();
        for (int i = 0; i < length; i++) {
            byte nextByte;
            if (i < str.length()) {
                nextByte = (byte) str.charAt(i);
            } else {
                nextByte = 0;
            }
            file.file.write(nextByte);
        }
        return length;
    }

    public int SkipFileBytes(FilePtr f, int count) {
        File file = (File) f;
        try {
            file.file.seek(file.file.getFilePointer() + count);
            return count;
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            return 0;
        }
    }

    public int GetFilePos(FilePtr f) {
        File file = (File) f;
        try {
            return (int) file.file.getFilePointer();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int SetFilePos(FilePtr f, int pos) {
        File file = (File) f;
        try {
            file.file.seek(pos);
            return pos;
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            try {
                return (int) file.file.getFilePointer();
            } catch (IOException ex1) {
                throw new RuntimeException(ex);
            }
        }
    }

    public Runtime.IRef<String> FileErrorMsg() {
        if (lastErrorMsg == null)
            return null;
        Runtime.IRef<String> result = new Runtime.Ref<>(lastErrorMsg);
        lastErrorMsg = null;
        return result;
    }

    public void CloseFile(IRef<FilePtr> f) {
        if (f.get() == null)
            return;
        File file = (File) f.get();
        try {
            file.file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        f.set(null);
    }

    public boolean RenameFile(Runtime.IRef<String> oldName, Runtime.IRef<String> newName) {
        try {
            java.nio.file.Files.move(Path.of(oldName.get()), Path.of(newName.get()));
            return true;
        } catch (IOException ex) {
            lastErrorMsg = ex.getMessage();
            return false;
        }
    }

    public boolean DeleteFile(Runtime.IRef<String> name) {
        // unused
        throw new UnsupportedOperationException("Not implemented: DeleteFile");
    }

    public DirectoryPtr OpenDirectory(Runtime.IRef<String> name) {
        return null;
        // todo implement OpenDirectory
    }

    public boolean DirectoryNext(DirectoryPtr d, Memory.TagItem tags) {
        // todo implement DirectoryNext
        throw new UnsupportedOperationException("Not implemented: DirectoryNext");
    }

    public void CloseDirectory(IRef<DirectoryPtr> d) {
        // todo implement RenameFile
        throw new UnsupportedOperationException("Not implemented: CloseDirectory");
    }

    public Runtime.RangeSet AskMiscSettings(Runtime.RangeSet which) {
        Runtime.RangeSet result = new Runtime.RangeSet(Memory.SET16_r);
        result.incl(Files.msGraphic);
        result.incl(Files.msMenus);
        CountDownLatch latch = new CountDownLatch(1);
        Thread launcherThread = new Thread(() -> {
            showLauncherSettings((as) -> {
                // Save
                Settings.reload(); // New settings have been sdaved. Reload them to reflect changes
                latch.countDown();
            }, () -> {
                // Cancel
                Settings.reload(); // New settings have not been save. Reload to erase any in-place modification
                latch.countDown();
            });
        }, "Show Launcher Settings");
        launcherThread.start();
        try {
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private void showLauncherSettings(Consumer<AppSettings> onSave, Runnable onCancel) {
        SwingUtilities.invokeLater(() -> {
            LauncherFrame launcherFrame = new LauncherFrame(Settings.appSettings(), Settings.appMode(), onSave, onCancel);
            launcherFrame.pack();
            launcherFrame.setLocationRelativeTo(null);
            launcherFrame.setVisible(true);
        });
    }

    public void begin() {

    }

    public void close() {

    }
}
