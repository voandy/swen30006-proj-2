package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindParcelStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		if (autoctrl.checkLeft(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the left turn to get it
			autoctrl.turnLeft();
			autoctrl.currState = State.FIND_WALL;
		} else if (autoctrl.checkRight(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
			// If there is a parcel to the right turn to get it
			autoctrl.turnRight();
			autoctrl.currState = State.FIND_WALL;
		} else {
			// If wall no longer on left, turn left
			if(!autoctrl.checkFollowingWall(autoctrl.getOrientation(), currentView)) {
				autoctrl.turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(autoctrl.checkWallAhead(autoctrl.getOrientation(), currentView)) {
					autoctrl.turnRight();
				}
			}
		}
	}
}
