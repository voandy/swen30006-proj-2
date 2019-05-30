package mycontroller;

public class DriveStrategyFactory {
	
	public IDriveStrategy getDriveStrategy(String strategyName) {
		IDriveStrategy driveStrategy = null;
		CompositeDriveStrategy compositeStrategy = null;
		
		switch (strategyName) {
		case "find-wall":
			driveStrategy = new FindWallStrategy();
			break;
		case "find-parcel":
			compositeStrategy = new CompositeDriveStrategy();
			compositeStrategy.add(new FindParcelStrategy());
			compositeStrategy.add(new FollowWallStrategy());
			driveStrategy = compositeStrategy;
			break;
		case "get-parcel":
			driveStrategy = new GetParcelStrategy();
			break;
		case "find-finish":
			compositeStrategy = new CompositeDriveStrategy();
			compositeStrategy.add(new FindFinishStrategy());
			compositeStrategy.add(new FollowWallStrategy());
			driveStrategy = compositeStrategy;
			break;
		}
		return driveStrategy;
	}
}
