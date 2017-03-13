package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class Controller {
	@FXML
	private Button Load, Change;
	@FXML
	private ImageView image;
	private Mat logo,salty,desalty;
	private int mode=1;//1= salty, 2= desalty, 0=origin
	private final int saltchance=35, ksize=5;
	@FXML
	private void Load(){//load image
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(application.Main.cpy);
		if (file != null) {
		    logo = Imgcodecs.imread(file.getAbsolutePath());
		    salty=logo.clone();
		    desalty=logo.clone();
		    image.setImage(mat2Image(logo));
			Change.setDisable(false);
		}
	}
	@FXML
	private void Change(){//change image (original, noisy, blur)
		if(mode==1){//noise
			saltying();
			image.setImage(mat2Image(salty));
			Change.setText("DeSalty");
			Load.setDisable(true);
			mode=2;
		}
		else if (mode==2){//blur
			desaltying();
			image.setImage(mat2Image(desalty));
			Change.setText("Revert");
			mode=0;
		}
		else{//original
			Load.setDisable(false);
			image.setImage(mat2Image(logo));
			mode=1;
			salty=logo.clone();
			Change.setText("Salty");
		}	
	}
	public void initialize(){/*TODO*/}
	private void saltying(){
		Random rand= new Random();
		for(int i=0;i<salty.height();i++){
			for (int j = 0; j < salty.width(); j++) {
				rand.nextInt(100);
				if(rand.nextInt(100)<saltchance){//n% chance to be saltified (salt&pepper noise)
					int r=rand.nextInt(2);
					if(r==0){//50% chance of black
						double[] bgr={0,0,0};
						salty.put(i, j, bgr);
					}else{//50% chance of white
						double[] bgr={255,255,255};
						salty.put(i, j, bgr);
					}
				}
			}
		}
	}
	private void desaltying(){//blurring. in this case median blur is the best way
		Imgproc.medianBlur(salty, desalty, ksize);//median blur with kernel size of ksize 
		//Imgproc.GaussianBlur(salty, desalty, new Size(ksize,ksize), 0);//median blur with kernel size of ksize 
		//Imgproc.blur(salty, desalty, new Size(ksize,ksize));
		
	}
	private Image mat2Image(Mat frame){
		MatOfByte buffer = new MatOfByte();// create a temporary buffer
		Imgcodecs.imencode(".png", frame, buffer);// encode the frame in the buffer, according to the PNG format
		return new Image(new ByteArrayInputStream(buffer.toArray()));// build and return an Image created from the image encoded in the buffer
	}
}
