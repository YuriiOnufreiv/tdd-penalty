package ua.onufreiv.penalties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Created by Yurii_Onufreiv on 20-Mar-17.
 */
public class PenaltyGameTest {
    private PenaltyGame game;
    private String firstTeam;
    private String secondTeam;
    private String player;

    @Before
    public void setUp() {
        firstTeam = "AC Milan";
        secondTeam = "FC Dynamo Kyiv";
        player = "Sheva";
        game = new PenaltyGame(firstTeam, secondTeam);
    }

    @Test
    public void initialScoreIsZeros() {
        assertEquals("0-0", game.score());
    }

    @Test
    public void scoreNotChangedAfterMissedKick() {
        game.kick(false);
        assertEquals("0-0", game.score());
    }

    @Test
    public void scoreIsOneOneAfterTwoSuccessfulKicks() {
        game.kick(true);
        game.kick(true);
        assertEquals("1-1", game.score());
    }

    @Test
    public void gameNotFinishedAfterTenSuccessfulKicks() {
        int kicksAmount = 10;
        performKicks(kicksAmount);
        assertEquals(false, game.finished());
    }

    @Test
    public void gameFinishedAfterNineSuccessfulOneMissedKicks() {
        performKicks(9);
        game.kick(false);
        assertEquals(true, game.finished());
    }

    @Test
    public void gameNotFinishedAfterFiveSuccessfulKicks() {
        performKicks(5);
        assertEquals(false, game.finished());
    }

    @Test
    public void gameNotFinishedForTwoZeroScore() {
        game.kick(true);
        game.kick(false);
        game.kick(true);
        game.kick(false);
        assertEquals(false, game.finished());
    }

    @Test
    public void gameFinishedDueToSpecialCase() {
        game.kick(true);
        game.kick(false);
        game.kick(true);
        game.kick(false);
        game.kick(true);
        game.kick(false);
        assertEquals(true, game.finished());
    }

    @Test
    public void playerKicksHistoryReturned() {
        boolean kickSuccess = true;
        boolean[] expected = {true, false, true, false, true, true, true, true, true, true};

        PenaltyGame gameSpy = Mockito.spy(game);
        when(gameSpy.kicksHistoryForPlayer(player)).thenReturn(expected);

        assertEquals(expected, gameSpy.kick(player, firstTeam, kickSuccess));

        InOrder inOrder = inOrder(gameSpy);
        inOrder.verify(gameSpy).kick(kickSuccess);
        inOrder.verify(gameSpy).kicksHistoryForPlayer(player);
    }

    @Test(expected = KickOnWrongTurnException.class)
    public void exceptionOnKickOnWrongTurn() {
        game.kick(player, secondTeam, true);
    }

    @Test
    public void playersPriceReturnedAfterSevenSeries() {
        performKicks(14, false);
        performKicks(2, true);

        PenaltyGame gameSpy = Mockito.spy(game);
        when(gameSpy.missedPlayersTotalPrice(firstTeam)).thenReturn(500);
        when(gameSpy.missedPlayersTotalPrice(secondTeam)).thenReturn(700);

        String actualScore = gameSpy.score();
        String expectedScore = firstTeam + " [500] (1)-(1) [700] " + secondTeam;

        assertEquals(expectedScore, actualScore);
    }

    @Test(expected = KickAfterFinishedException.class)
    public void exceptionOnKickAfterFinished() {
        performKicks(9, true);
        game.kick(false);
        game.kick(false);
    }


    private void performKicks(int kicksAmount, boolean success) {
        for (int i = 0; i < kicksAmount; i++) {
            game.kick(success);
        }
    }

    private void performKicks(int kicksAmount) {
        performKicks(kicksAmount, true);
    }
}