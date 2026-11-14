package org.firstinspires.ftc.teamcode.grobo;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

/*
 * ΡΥΘΜΙΣΕΙΣ ΡΟΜΠΟΤ
 * ================
 * ΟΛΕΣ οι παράμετροι του ρομπότ σε ΕΝΑ σημείο.
 * Αν αλλάξει κάτι στο ρομπότ (όνομα μοτέρ, ρόδες, θέση Hub, ρυθμίσεις),
 * το αλλάζετε ΕΔΩ και ισχύει σε όλα τα προγράμματα.
 */
public final class RobotConfig {

    private RobotConfig() {}   // δεν φτιάχνουμε αντικείμενα, μόνο σταθερές

    // ===================== ΟΝΟΜΑΤΑ ΣΤΟ CONFIGURATION (Driver Station) =====================
    // Σασί
    public static final String M_FRONT_LEFT  = "leftFMotor";
    public static final String M_FRONT_RIGHT = "rightFMotor";
    public static final String M_BACK_LEFT   = "leftBMotor";
    public static final String M_BACK_RIGHT  = "rightBMotor";

    // Μηχανισμοί (DC μοτέρ)
    public static final String M_INTAKE          = "intakeMotor";         // Intake
    public static final String M_BOX             = "boxMotor";         // Box
    public static final String M_FLYWHEEL_NECTAR = "FWNectar";  // FlyWheel Nectar
    public static final String M_FLYWHEEL_POLLEN = "FWPollen";  // FlyWheel Pollen

    // Servo (θέσης)
    public static final String S_FLOWER_NECTAR   = "mSNectar";  // Flower Nectar
    public static final String S_FLOWER_POLLEN   = "mSPollen";  // Flower Pollen
    public static final String S_NECTAR    = "mSSNectar";  // Servo Shooter Nectar
    public static final String S_POLLEN    = "mSSPollen";  // Servo Shooter Pollen

    // Αισθητήρες
    public static final String WEBCAM   = "Webcam 1";
    public static final String IMU_NAME = "imu";

    // ===================== ΘΕΣΗ CONTROL HUB (για το γυροσκόπιο) =====================
    // Προς τα πού κοιτάει το λογότυπο REV και προς τα πού οι θύρες USB.
    public static final RevHubOrientationOnRobot.LogoFacingDirection HUB_LOGO =
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
    public static final RevHubOrientationOnRobot.UsbFacingDirection HUB_USB =
            RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;

    // ===================== ΣΑΣΙ =====================
    public static final double TICKS_PER_REV     = 560;   // HD Hex + 20:1 = 560 | + 40:1 = 1120
    public static final double WHEEL_DIAMETER_CM = 7.5;   // REV mecanum 75 mm
    public static final double GEAR_RATIO        = 1.0;   // γρανάζια/αλυσίδα μετά το κιβώτιο, αλλιώς 1
    public static final double CALIBRATION       = 1.0;   // π.χ. 50.0 / 47.0 αν ζήτησες 50 και πήγε 47

    public static final double TICKS_PER_CM =
            TICKS_PER_REV * GEAR_RATIO / (Math.PI * WHEEL_DIAMETER_CM) * CALIBRATION;

    // Πρόσημο encoder: FL, FR, BL, BR. Σπρώχνοντας μπροστά πρέπει όλοι να ΑΥΞΑΝΟΝΤΑΙ.
    public static final int[] ENC_SIGN = {1, -1, 1, -1};

    // ===================== ΧΕΙΡΟΚΙΝΗΤΗ ΟΔΗΓΗΣΗ =====================
    public static final double STICK_DEADZONE = 0.15;
    // Αργή κίνηση ακρίβειας (κράτα LB στο gamepad 1): ποσοστό της κανονικής ισχύος
    public static final double SLOW_DRIVE = 0.35;   // κίνηση μπρος-πίσω / πλάγια
    public static final double SLOW_TURN  = 0.30;   // στροφή
    // true = ΕΝΑ χειριστήριο: όλα στο gamepad 1 (RT βολή, LT/A intake, Y/B flywheels, DPAD box)
    // false = δύο χειριστήρια: μηχανισμοί στο gamepad 2 (όπως πριν)
    public static final boolean SINGLE_GAMEPAD = false;

    // ===================== ΚΙΝΗΣΗ ΣΕ ΑΠΟΣΤΑΣΗ (driveCm) =====================
    public static final double DRIVE_MAX_POWER = 0.5;
    public static final double DRIVE_MIN_POWER = 0.12;
    public static final double SLOW_DOWN_CM    = 20;
    public static final double ACCEL_PER_S     = 1.5;
    public static final double DRIVE_TOL_CM    = 0.5;
    public static final double DRIVE_TIMEOUT_S = 5;

