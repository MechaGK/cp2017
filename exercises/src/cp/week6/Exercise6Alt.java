package cp.week6;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise6Alt
{
	/*
	- Implement tic-tac-toe between two threads.
	- The board is shared between the two threads.
	- Each thread takes a turn at making a move.
	- Stop the whole computation when a thread wins.
	- If no thread wins, stop and declare a draw.
	*/
    public static class Player extends Thread {
	    private boolean playing = true;
	    private final boolean isFirstPlayer;
	    private final int myNumber;

	    private static int[] fields = new int[9];

	    public enum GameState {
	        STARTING, PLAYER_ONE, PLAYER_TWO, DRAW, VICTORY_PLAYER_ONE, VICTORY_PLAYER_TWO
        }

        private static GameState gameState = GameState.PLAYER_ONE;

	    public Player(boolean isFirstPlayer) {
	        this.isFirstPlayer = isFirstPlayer;

	        myNumber = isFirstPlayer ? 1 : 2;
        }

	    public void doTurn(int[] fields) {
	        System.out.println(myNumber + "'s turn");
            ArrayList<Integer> emptyFields = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                if (fields[i] == 0) {
                    emptyFields.add(i);
                }
            }

            Collections.shuffle(emptyFields);
            int index = emptyFields.get(0);

            fields[index] = isFirstPlayer ? 1 : 2;

            int column = index % 3;
            int row = index / 3;

            if (    (fields[row * 3] == fields[row * 3 + 1]) && (fields[row * 3] == fields[row * 3 + 2]) ||
                    (fields[column] == fields[column + 3]) && (fields[column] == fields[column + 6]) ||
                    (fields[0] > 0) && (fields[0] == fields[4]) && (fields[0] == fields[8]) ||
                    (fields[2] > 0) && (fields[2] == fields[4]) && (fields[2] == fields[6])) {
                gameState = isFirstPlayer ? GameState.VICTORY_PLAYER_ONE : GameState.VICTORY_PLAYER_TWO;
            }
            else {
                gameState = isFirstPlayer ? GameState.PLAYER_TWO : GameState.PLAYER_ONE;
            }
        }

        @Override
        public void run() {
	        while(true) {
	            GameState state = gameState;

	            if (state == GameState.PLAYER_ONE && isFirstPlayer) {
	                doTurn(fields);
                }
                else if (state == GameState.PLAYER_TWO && !isFirstPlayer) {
	                doTurn(fields);
                }
                else if (state == GameState.DRAW) {
	                System.out.println("It's a draw");
                }
                else if (state == GameState.VICTORY_PLAYER_ONE) {
	                if (isFirstPlayer) {
	                    System.out.println("Player one: I won!");
                    }
                    else {
	                    System.out.println("Player two: I lost :(");
                    }

                    break;
                }
                else if (state == GameState.VICTORY_PLAYER_TWO) {
                    if (!isFirstPlayer) {
                        System.out.println("Player one: I won!");
                    }
                    else {
                        System.out.println("Player two: I lost :(");
                    }

                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
	    Player player1 = new Player(true);
	    Player player2 = new Player(false);

	    player1.start();
	    player2.start();
    }
}
