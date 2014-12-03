package recommand;


class Entity {
	int userID;
	int movieID;
	double score;

	public Entity(int userID, int movieID, double score) {
		this.userID = userID;
		this.movieID = movieID;
		this.score = score;
	}
}