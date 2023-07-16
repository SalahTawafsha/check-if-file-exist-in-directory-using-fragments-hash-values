package FilesReaders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecoveredFiles {

    private final List<String> recoveredHashing = new ArrayList<>();

    public RecoveredFiles(File[] files, String fileType, int size) {
        try {

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (fileType.equals(file.getName().split("\\.")[1])) {
                            byte[] buffer = new byte[size];
                            FileInputStream scan = new FileInputStream(file);
                            int j;
                            while ((j = scan.read(buffer)) != -1) {
                                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                byte[] encodedHash = digest.digest(Arrays.copyOfRange(buffer, 0, j));
                                recoveredHashing.add(new String(encodedHash));
                            }
                            scan.close();
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ObservableList<String> getRecoveredHashing() {
        return FXCollections.observableArrayList(recoveredHashing);
    }
}
