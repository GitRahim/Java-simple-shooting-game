package nahayi;

public class Player {
	//place in table which is defined with x and y;
	int x,y;
	//shooting direction. can be from 0 to 4.  1 is up. 2 is right. 3 is down. 4 is left. 0 is for dead robot or player.
	int shoot;
	//number of lives which is maximumly 4, but by default is 3. if 0, player dies.
	int lives = 3;

}
