package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import BasicLib4997.MasqLinearOpMode;
import BasicLib4997.MasqMotors.MasqRobot.Direction;

/**
 * This is a basic autonomous program to test the various autonomous functions.
 */

@Autonomous(name = "AutoTest", group = "Test")
public class AutoTest extends MasqLinearOpMode {

    public void runLinearOpMode() throws InterruptedException {
        while (!opModeIsActive()) {
            dash.create(robot);
            dash.update();
        }
        waitForStart();
        robot.drive(0.5, 10, Direction.FORWARD);
        robot.turn(45, Direction.RIGHT);
    }
}
