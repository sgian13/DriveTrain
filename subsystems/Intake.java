package org.firstinspires.ftc.teamcode.grobo.subsystems;

import static org.firstinspires.ftc.teamcode.grobo.RobotConfig.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * INTAKE (mI)
 *   in()    μαζεύει
 *   out()   βγάζει
 *   stop()
 */
public class Intake {

    private final DcMotor motor;

    public Intake(LinearOpMode opMode) {
        motor = opMode.hardwareMap.tryGet(DcMotor.class, M_INTAKE);
        if (motor == null) return;
        motor.setDirection(INTAKE_REVERSED ? DcMotorSimple.Direction.REVERSE
                : DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public boolean isAvailable() { return motor != null; }

    public void in()   { setPower(INTAKE_POWER); }
    public void out()  { setPower(-INTAKE_REVERSE_POWER); }

    public void stop() { setPower(0); }

    public void setPower(double power) {
        if (motor != null) motor.setPower(power);
    }
}
