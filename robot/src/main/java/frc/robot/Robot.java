// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //right motors
  private static final int rightfrontID = 3;
  private static final int rightrearID = 4;
  private final SparkMaxWrapper rightfrontMotor = new SparkMaxWrapper(rightfrontID, MotorType.kBrushless);
  private final SparkMaxWrapper rightrearMotor = new SparkMaxWrapper(rightrearID, MotorType.kBrushless);
  

  //left motors
  private static final int leftfrontID = 1;
  private static final int leftrearID = 2;
  private final SparkMaxWrapper leftfrontMotor = new SparkMaxWrapper(leftfrontID, MotorType.kBrushless);
  private final SparkMaxWrapper leftrearMotor = new SparkMaxWrapper(leftrearID, MotorType.kBrushless);

  //ejector motors
  private static final int leftEjectorID = 5;
  private static final int rightEjectorID = 6;
  private final SparkMaxWrapper leftEjector = new SparkMaxWrapper(leftEjectorID, MotorType.kBrushed);
  private final SparkMaxWrapper rightEjector = new SparkMaxWrapper(rightEjectorID, MotorType.kBrushed);
  
  //second ejector motors
   

  //Controller
  private PS4Controller controller = new PS4Controller(0) ;
  

  private MecanumDrive m_robotDrive;
  double currentTime = 0.0;
  Timer timer = new Timer();
  double ySpeed = 0;

  

  @Override
  public void robotInit() {

               
    rightfrontMotor.setSmartCurrentLimit(10);
    rightrearMotor.setSmartCurrentLimit(10);
    leftfrontMotor.setSmartCurrentLimit(10);
    leftrearMotor.setSmartCurrentLimit(10);

    rightfrontMotor.restoreFactoryDefaults();
    rightrearMotor.restoreFactoryDefaults();
    leftfrontMotor.restoreFactoryDefaults();
    leftrearMotor.restoreFactoryDefaults();

    leftEjector.restoreFactoryDefaults();
    rightEjector.restoreFactoryDefaults();
  

    rightfrontMotor.setInverted(false);
    rightrearMotor.setInverted(false);
    leftfrontMotor.setInverted(true);
    leftrearMotor.setInverted(true);

    m_robotDrive = new MecanumDrive(leftfrontMotor, leftrearMotor, rightfrontMotor, rightrearMotor);
    m_robotDrive.setDeadband(0.2);
    m_robotDrive.setMaxOutput(0.25);
    m_robotDrive.setSafetyEnabled(false);

    leftEjector.setSmartCurrentLimit(40);
    rightEjector.setSmartCurrentLimit(40);

    rightEjector.restoreFactoryDefaults();
    leftEjector.restoreFactoryDefaults();

  }

  @Override

  public void robotPeriodic() {}
  
  @Override
    public void autonomousInit() {
      int position = 1;
      String colour = "red";
      ySpeed = 0;
      timer.reset();
      timer.start();
      currentTime = System.currentTimeMillis();
      m_robotDrive.driveCartesian(0, 0,0);

      if((position == 1) && (colour == "red")){
        ySpeed = -0.25;
      }
      if((position == 2) && (colour == "red")){
        ySpeed = 0.25;
      }
      if((position == 3) && (colour == "red")){
        ySpeed = 0.5;
      }
      if((position == 1) && (colour == "blue")){
        ySpeed = 0.25;
      }
      if((position == 2) && (colour == "blue")){
        ySpeed = -0.25;
      }
      if((position == 3) && (colour == "blue")){
        ySpeed = -0.5;
      }

    }

    @Override
    public void autonomousPeriodic() {
      // Position 1 RED

      // 
      if(currentTime - System.currentTimeMillis() < 2500){
        m_robotDrive.driveCartesian(-0.5, 0,0);

      }
      
      if((System.currentTimeMillis() - currentTime > 2500) && (System.currentTimeMillis() - currentTime < 4000)){
        m_robotDrive.driveCartesian(0, 0, 0.5);
      }

      if((System.currentTimeMillis() - currentTime > 4000) && (System.currentTimeMillis() - currentTime < 5000)){
        m_robotDrive.driveCartesian(0, ySpeed, 0);
      }

      // Throw the ring
      if((System.currentTimeMillis() - currentTime > 5000) && (System.currentTimeMillis() - currentTime < 10000)){
        m_robotDrive.driveCartesian(0, 0, 0);
        leftEjector.setVoltage(4);
        rightEjector.setVoltage(-4);

      }
      if((System.currentTimeMillis() - currentTime) > 10000) {
        m_robotDrive.driveCartesian(0, 0, 0);
        leftEjector.setVoltage(0);
        rightEjector.setVoltage(0);
      }
    }
  

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    double y = 0;           //variable for forward/backward movement
    double x = 0;           //variable for side to side movement
    double turn = 0;        //variable for turning movement
    double deadzone = 0.3;	//variable for amount of deadzone
    
    // Deadzone for joystick

    if(controller.getLeftY() > deadzone || controller.getLeftY() < -deadzone) {
      x = controller.getLeftY();
    }
      
    if(controller.getLeftX() > deadzone || controller.getLeftX() < -deadzone) {
      y = -controller.getLeftX();
    }
    
    if(controller.getRightX() > deadzone || controller.getRightX() < -deadzone){
      turn = controller.getRightX();
    }
             
  

    m_robotDrive.driveCartesian(x, y, -turn);

    
    // Take the ring
    if (controller.getCircleButtonPressed()) {
      leftEjector.setVoltage(-6);
      rightEjector.setVoltage(6);
   }
   if (controller.getCircleButtonReleased()) {
    leftEjector.setVoltage(0);
    rightEjector.setVoltage(0);
    leftEjector.stopMotor();
    rightEjector.stopMotor();
    
   }
   // Throw the ring
   if (controller.getSquareButtonPressed()) {
      leftEjector.setVoltage(10);
      rightEjector.setVoltage(-10);
   }
   if (controller.getSquareButtonReleased()) {
    leftEjector.setVoltage(0);
    rightEjector.setVoltage(0);
   }
 
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
  timer.reset();  
  timer.start();
  currentTime = System.currentTimeMillis();
  }

  @Override
  public void testPeriodic() {
  if (controller.getSquareButtonPressed()) {
    while (currentTime - System.currentTimeMillis() < 5000){
        m_robotDrive.driveCartesian(-0.5, 0,0);

  }
  
}
  }
  {
  if (controller.getSquareButtonReleased()) {
    m_robotDrive.driveCartesian(0, 0,0);

  }}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic(){}
}