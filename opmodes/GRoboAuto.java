package org.firstinspires.ftc.teamcode.grobo.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.grobo.GRobot;

/*
 * ΠΑΡΑΔΕΙΓΜΑ AUTONOMOUS
 * Δείχνει πόσο απλό γίνεται ένα πρόγραμμα με την GRobot.
 */
@Autonomous(name = "GRoboFTC - Auto", group = "GRoboFTC")
public class GRoboAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        GRobot robot = new GRobot(this);

        telemetry.addLine("Έτοιμο. Στήσε το ρομπότ ίσια και πάτα START.");
        telemetry.update();
        waitForStart();

        robot.drive.resetHeading();

        robot.drive.driveCm(60);       // 60 cm μπροστά
        robot.drive.turnDeg(90);       // 90° αριστερά
        robot.drive.driveCm(30);       // 30 cm μπροστά
        robot.drive.turnToHeading(0);  // ξανά στην αρχική κατεύθυνση
        robot.drive.driveCm(-90);      // 90 cm πίσω

        robot.drive.stop();
        robot.vision.close();
    }
}
