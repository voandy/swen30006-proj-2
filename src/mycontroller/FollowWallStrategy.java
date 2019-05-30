package mycontroller;

import java.util.HashMap;

import mycontroller.MyAutoController.State;
import tiles.MapTile;
import utilities.Coordinate;

public class FollowWallStrategy implements IDriveStrategy {

	@Override
	public boolean drive(MyAutoController autoctrl) {
		HashMap<Coordinate, MapTile> currentView = autoctrl.getView();
		Coordinate currentPosition = new Coordinate(autoctrl.getPosition());
		
		/* Experimental healing strategy */
//		MapTile tile = currentView.get(new Coordinate(autoctrl.getPosition()));
//		if (autoctrl.strategyMode == Simulation.StrategyMode.FUEL && autoctrl.getHealth() < 300) {
//			if(tile.isType(MapTile.Type.TRAP)){
//				if (((TrapTile)tile).getTrap() == "health") {
//					autoctrl.applyBrake();
//					return false;
//				}
//			}
//		} else {
//			autoctrl.applyForwardAcceleration();
//		}
		
		if (currentPosition.equals(autoctrl.foundWallCoord)) {
			// we've done a complete circle, turn right to find a new wall
			autoctrl.turnRight();
			autoctrl.currState = State.FIND_WALL;
			return true;
		}

		if(!autoctrl.checkObstacleLeft(autoctrl.getOrientation(), currentView)) {
			// If wall no longer on left, turn left
			autoctrl.turnLeft();
		} else {
			// If wall on left and wall straight ahead, turn right
			if(autoctrl.checkObstacleAhead(autoctrl.getOrientation(), currentView)) {
				autoctrl.turnRight();
			}
		}

		if (autoctrl.numParcelsFound() == autoctrl.numParcels()) {
			autoctrl.currState = State.FIND_FINISH;
			return true;
		}
		
		return false;
	}

}
