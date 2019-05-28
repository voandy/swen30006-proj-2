package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindWallStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
					
		// checkStateChange();
		if(autoctrl.getSpeed() < autoctrl.CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			autoctrl.applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		
		// Start wall-following (with wall on left) as soon as we see a wall straight ahead
		if(autoctrl.checkWallAhead(autoctrl.getOrientation(),currentView)) {
			autoctrl.turnRight();
			autoctrl.currState = State.FOLLOW_WALL;
		}
	}

}
