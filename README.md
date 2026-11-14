# DriveTrain — GRoboFTC

FTC robot-control source code for the GRoboFTC robot. The code is organized around reusable subsystems and a central RobotConfig class.

> **Status:** Java source snapshot only. This is not a standalone Android Studio / FTC SDK project.

## Features
- Mecanum drivetrain using FTCLib.
- Encoder-based straight-line movement with heading correction and timeout.
- REV Control Hub IMU heading and turns.
- Vision Portal / AprilTag cluster detection with alliance filtering.
- Automatic aiming using target bearing, range and yaw.
- Two shooter assemblies (Nectar and Pollen), each with flywheel, feed motor and pusher servo.
- Intake and Box mechanisms.
- Diagnostic OpModes for drive/encoder/IMU checks and servo positioning.
- Configurable one-gamepad or two-gamepad TeleOp.

## Source files
| File | Purpose |
|---|---|
| GRobot.java | Creates and exposes all robot subsystems; provides stopAll(). |
| RobotConfig.java | Hardware names, encoder signs, dimensions and tuning constants. |
| opmodes/GRoboTeleOp.java | Main driver-controlled OpMode. |
| opmodes/GRoboAuto.java | Demonstration autonomous sequence. |
| opmodes/diagnostics/GRoboDriveTest.java | Encoder, IMU and drivetrain diagnostics. |
| opmodes/diagnostics/GRoboServoTest.java | Shooter servo position calibration. |
| subsystems/Drive.java | Mecanum drive, encoder movement and IMU turns. |
| subsystems/Vision.java | Camera setup, alliance filtering and target measurements. |
| subsystems/Aim.java | Vision-assisted aiming controller. |
| subsystems/Intake.java | Intake motor control. |
| subsystems/Box.java | Box motor and encoder-position helper. |
| subsystems/Shooter.java | Reusable flywheel, feed motor and servo controller. |

All Java files use package root org.firstinspires.ftc.teamcode.grobo. In a normal FTC SDK project, place them under TeamCode/src/main/java/org/firstinspires/ftc/teamcode/grobo/, preserving the opmodes and subsystems subfolders.

## Requirements
- A compatible FIRST Tech Challenge SDK Android Studio project.
- FTCLib configured in that project's Gradle dependencies (Drive uses FTCLib Motor and MecanumDrive).
- A REV Hub configuration matching the hardware names below.
- A webcam configured as Webcam 1 for vision.
- An FTC SDK / Vision library version that includes the AprilTag cluster APIs imported by Vision.java.

This repository does not include Gradle wrapper files, FTC SDK modules or dependency declarations. Use the versions already selected by your team's FTC project; this source snapshot does not build by itself.

## Hardware configuration
Names must match the Driver Station Robot Configuration exactly.

| Device | Configuration name |
|---|---|
| Front-left drive motor | leftFMotor |
| Front-right drive motor | rightFMotor |
| Back-left drive motor | leftBMotor |
| Back-right drive motor | rightBMotor |
| Intake motor | intakeMotor |
| Box motor | boxMotor |
| Nectar flywheel | FWNectar |
| Pollen flywheel | FWPollen |
| Nectar feed motor | mSNectar |
| Pollen feed motor | mSPollen |
| Nectar pusher servo | mSSNectar |
| Pollen pusher servo | mSSPollen |
| Webcam | Webcam 1 |
| IMU | imu |

## RobotConfig
Review RobotConfig.java before running the robot. Important settings include:
- HUB_LOGO and HUB_USB: physical REV Hub orientation.
- TICKS_PER_REV, WHEEL_DIAMETER_CM, GEAR_RATIO and CALIBRATION: drivetrain distance conversion.
- ENC_SIGN: encoder signs in front-left, front-right, back-left, back-right order.
- SINGLE_GAMEPAD: false selects two controllers; true selects one controller.
- Motor reversal flags, shooter RPM/PIDF, feed power and servo REST/PUSH positions.
- Camera exposure, gain, decimation and aiming tolerances.

Committed values are starting/configuration values, not guaranteed competition-ready tuning. Verify directions, encoder signs, distances, shooter speeds and servo positions on the actual robot.

## OpModes and controls
### Main TeleOp
Select GRoboFTC - TeleOp on the Driver Station.

During INIT, D-pad Left selects Blue alliance and D-pad Right selects Red alliance. Telemetry reports whether the camera and mechanisms were found.

With the default SINGLE_GAMEPAD = false:
| Controller | Input | Action |
|---|---|---|
| Gamepad 1 | Left stick | Drive / strafe |
| Gamepad 1 | Right stick X | Turn |
| Gamepad 1 | Hold LB | Slow precision drive |
| Gamepad 1 | Hold RB | Vision-assisted aiming |
| Gamepad 1 | X / A | Toggle intake in / out |
| Gamepad 2 | D-pad Up / Down | Move Box |
| Gamepad 2 | Y / B | Toggle Nectar / Pollen flywheel |
| Gamepad 2 | RB / LB | Fire Nectar / Pollen |

With SINGLE_GAMEPAD = true, mechanisms move to Gamepad 1: D-pad Up/Down controls Box, Y/B toggles Nectar/Pollen, and holding RT fires whichever shooter is running. Intake remains on X/A; RB is used for aiming.

Firing is gated by FIRE_ONLY_WHEN_READY and the configured flywheel RPM tolerance. Aiming checks target bearing, range (when enabled) and yaw (when tag-squaring is enabled).

### Autonomous example
GRoboFTC - Auto is a demonstration, not a finished match autonomous. It drives 60 cm, turns 90 degrees left, drives 30 cm, returns to heading 0 degrees, then drives 90 cm backward. Validate and adapt the path on the actual field before use.

### Diagnostics
- GRoboFTCTest - Drive: inspect encoder values and heading; A/B move 20 cm forward/backward, X/Y turn 90 degrees, D-pad Up resets encoders and yaw.
- GRoboFTCTest - Servo Positions: D-pad Up/Down adjusts Nectar; Y/A adjusts Pollen. Transfer measured positions to RobotConfig.java.

## First-time setup checklist
1. Open your team's FTC SDK Android Studio project.
2. Add FTCLib using the dependency version approved for that project.
3. Copy the Java files into the package structure described above.
4. Match every Robot Configuration name to the hardware table.
5. Confirm the physical Control Hub orientation.
6. With the robot safely supported and the mechanism area clear, check motor directions and encoder signs at low power.
7. Calibrate drivetrain distance and heading behavior.
8. Verify servo endpoints and shooter RPM/PIDF values.
9. Test camera detection and alliance selection.
10. Run autonomous only after validating its path in a controlled practice area.

## Known limitations
- No Gradle project/build configuration or automated tests are included.
- The autonomous OpMode is a short example, not a complete match strategy.
- Distance movement is encoder-based straight-line movement, not a trajectory/path-planning system.
- Vision depends on season-specific AprilTag cluster APIs and a correctly configured camera.
- Hardware constants and PIDF values require real-robot calibration.
- Review and test against the exact FTC SDK and FTCLib versions used by the team.

## Contributing
See CONTRIBUTING.md for code conventions and testing expectations.

## License
This project is licensed under the MIT License; see LICENSE. The MIT License covers original code in this repository only. FTC SDK, FTCLib, Android libraries and other third-party dependencies remain under their own licenses.