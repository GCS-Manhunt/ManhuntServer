# player Package

## class player

#### player(UUID uuid, String username)

#### player(UUID uuid, String username, String type)

### public final UUID uuid

### public final String username

### double getLocation()

#### returns the gps location in degrees as an double array

### void setLocation(double[2] location)

#### set the location by providing a double array of length 2 with {latitude, longitude}

### double distance(player p)

#### returns distance between current player and player p

### double heading(player p)

#### returns heading from current player to player p

## class PlayerSet

#### PlayerSet(int capacity)

### void addPlayer(player p)

#### add player to Playerset

### player removePlayer(UUID uuid)

#### returns player with UUID uuid

###  player getPlayer(UUID uuid)

#### returns player with UUID uuid

###  player[] getNClosest(UUID origin, int n)

#### returns an array with the top n closest Players