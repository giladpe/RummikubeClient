/*
 * this class deal with images in the game
 */

package rummikub.view;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtils {

    public static final String RESOURCES_FOLDER = "/rummikub/view/resources/";
    public static final String HUMAN_PLAYER_LOGO = "HumanLogo.png";
    public static final String COMPUTER_PLAYER_LOGO = "ComputerLogo.png";
    public static final String TILE_LOGO = "tile.png";
    public static final double IMAGE_SIZE = 35;

    public static Image getImage (String imageName){
        InputStream imageInputStream = ImageUtils.class.getResourceAsStream(RESOURCES_FOLDER + imageName);
        return new Image(imageInputStream,IMAGE_SIZE,IMAGE_SIZE,true,true);
    }
    
    public static ImageView getImageView (String imageName){
        return new ImageView(getImage(imageName));
    }    
}
