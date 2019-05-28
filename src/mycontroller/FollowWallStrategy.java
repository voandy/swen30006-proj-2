package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class FollowWallStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
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
