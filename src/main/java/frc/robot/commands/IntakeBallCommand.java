// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

 package frc.robot.commands;

  import edu.wpi.first.wpilibj2.command.CommandBase;
  import frc.robot.subsystems.BallTransitSubsystem;

 public class IntakeBallCommand extends CommandBase {
   private final BallTransitSubsystem ballTransitSubsystem;
   public IntakeBallCommand(BallTransitSubsystem subsystem) {
     ballTransitSubsystem = subsystem;
     addRequirements(ballTransitSubsystem);
   }

//   // Called when the command is initially scheduled.
   @Override
   public void initialize() {}

//   // Called every time the scheduler runs while the command is scheduled.
   @Override
   public void execute() {
    //If piston is locked then release piston
    if (ballTransitSubsystem.getUpPiston()){
      ballTransitSubsystem.toggleUpLock();
     }
     //If the piston is locked then end command, if piston is unlocked then move arm down
     //This is assuming when piston is released then arm is up
     if(!ballTransitSubsystem.getDownPiston()){
       //ballTransitSubsystem.transitDown();//Piston should be locked after this method is complete
        ballTransitSubsystem.downPistonPosition = true;
     }
   }

//   //Turns off the motor after the command ends
   @Override
   public void end(boolean interrupted) {
    ballTransitSubsystem.turnOffArm();
   }

   // Returns true when the command should end.
   @Override
   public boolean isFinished() {
     //When the piston is locked, then arm is down
    if(ballTransitSubsystem.getDownPiston()){
      return true;
    }
    return false;
   }
 }
