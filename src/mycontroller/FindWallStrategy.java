package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FindWallStrategy implements IDriveStrategy {

	@Override
	public void drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		// See the finish and have enough parcels so just head there instead.
		if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
			if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
				// If there is a parcel to the left turn to get it
				autoctrl.turnLeft();
				autoctrl.currState = State.GO_STRAIGHT;
			} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
				// If there is a parcel to the right turn to get it
				autoctrl.turnRight();
				autoctrl.currState = State.GO_STRAIGHT;
			}
		}
		
		// Already following wall
		if(autoctrl.checkFollowingWall(autoctrl.getOrientation(), currentView)) {
			autoctrl.foundWallCoord = new Coordinate(autoctrl.getPosition());
			if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
				autoctrl.currState = State.FIND_FINISH;
			} else {
				autoctrl.currState = State.FIND_PARCEL;
			}
			return;
		}
					
		// checkStateChange();
		if(autoctrl.getSpeed() < autoctrl.CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			autoctrl.applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		
		// Start wall-following (with wall on left) as soon as we see a wall straight ahead
		if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(),currentView)) {
			// found wall ahead
			if (autoctrl.checkWallRight(autoctrl.getOrientation(),currentView)) {
				// can't turn right, turn left and try to find another wall
				autoctrl.turnLeft();
			} else {
				// store position where we found the wall.
				autoctrl.foundWallCoord = new Coordinate(autoctrl.getPosition());
				// turn right and follow wall
				autoctrl.turnRight();
				if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
					autoctrl.currState = State.FIND_FINISH;
				} else {
					autoctrl.currState = State.FIND_PARCEL;
				}
			}
		}
	}

}
