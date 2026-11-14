# Contributing

Thanks for helping improve DriveTrain / GRoboFTC.

## Before making changes
- Check whether the behavior already exists in a subsystem or OpMode.
- Keep hardware names and robot-specific tuning values in RobotConfig.java.
- Prefer extending an existing subsystem over duplicating hardware control in an OpMode.
- Keep OpModes focused on sequencing and gamepad input.
- Do not commit credentials, private team information or unrelated Android Studio build output.

## Code conventions
- Keep package declarations consistent with the directory structure under org.firstinspires.ftc.teamcode.grobo.
- Use descriptive class, method and constant names.
- Comment non-obvious control logic, calibration assumptions and hardware-specific behavior.
- Keep motor/servo direction and tuning changes explicit in RobotConfig.java.
- Avoid combining unrelated robot behavior changes in one commit.

## Testing expectations
1. Build in the team's actual FTC SDK Android Studio project.
2. Check that Robot Configuration names match RobotConfig.java.
3. Test changed mechanisms at low power in a clear, controlled area.
4. Verify encoder signs, IMU orientation, motor directions and servo limits when relevant.
5. Test the affected OpMode on the robot and report the FTC SDK / FTCLib versions used.
6. Describe calibration and hardware assumptions in the pull request.

A successful compile does not prove that a mechanism is configured or tuned safely.

## Pull requests
Include a concise summary and reason, affected files/subsystems, build and test results (or what was not tested), hardware/configuration changes, and any values that need real-robot calibration.