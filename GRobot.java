package org.firstinspires.ftc.teamcode.grobo;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.grobo.subsystems.Aim;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Box;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Drive;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Intake;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.grobo.subsystems.Vision;

/*
 * ΤΟ ΡΟΜΠΟΤ
 * =========
 * Στήνει όλο το hardware μία φορά και δίνει πρόσβαση στα υποσυστήματα:
 *
 *   GRobot robot = new GRobot(this);
 *   robot.drive.driveCm(50);
 *   robot.nectar.spinUp();
 *   robot.intake.in();
 *
 * Για νέα λειτουργία: φτιάχνετε κλάση στο subsystems και την προσθέτετε εδώ.
 */
public class GRobot {

    public final Drive   drive;
    public final Vision  vision;
    public final Aim     aim;
    public final Intake  intake;
    public final Box     box;
    public final Shooter nectar;
    public final Shooter pollen;

    public GRobot(LinearOpMode opMode) {
        drive  = new Drive(opMode);
        vision = new Vision(opMode);
        aim    = new Aim(drive, vision);
        intake = new Intake(opMode);
        box    = new Box(opMode);

        nectar = new Shooter(opMode, "NECTAR",
                M_FLYWHEEL_NECTAR, NECTAR_FLYWHEEL_REVERSED, NECTAR_RPM,
                S_FLOWER_NECTAR,   NECTAR_FLOWER_REVERSED,   NECTAR_FLOWER_POWER,
                S_NECTAR,          NECTAR_SERVO_REST,        NECTAR_SERVO_PUSH);

        pollen = new Shooter(opMode, "POLLEN",
                M_FLYWHEEL_POLLEN, POLLEN_FLYWHEEL_REVERSED, POLLEN_RPM,
                S_FLOWER_POLLEN,   POLLEN_FLOWER_REVERSED,   POLLEN_FLOWER_POWER,
                S_POLLEN,          POLLEN_SERVO_REST,        POLLEN_SERVO_PUSH);
    }

    /** Σταματά όλα τα μοτέρ. */
    public void stopAll() {
        drive.stop();
        intake.stop();
        box.stop();
        nectar.stop();
        pollen.stop();
    }
}
