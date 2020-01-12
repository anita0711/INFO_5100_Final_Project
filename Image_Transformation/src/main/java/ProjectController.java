import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;

/**
 * @author Anita Ganani and Qiang Ma
 */
public class ProjectController implements EffectsInterface {

    @FXML
    private ImageView imageView;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem jpgToPng;
    @FXML
    private MenuItem pngToJpg;
    @FXML
    private TextArea text;
    @FXML
    public Button properties;

    private WritableImage wImage;
    private FileChooser fileChooser;
    Window stage;
    File file = null;
    Image image;

    Alert minDimensionAlert = new Alert(Alert.AlertType.WARNING, "You have reached the minimum " +
            "dimensions of an image (1x1).");
    Alert maxDimensionAlert = new Alert(Alert.AlertType.WARNING, "You have reached the maximum " +
            "dimensions of an image.");
    Alert noEffectsAlert = new Alert(Alert.AlertType.WARNING, "Please add an effect on the image before you save that");

    /**
     * @param openFileActionEvent Method to open a .jpg or .jpeg or .png or .bmp or .gif file from user's system.
     */
    public void openFile(ActionEvent openFileActionEvent) {
        fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.JPEG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser.ExtensionFilter extFilterBMP = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.BMP");
        FileChooser.ExtensionFilter extFilterGIF = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.GIF");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterJPEG, extFilterPNG, extFilterBMP, extFilterGIF);

        //Show open file dialog
        file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            System.out.println(file.getAbsolutePath());

            // BufferedImage bufferedImage = ImageIO.read(file);
            // Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            try {
                image = new Image(file.toURI().toURL().toString());
                imageView.setImage(image);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        } else {
            noImageSelectionWarning();
        }
    }

    /**
     * @param actionEvent Method to save the file on a location and in a format specified by user
     *                    only after applying effects.
     */
    @FXML
    public void saveFile(ActionEvent actionEvent) {
        //This will throw a warning message if user tries to Save image without
        if(wImage == null && file!=null){
            noEffectsAlert.showAndWait();
        }

        if (file != null) {
            fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(stage);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", file);
                imageSaveMsg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            noImageSelectionWarning();
        }
    }

    /**
     * @param toPng
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException     Method to save the uploaded image as .png with the dimensions of image view
     *                              (by default 100x100)
     */
    public void saveAsPNG(ActionEvent toPng) throws IOException, InterruptedException, IM4JavaException {
        if (file != null) {
            String inputPath = file.getAbsolutePath();
            String outputPath = FilenameUtils.removeExtension(inputPath);
            //outputPath = outputPath + getDateTime() + ".png";
            //outputImage = getExtensionByStringHandling(inputPath) + getDateTime();
            outputPath = outputPath + "_thumb_PNG.png";
            System.out.println(outputPath);

            try {
                FileInputStream fis = new FileInputStream(inputPath);
                FileOutputStream fos = new FileOutputStream(outputPath);
                resizeImage(fis, fos, (int) imageView.getFitHeight(), (int) imageView.getFitHeight(), "png");
                imageSaveMsg();
                System.out.println("Success. Converted and saved to .PNG file.");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (IM4JavaException ex) {
                ex.printStackTrace();
            }
        } else {
            noImageSelectionWarning();
        }
    }


    /**
     * @param toJpg
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException     Method to save the uploaded image as .jpg with the dimensions of image view
     *                              (by default 100x100)
     */
    public void saveAsJPG(ActionEvent toJpg) throws IOException, InterruptedException, IM4JavaException {
        if (file != null) {
            String inputPath = file.getAbsolutePath();
            String outputPath = FilenameUtils.removeExtension(inputPath);
            outputPath = outputPath + "_thumb_JPG.jpg";
            System.out.println(outputPath);

            try {
                FileInputStream fis = new FileInputStream(inputPath);
                FileOutputStream fos = new FileOutputStream(outputPath);
                resizeImage(fis, fos, (int) imageView.getFitHeight(), (int) imageView.getFitHeight(), "jpg");
                imageSaveMsg();
                System.out.println("Success. Converted and saved as .JPG file.");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (IM4JavaException ex) {
                ex.printStackTrace();
            }
        } else {
            noImageSelectionWarning();
        }
    }

    /**
     * @param input
     * @param output
     * @param width
     * @param height
     * @param format
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException     Method to resize an image (default 100x100),
     *                              used by saveAsJPG and saveAsPNG methods.
     */
    public void resizeImage(InputStream input, OutputStream output, int width, int height,
                            String format) throws IOException, InterruptedException, IM4JavaException {
        // ProcessStarter.setGlobalSearchPath("C:\\Program Files (x86)\\ImageMagick-6.3.9-Q16");
        ConvertCmd command = new ConvertCmd(true);

        Pipe pipeIn = new Pipe(input, null);
        Pipe pipeOut = new Pipe(null, output);

        command.setInputProvider(pipeIn);
        command.setOutputConsumer(pipeOut);

        IMOperation operation = new IMOperation();
        operation.addImage("-");
        operation.resize(width, height);
        operation.addImage(format + ":-");

        command.run(operation);
    }

    /**
     * Method to exit from the main screen.
     */
    public void exit() {
        Platform.exit();
    }

    /**
     * @param type Method to give a particular effect(s) to an image based on selection made.
     */
    @Override
    public void pixWithImage(int type) {
        if (file != null) {
            PixelReader pixelReader = imageView.getImage().getPixelReader();
            // Create WritableImage
            wImage = new WritableImage(
                    (int) image.getWidth(),
                    (int) image.getHeight());
            PixelWriter pixelWriter = wImage.getPixelWriter();

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = pixelReader.getColor(x, y);
                    switch (type) {
                        case 0:
                            color = color.brighter();
                            break;
                        case 1:
                            color = color.darker();
                            break;
                        case 2:
                            color = color.grayscale();
                            break;
                        case 3:
                            color = color.invert();
                            break;
                        case 4:
                            color = color.saturate();
                            break;
                        case 5:
                            color = color.desaturate();
                            break;
                        default:
                            break;
                    }
                    pixelWriter.setColor(x, y, color);
                }
            }
            imageView.setImage(wImage);
        } else {
            noImageSelectionWarning();
        }

    }

    /**
     * @param actionEvent Method to increase brightness of an image.
     */
    public void setBrighter(ActionEvent actionEvent) {
        pixWithImage(0);
    }

    /**
     * @param actionEvent Method to make the image darker.
     */
    public void setDarker(ActionEvent actionEvent) {
        pixWithImage(1);
    }

    /**
     * @param actionEvent Method to change the image to gray scale.
     */
    public void setGray(ActionEvent actionEvent) {
        pixWithImage(2);

    }

    /**
     * @param actionEvent Method to invert the image such as changing black to white, etc.
     */
    public void setInvert(ActionEvent actionEvent) {
        pixWithImage(3);
    }

    /**
     * @param actionEvent Method to make the colors of an image more colorful.
     */
    public void setSaturate(ActionEvent actionEvent) {
        pixWithImage(4);
    }

    /**
     * @param actionEvent Method to make the colors of an image less colorful or lighter.
     */
    public void setDesaturate(ActionEvent actionEvent) {
        pixWithImage(5);
    }

    /**
     * @param actionEvent Method to recover the image and set back it to original.
     */
    public void setRecover(ActionEvent actionEvent) {
        imageView.setImage(image);
    }

    /**
     * @param event Method to get properties of an image and image view such as height, width and location.
     */
    @FXML
    public void getImageProperty(ActionEvent event) {
        if (file != null) {
            text.setText("Image Original Properties:");
            text.appendText("\n Height: " + image.getHeight());
            text.appendText("\n Width: " + image.getWidth());
            text.appendText("\n Location: " + image.impl_getUrl());

            text.appendText("\n\nCurrent image size: ");
            text.appendText("\n Height: " + (int) imageView.getFitWidth());
            text.appendText("\n Width: " + (int) imageView.getFitHeight());

        } else {
            noImageSelectionWarning();
        }
    }

    /**
     * @param event Method to enlarge the size of an image on image view (default 100x100, max 200x200).
     */
    @FXML
    public void enlargeAction(ActionEvent event) {
        if (imageView.getFitWidth() <= 200 && imageView.getFitWidth() >= 1) {
            MakeSmallOrEnlarge.enlarge(imageView);
        }
        if (imageView.getFitWidth() >= 200)  //when the size of imageview is larger than 200, the enlarge
        // function are not allowed to implement
        {
            maxDimensionAlert.showAndWait();
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
        }
    }

    /**
     * @param event Method to enlarge the size of an image on image view (default 100x100, min 1x1).
     */
    @FXML
    public void smallAction(ActionEvent event) {
        if (file != null) {
            if (imageView.getFitWidth() <= 200 && imageView.getFitWidth() >= 1) {
                MakeSmallOrEnlarge.small(imageView);
                return;
            }
            if (imageView.getFitWidth() <= 1)   //when the size of imageView is smaller than 1,
            // the small function is not allowed to implement
            {
                minDimensionAlert.showAndWait();
                imageView.setImage(image);
                imageView.setFitWidth(1);
                imageView.setFitHeight(1);
            }
        } else {
            noImageSelectionWarning();
        }
    }

    /**
     * Method to throw a warning message if no image is selected and
     * user still chooses any effects or properties or tries to save the image.
     */
    public void noImageSelectionWarning() {
        Alert selectImageAlert = new Alert(Alert.AlertType.WARNING);
        selectImageAlert.setTitle("No image selected");
        selectImageAlert.setContentText("Please first select an image.");
        selectImageAlert.showAndWait();
        System.out.println("Please select a file");
        return;
    }

    /**
     * Method to show a message that image has been saved successfully.
     */
    public void imageSaveMsg() {
        Alert selectImageAlert = new Alert(Alert.AlertType.INFORMATION);
        selectImageAlert.setTitle("Image Saved");
        selectImageAlert.setContentText("Your image has been saved successfully.");
        selectImageAlert.showAndWait();
        System.out.println("Success. Image saved successfully.");
        return;
    }

    /**
     * Method to show details of how to use Image Transformation application.
     */
    public void aboutDescription() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About ");
        alert.setHeaderText("Welcome to Image Transformation Application!");
        alert.setContentText("This tool has been created by Anita and Qiang, students of class " +
                "INFO 5100 Application Engineering. " +
                "Below are the some guidelines to use this tool:" +
                "\n 1. File" +
                "\n  a) Open - You can upload your image using Open option and it will be displayed in 100x100 window." +
                "\n  b) Save - Choose the location of your choice and save the image along with extension (.jpg or .png) " +
                "only after applying effects." +
                "\n  c) Save As - This will save the image as either _thumb_JPG or _thumb_PNG at the same location " +
                "from where it was picked." +
                "\n  d) Exit - This will close the application." +
                "" +
                "\n \n 2. Edit " +
                "\n  This has some of the effects which can be applied to your image such as Increase Brightness, Make it Darker, " +
                "Grayed it out, etc." +
                "" +
                "\n \n 3. Help" +
                "\n  a) About - This window will be popped up." +
                "\n  b) Documentation - This will have a link to Java documentation of this project." +
                "" +
                "\n \n Your image here:" +
                "\n Your uploaded image will be be shown here with 100x100 size." +
                "" +
                "\n \n Plus/Minus buttons will enlarge or reduce the dimensions of image in image view." +
                "" +
                "\n \n Image Properties" +
                "\n This will the image's original height, width, and location. Also, it will show height and width" +
                "after enlarging and reducing the image view's dimension (default 100x100).");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }

    /**
     * @throws IOException Method to see the Javadoc.
     */
    @FXML
    public void documentation() throws IOException {
        String path = System.getProperty("user.dir");
        String docPath = "\\src\\main\\resources\\Javadoc\\index.html";
        String fullPath = path + docPath;
        File url = new File(fullPath);
        Desktop.getDesktop().browse(url.toURI());
    }

/*    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }*/
}
