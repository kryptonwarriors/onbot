package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import java.lang.annotation.Target;
import com.qualcomm.hardware.rev.RevTouchSensor;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaSkyStone;
import org.firstinspires.ftc.robotcore.external.tfod.TfodSkyStone;
import java.util.List;
import java.util.Locale;


@Autonomous(name = "BBB (Blocks to Java)", group = "")
public class BBB extends LinearOpMode {

  private DcMotor LeftForward;
  private DcMotor LeftBack;
  private DcMotor RightBack;
  private DcMotor RightForward;
  private DcMotor LinearActuator;
  private DcMotor LeftCascade;
  private DcMotor RightCascade;
  private VuforiaSkyStone vuforiaSkyStone;
  private TfodSkyStone tfodSkyStone;
  private DistanceSensor BackDistance;
  private RevTouchSensor LFBumper;
  private RevTouchSensor RFBumper;
  private Servo LeftFoundation;
  private Servo RightFoundation;
  private Servo LeftClamp;
  private Servo RightClamp;
  private double eyes;
  private double boxRightEdge;
  private double boxWidth;
  private double boxLeftEdge;
  private double SkystoneCenter;

  private Util util;

  int FORWARD = 0;
  int BACKWARD = 1;
  int LEFT = 2;
  int RIGHT = 3;
  int UP = 4;
  int WALL = 5;
  int park = UP;
  int RTurn = 6;
  int LTurn = 7;
  int EXTEND = 8;
  int RETRACT = 9;
  int THRESH = 15;
  int ALL_THRESH = 15;
  int TURNTHRESH = 30;

  @Override
  public void runOpMode() {

    //dhriti is the lead of the team
    //aman is the greatest
    //muthu and aarav are eh

    LeftForward = hardwareMap.dcMotor.get("LeftForward");
    RightForward = hardwareMap.dcMotor.get("RightForward");
    LeftBack = hardwareMap.dcMotor.get("LeftBack");
    RightBack = hardwareMap.dcMotor.get("RightBack");
    LinearActuator = hardwareMap.dcMotor.get("LinearActuator");
    LeftCascade = hardwareMap.dcMotor.get("LeftCascade");
    RightCascade = hardwareMap.dcMotor.get("RightCascade");
    vuforiaSkyStone = new VuforiaSkyStone();
    tfodSkyStone = new TfodSkyStone();
    BackDistance = hardwareMap.get(DistanceSensor.class, "BackDistance");
    LFBumper = hardwareMap.get(RevTouchSensor.class, "LFBumper");
    RFBumper = hardwareMap.get(RevTouchSensor.class, "RFBumper");
    LeftFoundation = hardwareMap.servo.get("LeftFoundation");
    RightFoundation = hardwareMap.servo.get("RightFoundation");
    LeftClamp = hardwareMap.servo.get("LeftClamp");
    RightClamp = hardwareMap.servo.get("RightClamp");


    vuforiaSkyStone.initialize(
        "Aep6rHH/////AAABme4+LfHFHUG+rWg5ePEH2PhmJqjgZILzDPD4pxfckKin9iIq+waMwELsjAS6l/M0nM3U1uaktfb3oAx9njXP/KlwvaExjQcQxkFil6zbo6X4uTwvwTjLgZRuBOdcc9XlBtZwsi9x4XHMQRm/29dqHdj6vQFeEhcSkd5L+UePPjnrRULYSWkKz7tatdLmMTSm70s7A7Eii4T+7evwoLwXnkvhGSo3x5gp5LaMWPFuVZsxWnazqB1zXFY2XjASeSk9bXiZsd/knmpqz7IsmuO0oaYczP+2AgB8M+GLizjcaPAXw/u/eziOjsQC1C/ZeOkOqH1wvjFX2jHPM7+l+i4sM/riBT1yMpqoB7h5zf6HLRQN", // vuforiaLicenseKey
        hardwareMap.get(WebcamName.class, "Webcam 1"), // cameraName
        "", // webcamCalibrationFilename
        true, // useExtendedTracking
        false, // enableCameraMonitoring
        VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES, // cameraMonitorFeedback
        0, // dx
        0, // dy
        0, // dz
        0, // xAngle
        0, // yAngle
        0, // zAngle
        true); // useCompetitionFieldTargetLocations
    // Set min confidence threshold to 0.7
    tfodSkyStone.initialize(vuforiaSkyStone, 0.7F, true, true);
    // Initialize TFOD before waitForStart.
    // Init TFOD here so the object detection labels are visible
    // in the Camera Stream preview window on the Driver Station.
    tfodSkyStone.activate();

    util = new Util( LeftFoundation, RightFoundation,
                    LeftClamp, RightClamp, LeftForward,
                    LeftBack, RightForward, RightBack,
                    LinearActuator, LeftCascade, RightCascade,
                    IMU, Color, BackDistance, RBBumper, RFBumper,
                    LBBumper, LFBumper);

    LeftFoundation.setPosition(0.68);
    RightFoundation.setPosition(0.22);
    LeftClamp.setPosition(1);
    RightClamp.setPosition(0.8);
    telemetry.addData("Distance", BackDistance.getDistance(DistanceUnit.INCH));
    telemetry.addData(">", "Press Play to start");
    telemetry.update();
    waitForStart();

    if (opModeIsActive()) {

      LeftCascade.setPower(0.2);
      RightCascade.setPower(-0.2);
      sleep(100);
      LeftCascade.setPower(0);
      RightCascade.setPower(0);

      runWithoutEncoders();
      LeftForward.setPower(-0.7);
      RightForward.setPower(0.7);
      LeftBack.setPower(-0.7);
      RightBack.setPower(0.7);

      double eyes = BackDistance.getDistance(DistanceUnit.INCH);

      //move until 13 inches from the alliance wall
      while (eyes < 12) {
        eyes = BackDistance.getDistance(DistanceUnit.INCH);
        telemetry.addData("> Distance ( INCH )", Double.parseDouble(JavaUtil.formatNumber(eyes, 2)));
        telemetry.update();
      }

      LeftForward.setPower(0);
      RightForward.setPower(0);
      LeftBack.setPower(0);
      RightBack.setPower(0);


      int move = Math.abs(LeftForward.getCurrentPosition());
      telemetry.addData("move", move);
      telemetry.update();

      tfodSkyStone.deactivate();
      vuforiaSkyStone.close();
      tfodSkyStone.close();



      util.MoveTank(FORWARD, 750, 0.5);
      LeftClamp.setPosition(0.8);
      RightClamp.setPosition(1);
      sleep(600);
      runWithoutEncoders();
      LinearActuator.setPower(0.4);
      sleep(1000);
      LinearActuator.setPower(0);
      LeftClamp.setPosition(1);
      RightClamp.setPosition(0.7);
      sleep(700);
      LeftCascade.setPower(-0.2);
      RightCascade.setPower(0.15);
      sleep(300);
      LeftCascade.setPower(0);
      RightCascade.setPower(0);
      util.MoveTank(BACKWARD, 1800, 0.7);

      //TODO: Go To Foundation and Drop the Skystone
      util.MoveTank(LEFT, 2700 + move, 0.8);
      LeftClamp.setPosition(0.8);
      RightClamp.setPosition(1);
      sleep(400);
      util.MoveTank(RIGHT, 1000, 0.8);
    }
  }


