package mycontroller;

import controller.CarController;
import world.Car;
import java.util.HashMap;

import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		
		public enum State {
			FIND_WALL, // going straight until a wall is found
			FIND_PARCEL, // sticking to a wall while looking for parcels
			GET_PARCEL, // go straight until a parcel is pick-up then find wall
			FIND_FINISH, // sticking to a wall while looking for finish
			GO_STRAIGHT
		}
		
		public enum Goal {
			PARCEL,
			FINISH,
			NONE
		}
		
		private boolean avoidLava = false;
		private boolean avoidHealth = false;
				
		public State currState;
		
		DriveStrategyFactory strategyFactory = new DriveStrategyFactory();
		
		public Coordinate foundWallCoord = new Coordinate(getPosition());
		
		// Car Speed to move at
		public final int CAR_MAX_SPEED = 1;
		
		public MyAutoController(Car car) {
			super(car);
			currState = State.FIND_WALL;
//			
//			if (Simulation.toConserve() == Simulation.StrategyMode.FUEL) {
//				throw new UnsupportedModeException();
//			}
			
			
		}
		
		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			System.out.println(currState.toString());
			
			switch (currState) {
			case FIND_WALL:
				IDriveStrategy findWallStrategy = strategyFactory.getDriveStrategy("find-wall");
				findWallStrategy.drive(this);
				break;
			case FIND_PARCEL:
				IDriveStrategy findParcelStrategy = strategyFactory.getDriveStrategy("find-parcel");
				findParcelStrategy.drive(this);
				break;
			case GET_PARCEL:
				IDriveStrategy getParcelStrategy = strategyFactory.getDriveStrategy("get-parcel");
				getParcelStrategy.drive(this);
				break;
			case FIND_FINISH:
				IDriveStrategy findExitStrategy = strategyFactory.getDriveStrategy("find-finish");
				findExitStrategy.drive(this);
				break;
			case GO_STRAIGHT:
				// ignore everything and just go straight
				break;
			}
		}
		
		// returns the new orientation after turning because this doesn't update during the current cycle apparently
		public WorldSpatial.Direction newOrientation(WorldSpatial.Direction initialOrientation, WorldSpatial.RelativeDirection turn){
			switch(turn) {
			case LEFT:
				switch(initialOrientation){
				case EAST:
					return WorldSpatial.Direction.NORTH;
				case NORTH:
					return WorldSpatial.Direction.WEST;
				case SOUTH:
					return WorldSpatial.Direction.EAST;
				case WEST:
					return WorldSpatial.Direction.SOUTH;
				default:
					return WorldSpatial.Direction.EAST;
				}
			case RIGHT:
				switch(initialOrientation){
				case EAST:
					return WorldSpatial.Direction.SOUTH;
				case NORTH:
					return WorldSpatial.Direction.EAST;
				case SOUTH:
					return WorldSpatial.Direction.WEST;
				case WEST:
					return WorldSpatial.Direction.NORTH;
				default:
					return WorldSpatial.Direction.EAST;
				}
			}
			return initialOrientation;
		}
		
		public boolean checkParcelAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			Coordinate currentPosition = new Coordinate(getPosition());
			MapTile tile;
			switch(orientation){
			case EAST:
				tile = currentView.get(new Coordinate(currentPosition.x+1, currentPosition.y));
				return checkParcel(tile);
			case NORTH:
				tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+1));
				return checkParcel(tile);
			case SOUTH:
				tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-1));
				return checkParcel(tile);
			case WEST:
				tile = currentView.get(new Coordinate(currentPosition.x-1, currentPosition.y));
				return checkParcel(tile);
			default:
				return false;
			}
		}
		
		// returns true of the given tile has a pacel on it
		private boolean checkParcel(MapTile tile) {
			if(tile.isType(MapTile.Type.TRAP)){
				if (((TrapTile)tile).getTrap() == "parcel") {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Check if you have a wall in front of you!
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		public boolean checkObstacleAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
			switch(orientation){
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
			}
		}
		
		/**
		 * Check if the wall or other obstacle is on your left hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		public boolean checkObstacleLeft(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			switch(orientation){
			case EAST:
				return checkNorth(currentView);
			case NORTH:
				return checkWest(currentView);
			case SOUTH:
				return checkEast(currentView);
			case WEST:
				return checkSouth(currentView);
			default:
				return false;
			}
		}
		
		/**
		 * Check if the wall or other obstacle is on your right hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		public boolean checkObstacleRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			switch(orientation){
			case EAST:
				return checkSouth(currentView);
			case NORTH:
				return checkEast(currentView);
			case SOUTH:
				return checkWest(currentView);
			case WEST:
				return checkNorth(currentView);
			default:
				return false;
			}
		}
		
		/**
		 * Check if there is a parcel or finish tile to the left of the car within view
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		public Goal checkGoalLeft(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			Coordinate currentPosition = new Coordinate(getPosition());
			MapTile tile;
			
			switch(orientation){
			case EAST:
				// car facing East, check North for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case NORTH:
				// car facing North, check West for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case SOUTH:
				// car facing South, check East for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case WEST:
				// car facing West, check South for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			default:
				return Goal.NONE;
			}	
		}
		
		/**
		 * Check if there is a parcel or finish tile to the left of the car within view
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		public Goal checkGoalRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			Coordinate currentPosition = new Coordinate(getPosition());
			MapTile tile;
			
			switch(orientation){
			case EAST:
				// car facing East, check South for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case NORTH:
				// car facing North, check East for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case SOUTH:
				// car facing South, check West for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			case WEST:
				// car facing West, check North for goal
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
					// there is an obstacle in the way
					if(checkTile(tile)){
						return Goal.NONE;
					} else if (tile.isType(MapTile.Type.FINISH)){
						return Goal.FINISH;
					} else if (tile.isType(Type.TRAP)){
						if (((TrapTile) tile).getTrap() == "parcel") {
							return Goal.PARCEL;
						}
					}
				}
				return Goal.NONE;
			default:
				return Goal.NONE;
			}	
		}
		
		// returns true if the given tile is a wall or obstacle.
		private boolean checkTile(MapTile tile) {
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
			
			if (avoidLava) {
				if(tile.isType(MapTile.Type.TRAP)){
					if (((TrapTile)tile).getTrap() == "lava") {
						return true;
					}
				}
			}
			
			if (avoidHealth) {
				if(tile.isType(MapTile.Type.TRAP)){
					if (((TrapTile)tile).getTrap() == "health") {
						return true;
					}
					if (((TrapTile)tile).getTrap() == "water") {
						return true;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Method below just iterates through the list and check in the correct coordinates.
		 * i.e. Given your current position is 10,10
		 * checkEast will check up to wallSensitivity amount of tiles to the right.
		 * checkWest will check up to wallSensitivity amount of tiles to the left.
		 * checkNorth will check up to wallSensitivity amount of tiles to the top.
		 * checkSouth will check up to wallSensitivity amount of tiles below.
		 */
		public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
			// Check tiles to my right
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(checkTile(tile)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to my left
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				if(checkTile(tile)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to towards the top
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				if(checkTile(tile)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles towards the bottom
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				System.out.println(tile.getType());
				if(checkTile(tile)){
					return true;
				}
			}
			return false;
		}
		
	}
