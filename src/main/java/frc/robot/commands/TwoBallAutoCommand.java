// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import frc.robot.subsystems.BallTransitSubsystem;
import frc.robot.subsystems.DriveSubsystem;

public class TwoBallAutoCommand extends SequentialCommandGroup {
  /**
   * This is where most of the auto code should go.
   * It should be built by using commands that are running seqentially
   * to prevent code from being repeated
   * @param drive
   * @param transitSubsystem
   */
  public TwoBallAutoCommand(DriveSubsystem drive, BallTransitSubsystem transitSubsystem) {
    //Make Sure to have a timeout after every Command, just incase the command doesn't end
    //Use ToggleArm Command because at the end of Auto the robot will be disable and the arm will drop
    addCommands(
      //Command list of wanted movement
      //new InstantCommand(()->ballTransitSubsystem.releaseArm(), transitSubsystem),
      //new StartEndCommand(()->transitSubsystem.outTake(), ()->transitSubsystem.turnOffIntakeMotor(), transitSubsystem).withTimeout(1),
      new AutoDriveCommand(drive, -8.41 *  (23.125 / (6 * Math.PI))),
      new TurnToAngleCommand(drive, 146),
      new AutoDriveCommand(drive, 8.41 * (69 / (6 * Math.PI))),
      new TurnToAngleCommand(drive, -89),
      new AutoDriveCommand(drive, 8.41 * (107.1875 / (6 * Math.PI))),
      new TurnToAngleCommand(drive, 40),
      new AutoDriveCommand(drive, 8.41 * ( 100.44 / (6 * Math.PI))),
      new TurnToAngleCommand(drive, 2),
      new AutoDriveCommand(drive, 8.41 * (5 / (6 * Math.PI)))

        //Tests
        /*new SetDistanceCommand(drive, 80),

        new TurnToAngleCommand(drive, -91),

        new AutoDriveCommand(drive, 50),

        new TurnToAngleCommand(drive, 90),

        new AutoDriveCommand(drive, 50),

        new TurnToAngleCommand(drive, 0),

        new AutoDriveCommand(drive, -25).withInterrupt(transitSubsystem::getDownPiston)*/
        
        // new AutoDriveCommand(drive, -35)
       // new AutoDriveCommand(drive, 50)*/
        // Turns to specified angle
        /*new AlignCommand(drive, 50), // Enter wanted angle

        new ParallelRaceGroup(
            new AutoDriveCommand(drive,50), // Drives the robot to specifed distance,
                                                                             // stops after two seconds
            new IntakeBallCommand(transitSubsystem).withTimeout(2)),

        new AutoDriveCommand(drive,50),

        new DumpBallCommand(transitSubsystem).withTimeout(1)*/);
        
  }
}
