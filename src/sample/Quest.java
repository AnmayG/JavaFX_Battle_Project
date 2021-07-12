package sample;

public class Quest {
    //This is basically an overcomplicated storage class.
    //It just stores the reward, its name, and when it gets chosen it sends its stats to the battle screen.
    private final int[] reward;
    private final int difficulty;
    private final String name;
    public Quest(int[] r, int d, String n){
        this.reward = r;
        this.difficulty = d;
        this.name = n;
    }

    public String getName() {
        return name;
    }
    public int getDifficulty() {
        return difficulty;
    }
    public int getGoldReward() {
        return reward[0];
    }
    public int getExpReward(){
        return reward[1];
    }

    public void chooseQuest(int[] stats, Weapon w, Special s, String n){
        Controller.receiveHero(stats, this.difficulty, w, s, n, this.reward);
    }
}
