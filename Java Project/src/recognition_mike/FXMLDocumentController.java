 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition_mike;

import GUI.GetStudentInfoController;
import GUI.StudentInfoController;
import GUI.StudentVisitController;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

/**
 * FXML Controller class
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
public class FXMLDocumentController implements Initializable {

    //  @FXML
    //  private CheckBox haarClassifier;
    @FXML
    private ImageView originalFrame;
    @FXML
    private Button cameraButton;
    @FXML
    private CheckBox newUser;
    @FXML
    private TextField newUserName;
    @FXML
    private Button bt_recordNewStudent;
    @FXML
     private Button report_but;

    @FXML
    private Button bt_newStudInfo;

    @FXML
    private Button bt_viewStudent;

    @FXML
    private Button bt_recordVisit;

    @FXML
    private Button gender_report;//gendeer report

    private String username;
//    private int numberOfPicturesTaken;
    //   private boolean completedRegisteringNewStudent;

    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;

    // face cascade classifier
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;

    public int index = 0;
    public int ind = 0;

    // New user Name for a training data
    public String newname;

    // Names of the people from the training set
    public HashMap<Integer, String> names = new HashMap<Integer, String>();

    // Random number of a training set
    public int random = (int) (Math.random() * 20 + 3);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;

        // disable 'new user' and 'view user' functionality
        this.bt_recordNewStudent.setDisable(true);
        this.bt_newStudInfo.setDisable(true);
        this.newUserName.setDisable(true);
        this.bt_viewStudent.setDisable(true);
        this.bt_recordVisit.setDisable(true);

        // Takes some time thus use only when training set
        // was updated 
        trainModel();
    }

    // ***************** ENTER STUDENT VISIT FUNCTIONALITY ***************************
    /**
     * VISIT Goes to the Window to record the reason for a students visit.
     *
     * @param event
     */
    @FXML
    void recordStudentVisit(ActionEvent event) {
        Stage s = (Stage) bt_recordVisit.getScene().getWindow();
        try {
            // Parent studentInfo = FXMLLoader.load(getClass().getResource("GetStudentInfo.fxml"));
            FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/StudentVisit.fxml"));
            StudentVisitController controller = new StudentVisitController(username);
            studentInfo.setController(controller);
            s.setScene(new Scene(studentInfo.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ************************** REPORTS FUNCTIONALITY ***********************************
    /**
     * Opens Report for Gender
     * @param event 
     */
    @FXML
    private void genderReport(ActionEvent event) {
       // Stage s = (Stage) gender_report.getScene().getWindow();
        try {
            Parent genderReport = FXMLLoader.load(getClass().getResource("/GUI/gender_report.fxml"));
            // FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/gender_report.fxml"));
            // StudentVisitController controller = new StudentVisitController(username);
            // studentInfo.setController(controller);
           // s.setScene(new Scene(genderReport));
           
           Stage stage = new Stage();
           stage.setScene(new Scene(genderReport));
           stage.setTitle("Report");
           stage.show();
         //  Stage st = new Stage().setScene(new Scene(FXMLLoader.load(getClass().getResource("/GUI/gender_report.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens Report for Reasons
     * @param event 
     */
    @FXML
     private void reasonReport(ActionEvent event) {
       // Stage s = (Stage) report_but.getScene().getWindow();
        try {
            Parent reasonReport = FXMLLoader.load(getClass().getResource("/GUI/reason_report.fxml"));
            // FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/gender_report.fxml"));
            // StudentVisitController controller = new StudentVisitController(username);
            // studentInfo.setController(controller);
           // s.setScene(new Scene(reasonReport));
            
            Stage stage = new Stage();
           stage.setScene(new Scene(reasonReport));
           stage.setTitle("Report");
           stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ************************** NEW USER FUNCTIONALITY ***********************************
    /**
     * NEW USER When you select the New User Checkbox.
     *
     * @param event
     */
    @FXML
    private void newUserSelected(ActionEvent event) {
        if (this.newUser.isSelected()) {
            this.bt_recordNewStudent.setDisable(false);
            //this.bt_newStudInfo.setDisable(false);
            this.newUserName.setDisable(false);
            this.bt_recordVisit.setDisable(true);
            this.bt_viewStudent.setDisable(true);
            this.cameraButton.setDisable(true);
        } else {
            this.bt_recordNewStudent.setDisable(true);
            this.bt_newStudInfo.setDisable(true);
            this.newUserName.setDisable(true);
            this.bt_recordVisit.setDisable(false);
            this.bt_viewStudent.setDisable(false);
            this.cameraButton.setDisable(false);
        }
    }

    /**
     * NEW USER Goes to the Window to enter new information.
     *
     * @param event
     */
    @FXML
    private void enterNewStudentInformation(ActionEvent event) {
        Stage s = (Stage) bt_recordNewStudent.getScene().getWindow();

        try {
            // Parent studentInfo = FXMLLoader.load(getClass().getResource("GetStudentInfo.fxml"));
            FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/GetStudentInfo.fxml"));
            GetStudentInfoController controller = new GetStudentInfoController(newUserName.getText());
            studentInfo.setController(controller);
            s.setScene(new Scene(studentInfo.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW USER When the Record New Student button is pressed. This will save
     * the username and start the camera running.
     *
     * @param event
     */
    @FXML
    private void recordNewStudent(ActionEvent event) {
        bt_newStudInfo.setDisable(false);
        if ((newUserName.getText() != null && !newUserName.getText().isEmpty())) {
            newname = newUserName.getText();
            startCameraMethod();
        }

    }

    // ***************** SEE STUDENT DATA FUNCTIONALITY ***************************
    /**
     * VIEW STUDENT When View Student button is pressed this action starts.
     *
     * @param event
     */
    @FXML
    void viewStudentButton(ActionEvent event) {
        goToStudentData(username);
    }

    /**
     * VIEW STUDENT
     *
     * @param username
     */
    private void goToStudentData(String username) {
        try {
            FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/StudentInfo.fxml"));
            Stage s = (Stage) bt_viewStudent.getScene().getWindow();
            StudentInfoController controller = new StudentInfoController(username);
            studentInfo.setController(controller);
            s.setScene(new Scene(studentInfo.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // **************************** FACIAL RECOGNITION FUNCTIONALITY *************************
    /**
     * CAMERA When the 'Start Camera' or 'Stop Camera' button is pressed.
     *
     * @param event
     */
    @FXML
    private void startCamera(ActionEvent event) {
        startCameraMethod();
    }

    /**
     * FACIAL RECOGNITION Begins the facial recognition methods
     */
    private void startCameraMethod() {

        this.checkboxSelection("./resources/haarcascades/haarcascade_frontalface_alt.xml");

        // set a fixed width for the frame
        originalFrame.setFitWidth(800);
        // preserve image ratio
        originalFrame.setPreserveRatio(true);

        if (!this.cameraActive) {
            // disable setting checkboxes
            //this.haarClassifier.setDisable(true);

            // disable 'New user' checkbox
            this.newUser.setDisable(true);

            // disable view student and new visit
            this.bt_viewStudent.setDisable(true);
            this.bt_recordVisit.setDisable(true);

            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {
                        Image imageToShow = grabFrame();
                        originalFrame.setImage(imageToShow);
                    }

                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 5, TimeUnit.MILLISECONDS);

                // update the button content
                this.cameraButton.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.cameraButton.setText("Start Camera");
            // enable classifiers checkboxes
            // this.haarClassifier.setDisable(false);
            // enable 'New user' checkbox
            //this.newUser.setDisable(false);
            // enable view student and visit
            if (username != null) {
                this.bt_viewStudent.setDisable(false);
                this.bt_recordVisit.setDisable(false);
                newUser.setDisable(true);
            }
            if(username == null){
                newUser.setDisable(false);
            }
            // stop the timer
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(5, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log the exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }

            // release the camera
            this.capture.release();
            // clean the frame
            this.originalFrame.setImage(null);

            // Clear the parameters for new user data collection
            index = 0;
            newname = "";
        }
    }

    /**
     * FACIAL RECOGNITION
     *
     * @return
     */
    private Image grabFrame() {
        // init everything
        Image imageToShow = null;
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // face detection
                    this.detectAndDisplay(frame);

                    // convert the Mat object (OpenCV) to Image (JavaFX)
                    imageToShow = mat2Image(frame);
                }

            } catch (Exception e) {
                // log the (full) error
                System.err.println("ERROR: " + e);
            }
        }

        return imageToShow;
    }

    /**
     * FACIAL RECOGNITION
     *
     * @param classifierPath
     */
    private void checkboxSelection(String classifierPath) {
        // load the classifier(s)
        this.faceCascade.load(classifierPath);

        // now the video capture can start
        this.cameraButton.setDisable(false);
    }

    /**
     * FACIAL RECOGNITION
     *
     * @param frame
     */
    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        try {
            this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
        } catch (CvException ce) {
            System.out.println("Running");
        } catch (Exception e) {
            System.out.println("Recognition unsuccessful");
        }

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);

            // Crop the detected faces
            Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
            Mat croppedImage = new Mat(frame, rectCrop);
            // Change to gray scale
            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
            // Equalize histogram
            Imgproc.equalizeHist(croppedImage, croppedImage);
            // Resize the image to a default size
            Mat resizeImage = new Mat();
            Size size = new Size(250, 250);
            Imgproc.resize(croppedImage, resizeImage, size);

            // text above box.
            String box_text = "";
            // check if 'New user' checkbox is selected
            // if yes start collecting training data (50 images is enough)
            if ((newUser.isSelected() && !(newname.isEmpty()))) {
                if (index < 10) {
                    Imgcodecs.imwrite("./resources/trainingset/combined/"
                            + random + "-" + newname + "_" + (index++) + ".png", resizeImage);
                    box_text = "Have student look into camera";
                } else {
                    box_text = "Finished recording new Student";
                }
            }

//			int prediction = faceRecognition(resizeImage);
            double[] returnedResults = faceRecognition(resizeImage);
            double prediction = returnedResults[0];
            double confidence = returnedResults[1];

//			System.out.println("PREDICTED LABEL IS: " + prediction);
            int label = (int) prediction;
            String name = "";
            if (names.containsKey(label)) {
                name = names.get(label);
            } else {
                name = "Unknown";
            }

            // Display text above green box
            if (!(newUser.isSelected())) {
                // The box text that is regularly shown
                if (Stranger(confidence) == true) {
                    // box_text = "Name: " + name + "Confidence: " + confidence;
                    box_text = "Unable to recognize." ;
                    username = null;//Please Register Andrew id if you are a new student";

                } else {
                    box_text = "Welcome to Carnegie Mellon " + name + "! ";
                    username = name;
                }
            }

            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // And now put it into the image:
            Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));

        }
    }

    /**
     * FACIAL RECOGNITION
     *
     * @param c
     * @return
     */
    public Boolean Stranger(Double c) {
        if (c > 37) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * FACIAL RECOGNITION
     *
     * @param currentFace
     * @return
     */
    private double[] faceRecognition(Mat currentFace) {

        // predict the label
        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result = -1;

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("traineddata");
        faceRecognizer.predict(currentFace, predLabel, confidence);
//        	result = faceRecognizer.predict_label(currentFace);
        result = predLabel[0];

        return new double[]{result, confidence[0]};
    }

    /**
     * FACIAL RECOGNITION
     *
     * @param frame
     * @return
     */
    private Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    /**
     * FACIAL RECOGNITION
     *
     */
    private void trainModel() {
        // Read the data from the training set
        File root = new File("./resources/trainingset/combined/");

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".png");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        imageFiles = root.listFiles();

        List<Mat> images = new ArrayList<Mat>();

        System.out.println("Number of images stored in database: " + imageFiles.length);

        List<Integer> trainingLabels = new ArrayList<>();
   
        Mat labels = new Mat(imageFiles.length, 1, CvType.CV_32SC1);

        int counter = 0;

        for (File image : imageFiles) {
            // Parse the training set folder files
            Mat img = Imgcodecs.imread(image.getAbsolutePath());
            // Change to Grayscale and equalize the histogram
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(img, img);
            // Extract label from the file name
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            // Extract name from the file name and add it to names HashMap
            String labnname = image.getName().split("\\_")[0];
            String name = labnname.split("\\-")[1];
            names.put(label, name);
            // Add training set images to images Mat
            images.add(img);

            labels.put(counter, 0, label);
            counter++;
        }
        //FaceRecognizer faceRecognizer = Face.createFisherFaceRecognizer(0,1500);
        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        //FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer(0,50);
        faceRecognizer.train(images, labels);
        faceRecognizer.save("traineddata");
    }
}
