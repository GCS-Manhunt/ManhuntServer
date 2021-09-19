# Player Package

## class Player

#### Player(UUID uuid, String username)

#### Player(UUID uuid, String username, String type)

### public final UUID uuid

### public final String username

### double getLocation()

#### returns the gps location in degrees as an double array

### void setLocation(double[2] location)

#### set the location by providing a double array of length 2 with {latitude, longitude}

### double distance(Player p)

#### returns distance between current player and Player p

### double heading(Player p)

#### returns heading from current player to Player p

## class PlayerSet

#### PlayerSet(int capacity)

### void addPlayer(Player p)

#### add Player to Playerset

### Player removePlayer(UUID uuid)

#### returns Player with UUID uuid

###  Player getPlayer(UUID uuid)

#### returns Player with UUID uuid

###  Player[] getNClosest(UUID origin, int n)

#### returns an array with the top n closest Players