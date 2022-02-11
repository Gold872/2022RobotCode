// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.commands.AutoDriveCommand;

public class DriveSubsystem extends SubsystemBase {
  private final CANSparkMax leftFrontMotor = new CANSparkMax(Constants.leftFrontMotorID, MotorType.kBrushless);
  private final CANSparkMax leftBackMotor = new CANSparkMax(Constants.leftBackMotorID, MotorType.kBrushless);
  private final CANSparkMax rightFrontMotor = new CANSparkMax(Constants.rightFrontMotorID, MotorType.kBrushless);
  private final CANSparkMax rightBackMotor = new CANSparkMax(Constants.rightBackMotorID, MotorType.kBrushless);

  private RelativeEncoder m_leftFrontEncoder = leftFrontMotor.getEncoder();
  private RelativeEncoder leftBackEncoder = leftBackMotor.getEncoder();
  private RelativeEncoder m_rightFrontEncoder = rightFrontMotor.getEncoder();
  private RelativeEncoder rightBackEncoder = rightBackMotor.getEncoder();

  private SparkMaxPIDController leftFrontPIDCon = leftFrontMotor.getPIDController();
  private SparkMaxPIDController leftBackPIDCon = leftBackMotor.getPIDController();
  private SparkMaxPIDController rightFrontPIDCon = rightFrontMotor.getPIDController();
  private SparkMaxPIDController rightBackPIDCon = rightBackMotor.getPIDController();

  

  public final AnalogInput ultrasonic = new AnalogInput(0);
  int smartMotionSlot = 0;
  int allowedErr;
  int minVel;
  double kP = 4e-4;
  double kI = 0;
  double kD = 0;
  double kIz = 0;
  double kFF = 0.000156;
  double kMaxOutput = 1;
  double kMinOutput = -1;
  double maxRPM = 5700;
  double maxVel = 4000;
  double maxAcc = 1500;
  double setPointDrive = 0;
  // The gyro sensor
  public static final AHRS m_gyro = new AHRS(SerialPort.Port.kUSB1);
  //private PowerDistribution powerDistributionModule = new PowerDistribution(0, ModuleType.kCTRE);

  public DriveSubsystem() {
    resetGyro();
    Shuffleboard.getTab("Example tab").add(m_gyro);
    leftFrontMotor.restoreFactoryDefaults();
    leftBackMotor.restoreFactoryDefaults();
    rightFrontMotor.restoreFactoryDefaults();
    rightBackMotor.restoreFactoryDefaults();
    initializePID(leftFrontPIDCon, m_leftFrontEncoder);
    initializePID(leftBackPIDCon, leftBackEncoder);
    initializePID(rightFrontPIDCon, m_rightFrontEncoder);
    initializePID(rightBackPIDCon, rightBackEncoder);
    resetEncoders();
  }

  public void manualDrive(double x, double y, double scaleX, double scaleY) {
    if (Math.abs(x) <= 0.5 && Math.abs(y) <= 0.01) {
      leftFrontMotor.set(0);
      rightFrontMotor.set(0);
      leftBackMotor.set(0);
      rightBackMotor.set(0);
    } else {
      leftFrontPIDCon.setReference(setPointLeft(x, y, scaleX, scaleY), CANSparkMax.ControlType.kSmartVelocity);
      leftBackPIDCon.setReference(setPointLeft(x, y, scaleX, scaleY), CANSparkMax.ControlType.kSmartVelocity);
      rightFrontPIDCon.setReference(setPointRight(x, y, scaleX, scaleY), CANSparkMax.ControlType.kSmartVelocity);
      rightBackPIDCon.setReference(setPointRight(x, y, scaleX, scaleY), CANSparkMax.ControlType.kSmartVelocity);
    }
  }

  public double setPointLeft(double Jx, double Jy, double scaleX, double scaleY) {
    double yScale = ((Jy) * scaleY);
    double xScale = (Jx) * scaleX;
    return xScale + yScale;
  }

  public double setPointRight(double Jx, double Jy, double scaleX, double scaleY) {
    double xScale = (-(Jx) * scaleX);
    double yScale = ((Jy) * scaleY);
    return -1 * (xScale + yScale);
  }

