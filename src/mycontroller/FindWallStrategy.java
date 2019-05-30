package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.Goal;
import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class FindWallStrategy implements IDriveStrategy {

	@Override
	public boolean drive(MyAutoController autoctrl) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		
		// checkStateChange();
		if(autoctrl.getSpeed() < autoctrl.CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(), currentView)) {
				autoctrl.turnRight();
			}
			autoctrl.applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		
		if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
			// See the finish and have enough parcels so just head there instead.
			if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
				// If there is a parcel to the left turn to get it
				autoctrl.turnLeft();
				autoctrl.currState = State.GO_STRAIGHT;
				return true;
			} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.FINISH) {
				// If there is a parcel to the right turn to get it
				autoctrl.turnRight();
				autoctrl.currState = State.GO_STRAIGHT;
				return true;
			}
		} else {
			// don't have enough parcels and see one so go straight to parcel instead of wall
			if (autoctrl.checkGoalLeft(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
				// If there is a parcel to the left turn to get it and get new orientation after turning;
				WorldSpatial.Direction newOrientation = autoctrl.newOrientation(autoctrl.getOrientation(), WorldSpatial.RelativeDirection.LEFT);
				autoctrl.turnLeft();

				if (autoctrl.checkParcelAhead(newOrientation, currentView)) {
					// if there is a parcel directly ahead after turning go into find wall mode
					autoctrl.currState = State.FIND_WALL;
					return true;
				} else {
					// otherwise go into get parcel mode
					autoctrl.currState = State.GET_PARCEL;
					return true;
				}
			} else if (autoctrl.checkGoalRight(autoctrl.getOrientation(), currentView) == Goal.PARCEL) {
				// If there is a parcel to the right turn to get it and get new orientation after turning;
				WorldSpatial.Direction newOrientation = autoctrl.newOrientation(autoctrl.getOrientation(), WorldSpatial.RelativeDirection.RIGHT);
				autoctrl.turnRight();

				if (autoctrl.checkParcelAhead(newOrientation, currentView)) {
					// if there is a parcel directly ahead after turning go into find wall mode
					autoctrl.currState = State.FIND_WALL;
					return true;
				} else {
					// otherwise go into get parcel mode
					autoctrl.currState = State.GET_PARCEL;
					return true;
				}
			}
		}

		
		// Already following wall
		if(autoctrl.checkObstacleLeft(autoctrl.getOrientation(), currentView)) {
			autoctrl.foundWallCoord = new Coordinate(autoctrl.getPosition());
			
			// If wall on left and wall straight ahead, turn right
			if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(), currentView)) {
				autoctrl.turnRight();
			}
			
			if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
				autoctrl.currState = State.FIND_FINISH;
				return true;
			} else {
				autoctrl.currState = State.FIND_PARCEL;
				return true;
			}
		}
		
		// Start wall-following (with wall on left) as soon as we see a wall straight ahead
		if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(),currentView)) {
			// found wall ahead
			if (autoctrl.checkObstacleRight(autoctrl.getOrientation(),currentView)) {
				// can't turn right, turn left and try to find another wall
				autoctrl.turnLeft();
			} else {
				// store position where we found the wall.
				autoctrl.foundWallCoord = new Coordinate(autoctrl.getPosition());
				// turn right and follow wall
				autoctrl.turnRight();
				if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
					autoctrl.currState = State.FIND_FINISH;
					return true;
				} else {
					autoctrl.currState = State.FIND_PARCEL;
					return true;
				}
			}
		}
		return false;
	}

}
