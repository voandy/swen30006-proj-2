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
			FIND_FINISH, // sticking to a wall while looking for finish
			GO_STRAIGHT
		}
		
		public enum Goal {
			PARCEL,
			FINISH,
			NONE
		}
		
		public State currState;
		public Goal goal;
		
		DriveStrategyFactory strategyFactory = new DriveStrategyFactory();
		
		
		// Car Speed to move at
		public final int CAR_MAX_SPEED = 1;
		
		public MyAutoController(Car car) {
			super(car);
			currState = State.FIND_WALL;
		}
		
		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			switch (currState) {
			case FIND_WALL:
				IDriveStrategy findWallStrategy = strategyFactory.getDriveStrategy("find-wall");
				findWallStrategy.drive(this);
				break;
			case FIND_PARCEL:
				IDriveStrategy findParcelStrategy = strategyFactory.getDriveStrategy("find-parcel");
				findParcelStrategy.drive(this);
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
		
		/**
		 * Check if you have a wall in front of you!
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
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
		 * Check if the wall is on your left hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
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
		 * Check if there is a parcel or finish tile to the left of the car within view
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		public Goal checkLeft(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			Coordinate currentPosition = new Coordinate(getPosition());
			
			switch(orientation){
			case EAST:
				// car facing East, check North for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing North, check West for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing South, check East for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing West, check South for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
		public Goal checkRight(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			Coordinate currentPosition = new Coordinate(getPosition());
			
			switch(orientation){
			case EAST:
				// car facing East, check South for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing North, check East for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing South, check West for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				// car facing West, check North for parcel
				for(int i = 0; i <= Car.VIEW_SQUARE; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
					// there is a wall in the way
					if(tile.isType(MapTile.Type.WALL)){
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
				if(tile.isType(MapTile.Type.WALL)){
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
				if(tile.isType(MapTile.Type.WALL)){
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
				if(tile.isType(MapTile.Type.WALL)){
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
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
	}