    // Ευθεία κίνηση με γυροσκόπιο
    public static final double HEADING_KP       = 0.02;
    public static final double MAX_HEADING_TURN = 0.25;
    public static final double HEADING_SIGN     = 1.0;   // αν στρίβει περισσότερο αντί να ισιώνει -> -1

    // ===================== ΣΤΡΟΦΗ ΣΕ ΓΩΝΙΑ (turnToHeading) =====================
    public static final double TURN_KP        = 0.01;
    public static final double TURN_MIN_POWER = 0.08;
    public static final double TURN_MAX_POWER = 0.45;
    public static final double TURN_TOL_DEG   = 1.0;
    public static final double TURN_TIMEOUT_S = 3;

    // ===================== ΚΑΜΕΡΑ =====================
    public static final boolean USE_MANUAL_EXPOSURE = true;
    public static final int     EXPOSURE_MS         = 6;
    public static final int     CAMERA_GAIN         = 250;
    public static final int     DECIMATION          = 2;     // αν δεν βλέπει από μακριά -> 1
    public static final double  VISION_FILTER_ALPHA = 0.5;

    // ===================== ΣΗΜΑΔΕΜΑ ΣΤΟ CELL =====================
    public static final double  AIM_BEARING_TOL_DEG = 1.5;
    public static final boolean AIM_USE_RANGE       = true;
    public static final double  AIM_TARGET_RANGE_CM = 120.0;  // απόσταση όπου ευστοχεί ο shooter
    public static final double  AIM_RANGE_TOL_CM    = 4.0;

    public static final double AIM_TURN_KP  = 0.012, AIM_TURN_KS  = 0.10, AIM_TURN_MAX  = 0.35;
    public static final double AIM_DRIVE_KP = 0.010, AIM_DRIVE_KS = 0.06, AIM_DRIVE_MAX = 0.45;
    public static final double AIM_TURN_SIGN  = 1.0;
    public static final double AIM_DRIVE_SIGN = 1.0;
    public static final double AIM_SLEW_PER_SEC = 2.0;

    // Κάθετα στο CELL: true = κινείται ΠΛΑΓΙΑ μέχρι το yaw του tag ≈ 0
    //                  false = όπως πριν (το πλάγιο το κάνει ο οδηγός)
    public static final boolean AIM_SQUARE_TO_TAG = true;
    public static final double  AIM_YAW_TOL_DEG   = 3.0;
    public static final double  AIM_STRAFE_KP = 0.015, AIM_STRAFE_KS = 0.10, AIM_STRAFE_MAX = 0.35;  // ανά μοίρα yaw
    public static final double  AIM_STRAFE_SIGN = -1.0;   // αν το yaw ΜΕΓΑΛΩΝΕΙ ενώ κινείται πλάγια -> -1

    // ===================== INTAKE =====================
    public static final double  INTAKE_POWER         = 0.8;   // μάζεμα
    public static final double  INTAKE_REVERSE_POWER = 0.4;   // ανάποδο
    public static final boolean INTAKE_REVERSED      = true;

    // ===================== BOX =====================
    public static final double  BOX_POWER    = 0.5;
    public static final boolean BOX_REVERSED = false;

    // ===================== SHOOTERS (κοινά) =====================
    public static final double  FLYWHEEL_TICKS_PER_REV = 28;   // HD Hex χωρίς κιβώτιο
    public static final double  FLYWHEEL_RPM_TOL       = 100;  // "έτοιμος" = ±100 RPM
    // Βάλτε τις τιμές που έβγαλε το "RPM - RPM Tester"
    public static final double  FLYWHEEL_F = 11.7;
    public static final double  FLYWHEEL_P = 1.2;
    // Η βολή γίνεται μόνο όταν το flywheel έχει φτάσει στις στροφές
    public static final boolean FIRE_ONLY_WHEN_READY = true;

    // ===================== SHOOTER NECTAR =====================
    public static final double  NECTAR_RPM               = 3500;
    public static final boolean NECTAR_FLYWHEEL_REVERSED = false;
    public static final double  NECTAR_FLOWER_POWER      = 0.8;
    public static final boolean NECTAR_FLOWER_REVERSED   = false;
    public static final double  NECTAR_SERVO_REST        = 0.0;   // θέση ηρεμίας (0..1)
    public static final double  NECTAR_SERVO_PUSH        = 0.5;   // θέση που σπρώχνει (0..1)

    // ===================== SHOOTER POLLEN =====================
    public static final double  POLLEN_RPM               = 3500;
    public static final boolean POLLEN_FLYWHEEL_REVERSED = false;
    public static final double  POLLEN_FLOWER_POWER      = 0.8;
    public static final boolean POLLEN_FLOWER_REVERSED   = false;
    public static final double  POLLEN_SERVO_REST        = 0.0;
    public static final double  POLLEN_SERVO_PUSH        = 0.5;
}
