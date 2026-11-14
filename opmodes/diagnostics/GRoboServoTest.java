package org.firstinspires.ftc.teamcode.grobo.opmodes.diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.grobo.GRobot;

/*
 * ΤΕΣΤ SERVO: για να βρείτε τις θέσεις REST και PUSH των servo shooter.
 *   DPAD ↑ / ↓   μετακινεί το servo NECTAR
 *   Y / A        μετακινεί το servo POLLEN
 * Γράψτε τις θέσεις που βλέπετε στην οθόνη στο RobotConfig
 * (NECTAR_SERVO_REST / PUSH, POLLEN_SERVO_REST / PUSH).
 */
@TeleOp(name = "GRoboFTCTest - Servo Positions", group = "GRoboFTCTest")
public class GRoboServoTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        GRobot robot = new GRobot(this);
        double nectarPos = 0.5, pollenPos = 0.5;

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.dpad_up)   nectarPos += 0.005;
            if (gamepad1.dpad_down) nectarPos -= 0.005;
            if (gamepad1.y)         pollenPos += 0.005;
            if (gamepad1.a)         pollenPos -= 0.005;
            nectarPos = Math.max(0, Math.min(1, nectarPos));
            pollenPos = Math.max(0, Math.min(1, pollenPos));

            robot.nectar.setServo(nectarPos);
            robot.pollen.setServo(pollenPos);

            telemetry.addData("NECTAR servo", "%.3f  (DPAD ↑/↓)", nectarPos);
            telemetry.addData("POLLEN servo", "%.3f  (Y/A)", pollenPos);
            telemetry.update();
            sleep(20);
        }
    }
}