  private void DetectSkystone(double SkystoneCenter) {

    while (SkystoneCenter < 350 && opModeIsActive() && Math.abs(LeftForward.getCurrentPosition()) < 2200) {
      List<Recognition> recognitions = tfodSkyStone.getRecognitions();
      if (recognitions.size() == 0) {
        telemetry.addData("TFOD", "No items detected.");
      } else {
        int index = 0;
        for (Recognition recognition : recognitions) {
            if (recognition.getLabel().equals("Skystone")) {
              boxRightEdge = recognition.getRight();
              boxWidth = recognition.getWidth();
              boxLeftEdge = recognition.getLeft();
              SkystoneCenter = (boxRightEdge + boxLeftEdge) / 2;
              displayInfo(index, recognition);
              index = index + 1;
            }
          }
      }
      telemetry.addData("Skystone Center ", SkystoneCenter);
      telemetry.update();
    }
  }

  /**
   * Display info (using telemetry) for a recognized object.
   */
  private void displayInfo( int i, Recognition recog) {
    telemetry.addData("label " + i, recog.getLabel());
    telemetry.addData("Top Left" + i, recog.getLeft());
    telemetry.addData("Lower Right" + i, recog.getRight());
    telemetry.addData("Box Width" + i, recog.getWidth());
    telemetry.addData("Box Height" + i, recog.getHeight());
    telemetry.addData("Image Width" + i, recog.getImageWidth());
    telemetry.addData("Image Height" + i, recog.getImageHeight());
  }

  private void runWithoutEncoders() {
      LeftForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      LeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      RightForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      RightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }
  private void LinearEncoder_Function(int Direction, int TargetPosition, double Power)
  {
    int EXTEND = 8;
    int RETRACT = 9;

    LinearActuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    LinearActuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


    if (Direction == EXTEND) {
      LinearActuator.setTargetPosition(TargetPosition);
      LinearActuator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      LinearActuator.setPower(Power);
    }
    else if (Direction == RETRACT) {
      LinearActuator.setTargetPosition(-TargetPosition);
      LinearActuator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      LinearActuator.setPower(-Power);
    }

    int CurrentPosition = LinearActuator.getCurrentPosition();
    while ((Math.abs(Math.abs(CurrentPosition) - Math.abs(TargetPosition)) > 15) && !(isStopRequested())) {

        CurrentPosition = LinearActuator.getCurrentPosition();

        telemetry.addData("key", "Linear Actuator is being stupid");
        telemetry.addData("CurrentPosition", CurrentPosition);
        telemetry.addData("TargetPosition", TargetPosition);
        telemetry.update();
      }

    LinearActuator.setPower(0);
    sleep(200);

  }

}
