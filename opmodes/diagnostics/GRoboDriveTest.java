package org.firstinspires.ftc.teamcode.grobo.opmodes.diagnostics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.grobo.GRobot;

/*
 * ΤΕΣΤ ΣΑΣΙ: encoders, γυροσκόπιο, driveCm, turnDeg
 * Χρησιμοποιεί τις ΙΔΙΕΣ ρυθμίσεις (RobotConfig) με το Autonomous.
 *
 * ΒΗΜΑ 1 - χωρίς να πατήσεις τίποτα:
 *   ΣΠΡΩΞΕ το ρομπότ με το χέρι ΜΠΡΟΣΤΑ.
 *   Και οι 4 encoders πρέπει να ΑΥΞΑΝΟΝΤΑΙ.
 *   Όποιος μειώνεται -> -1 στη θέση του στο ENC_SIGN του RobotConfig.
 *   Όποιος μένει 0   -> έλεγξε το καλώδιο του encoder.
 *
 * ΒΗΜΑ 2 - ΓΥΡΝΑ το ρομπότ με το χέρι ΑΡΙΣΤΕΡΑ:
 *   Η γωνία πρέπει να ΑΥΞΑΝΕΤΑΙ. Αν δεν αλλάζει -> HUB_LOGO / HUB_USB.
 *
 * ΒΗΜΑ 3 - κουμπιά (gamepad1):
 *   A = 20 cm μπροστά      B = 20 cm πίσω
 *   X = στροφή 90° αριστερά Y = στροφή 90° δεξιά
 *   DPAD ↑ = μηδενισμός encoders και γωνίας
 */
@TeleOp(name = "GRoboFTCTest - Drive", group = "GRoboFTCTest")
public class GRoboDriveTest extends LinearOpMode {

    static final double TEST_CM  = 20;
    static final double TEST_DEG = 90;

    @Override
    public void runOpMode() {
        GRobot robot = new GRobot(this);

        while (opModeInInit()) {
            showStatus(robot);
            telemetry.addLine("Σπρώξε μπροστά: και οι 4 encoders πρέπει να ανεβαίνουν");
            telemetry.update();
            sleep(50);
        }

        while (opModeIsActive()) {
            if (gamepad1.a) robot.drive.driveCm(TEST_CM);
            if (gamepad1.b) robot.drive.driveCm(-TEST_CM);
            if (gamepad1.x) robot.drive.turnDeg(TEST_DEG);
            if (gamepad1.y) robot.drive.turnDeg(-TEST_DEG);
            if (gamepad1.dpad_up) {
                robot.drive.resetEncoders();
                robot.drive.resetHeading();
            }

            showStatus(robot);
            telemetry.addLine("A/B = 20 cm μπρος/πίσω, X/Y = 90° αριστ/δεξιά, DPAD↑ = μηδενισμός");
            telemetry.update();
        }
        robot.drive.stop();
    }

    private void showStatus(GRobot robot) {
        telemetry.addData("FL / FR", "%d / %d", robot.drive.encoder(0), robot.drive.encoder(1));
        telemetry.addData("BL / BR", "%d / %d", robot.drive.encoder(2), robot.drive.encoder(3));
        telemetry.addData("Θέση", "%.1f cm", robot.drive.positionCm());
        telemetry.addData("Γωνία", "%.1f°", robot.drive.heading());
    }
}