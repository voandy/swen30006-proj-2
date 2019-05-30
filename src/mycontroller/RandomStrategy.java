package mycontroller;

import java.util.Random;

import mycontroller.MyAutoController.State;

public class RandomStrategy implements IDriveStrategy {
	private static final int jumpChance = 100;

	// When the car has taken too many steps without finishing the map we start
	// to turn off the wall at random intervals in order to find the remaining parcels/finish
	public boolean drive(MyAutoController autoctrl) {
		
		if (autoctrl.currFuel <= autoctrl.initialFuel - autoctrl.stepThreshold) {
			Random rand = new Random();
			int n = rand.nextInt(jumpChance);
			
			if (n == 0) {
				autoctrl.turnRight();
				autoctrl.currState = State.FIND_WALL;
				return true;
			}
		}
		
		return false;
	}

}
