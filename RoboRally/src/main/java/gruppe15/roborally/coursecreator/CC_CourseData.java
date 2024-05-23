package gruppe15.roborally.coursecreator;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class CC_CourseData {
    private List<CC_SubBoard> subBoards;
    private String snapshotAsBase64;

    public CC_CourseData(List<CC_SubBoard> subBoards, String snapshotAsBase64) {
        this.subBoards = subBoards;
        this.snapshotAsBase64 = snapshotAsBase64;
    }

    public List<CC_SubBoard> getSubBoards() {
        return subBoards;
    }

    public String getSnapshotAsBase64() {
        return snapshotAsBase64;
    }

    public Image getImage() {
        // Decode Base64 to Image and save to file
        if (snapshotAsBase64 != null && !snapshotAsBase64.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(snapshotAsBase64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            Image courseImage = new Image(inputStream);
            return courseImage;
        }
        return null;
    }

    public void saveImageToFile() {
        Image courseImage = getImage();
        // Save the writable image to a file
        WritableImage writableImage = new WritableImage(courseImage.getPixelReader(), (int) courseImage.getWidth(), (int) courseImage.getHeight());
        File file = new File("savedImage.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Image saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
