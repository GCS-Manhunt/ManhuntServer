package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

import java.util.UUID;

public class EventSendScore extends PersonalEvent{
    int playerScore;
    int playerRanking;
    int scoreAhead;


    //if Ranking is 1, ScoreAhead should be 0;
    //if there is a tie?

    public EventSendScore() {

    }
    public EventSendScore(int playerScore , int playerRanking, int scoreAhead) {
        this.playerScore = playerScore;
        this.playerRanking = playerRanking;
        this.scoreAhead = scoreAhead;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeInt(this.playerScore);
        b.writeInt(this.playerRanking);
        b.writeInt(this.scoreAhead);
    }

    @Override
    public void read(ByteBuf b) {
        this.playerScore = b.readInt();
        this.playerRanking = b.readInt();
        this.scoreAhead = b.readInt();
    }

    @Override
    public void execute() {

    }

}
