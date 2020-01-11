import javafx.scene.image.ImageView;

/**
 * @author Qiang Ma
 */

public class MakeSmallOrEnlarge {
    private static int changeNum = 1;

    /**
     * @param imageView Method to increase the dimensions of image by 10% of its actual dimensions.
     */
    public static void enlarge(ImageView imageView) {
        changeNum += 1;
        imageView.setFitWidth(100 * (changeNum * 0.1 + 1));
        imageView.setFitHeight(100 * (changeNum * 0.1 + 1));
        imageView.setPreserveRatio(true);
    }

    /**
     * @param imageView Method to reduce the dimensions of image by 10% of its actual dimensions.
     */
    public static void small(ImageView imageView) {
        changeNum -= 1;
        //Image image = imageView.getImage();
        // ImageView imageView1 = new ImageView(image);
        imageView.setFitWidth(100 * (changeNum * 0.1 + 1));
        imageView.setFitHeight(100 * (changeNum * 0.1 + 1));
        imageView.setPreserveRatio(true);
    }
}