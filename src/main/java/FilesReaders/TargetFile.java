package FilesReaders;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetFile {


    private String fileType;
    private final List<String> targetHashing = new ArrayList<>();
    private final int size;
    private boolean error;

    public TargetFile(File input, int size) throws IOException {
        if (input == null || !input.exists())
            throw new FileNotFoundException();
        this.size = size;

        fileType = input.getName().split("\\.")[1];

        try (FileInputStream scan = new FileInputStream(input)) {

            byte[] buffer = new byte[size];

            int j, count = 1;
            while ((j = scan.read(buffer)) != -1) {
//                try (FileOutputStream out = new FileOutputStream("targetFileFragments/" + input.getName().substring(0, input.getName().lastIndexOf(".")) + "(" + count + ")" + ".frg");) {
//                    out.write(buffer, 0, j);
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] encodedHash = digest.digest(Arrays.copyOfRange(buffer, 0, j));
                    targetHashing.add(new String(encodedHash));
//                } catch (IOException e) {
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Error");
//                    alert.setHeaderText("Error while writing to file");
//                    alert.setContentText("Please try again");
//                    alert.show();
//                    error = true;
//                    break;
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                count++;
            }


        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public String getFileType() {
        return fileType;
    }

    public int getSize() {
        return size;
    }

    public ObservableList<String> getTargetHashing() {
        return FXCollections.observableArrayList(targetHashing);
    }
}