  public void autoDrive(double displacement) {
    leftFrontPIDCon.setReference(displacement, CANSparkMax.ControlType.kSmartMotion);
    leftBackPIDCon.setReference(displacement, CANSparkMax.ControlType.kSmartMotion);
    rightFrontPIDCon.setReference(-displacement, CANSparkMax.ControlType.kSmartMotion);
    rightBackPIDCon.setReference(-displacement, CANSparkMax.ControlType.kSmartMotion);
  }
  // Neeed to get rid of this soon
  public double angleError(double expectedAngle){
    double angleSubtract = Math.IEEEremainder(expectedAngle, 360) - Math.IEEEremainder(m_gyro.getAngle(), 360);
    if (angleSubtract > 180) {
      return angleSubtract - 360;

    } else if (angleSubtract < -180) {
      return angleSubtract + 360;

    } else {
      return angleSubtract;
    }
  }

  public void resetGyro() {
    m_gyro.calibrate();
    m_gyro.reset();
  }

  public double distanceError(double expectedDistance) {
    return expectedDistance - (ultrasonic.getValue() * 0.125);
  }

  public boolean pointReached(double displacement) {
    if (Math.abs(m_leftFrontEncoder.getPosition()) >= Math.abs(displacement) - 1) {
      resetEncoders();
      return true;
    }
    return false;
  }

  public void resetEncoders() {
    m_leftFrontEncoder.setPosition(0);
    leftBackEncoder.setPosition(0);
    m_rightFrontEncoder.setPosition(0);
    rightBackEncoder.setPosition(0);
  }

  public void initializePID(SparkMaxPIDController p, RelativeEncoder h) {
    p.setP(kP);
    p.setI(kI);
    p.setD(kD);
    p.setIZone(kIz);
    p.setFF(kFF);
    p.setOutputRange(kMinOutput, kMaxOutput);
    p.setSmartMotionMaxVelocity(maxVel, smartMotionSlot);
    p.setSmartMotionMinOutputVelocity(minVel, smartMotionSlot);
    p.setSmartMotionMaxAccel(maxAcc, smartMotionSlot);
    p.setSmartMotionAllowedClosedLoopError(allowedErr, smartMotionSlot);
  }

  @Override
  public void periodic() {
    double processVariable = leftBackEncoder.getVelocity();
    
    //SmartDashboard.putNumber("PDP Thing", powerDistributionModule.getModule());

    // This method will be called once per scheduler run
    
    SmartDashboard.putNumber("Ultrasonic", ultrasonic.getValue() * 0.125);
    SmartDashboard.putNumber("Postion", leftBackEncoder.getPosition());
    SmartDashboard.putNumber("Velocity", leftBackEncoder.getVelocity());
    SmartDashboard.putNumber("Joystick x", RobotContainer.driverStick.getX());
    SmartDashboard.putNumber("Joystick y", RobotContainer.driverStick.getY());
    SmartDashboard.putNumber("Process Variable", processVariable);
    SmartDashboard.putNumber("Output", leftBackMotor.getAppliedOutput());
    SmartDashboard.putBoolean("Collision Detected?", AutoDriveCommand.collisionDetected);
    /*SmartDashboard.putNumber("Total Current", powerDistributionModule.getTotalCurrent());
    SmartDashboard.putNumber("Total Power", powerDistributionModule.getTotalPower());
    SmartDashboard.putNumber("Total Energy", powerDistributionModule.getTotalEnergy());
    SmartDashboard.putNumber("Voltage -_-", powerDistributionModule.getVoltage());*/

   /* SmartDashboard.putNumber("Current of Motor 4", powerDistributionModule.getCurrent(14));
    SmartDashboard.putNumber("Current of Motor 3", powerDistributionModule.getCurrent(13));
    SmartDashboard.putNumber("Current of Motor 1", powerDistributionModule.getCurrent(12));
    SmartDashboard.putNumber("Current of Motor 2", powerDistributionModule.getCurrent(15));*/
    /*SmartDashboard.putNumber("Current of Motor 0", powerDistributionModule.getCurrent(0));
    SmartDashboard.putNumber("Current of Motor 1", powerDistributionModule.getCurrent(1));
    SmartDashboard.putNumber("Current of Motor 2", powerDistributionModule.getCurrent(2));
    SmartDashboard.putNumber("Current of Motor 3", powerDistributionModule.getCurrent(3));*/
    //BallTransitSubsystem.toggleIntake(Constants.Buttons.intakeBallToggle);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
