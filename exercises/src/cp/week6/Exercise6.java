package cp.week6;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise6
{
	/*
	- Implement tic-tac-toe between two threads.
	- The board is shared between the two threads.
	- Each thread takes a turn at making a move.
	- Stop the whole computation when a thread wins.
	- If no thread wins, stop and declare a draw.
	*/

	public static class Board extends Thread {
	    private boolean playing = true;
	    private static int[] fields = new int[9];

	    private final ArrayList<Integer> playerIds = new ArrayList<>();
	    private int gameState = 0;

        public synchronized void setField(int playerId, int fieldIndex) {
            int playerIndex = playerIds.indexOf(playerId);
            fields[fieldIndex] = playerIndex + 1;

            int column = fieldIndex % 3;
            int row = fieldIndex / 3;

            // Checking for victory (yikes)
            if (    (fields[row * 3] == fields[row * 3 + 1]) && (fields[row * 3] == fields[row * 3 + 2]) ||
                    (fields[column] == fields[column + 3]) && (fields[column] == fields[column + 6]) ||
                    (fields[0] > 0) && (fields[0] == fields[4]) && (fields[0] == fields[8]) ||
                    (fields[2] > 0) && (fields[2] == fields[4]) && (fields[2] == fields[6])) {
                gameState = playerIndex + 1;
            }

            if (gameState > 100) {
                boolean allSet = true;
                for (int i = 0; i < 9; i++) {
                    if (fields[i] == 0) {
                        allSet = false;
                        break;
                    }
                }
                if (allSet) {
                    gameState = 3;
                }
            }

            if (gameState > 100) {
                if (playerIndex == 0) {
                    gameState = playerIds.get(1);
                }
                else {
                    gameState = playerIds.get(0);
                }
            }
            else {
                playing = false;
                if (gameState < 3) {
                    System.out.println(String.format("Player %d won!", playerIds.get(gameState)));
                }
                else if (gameState == 3) {
                    System.out.println("It's a draw!");
                }
            }
        }

        @Override
        public void run() {
            while (playing) {
                Thread.yield();
            }
        }

        public int getGameState() {
	        return gameState;
        }

        public int[] getFields() {
            return fields.clone();
        }

        public synchronized int registerPlayer(int playerId) {
            if (playerIds.size() > 2 || gameState > 0) {
                return 1;
            }
            else {
                playerIds.add(playerId);

                if (playerIds.size() >= 2) {
                    gameState = playerIds.get(0);
                }

                return 0;
            }
        }
    }

    public static class Player extends Thread {
	    private boolean playing = true;
	    private Board board;
	    private final int id;

	    public Player(Board board, int id) {
	        this.board = board;
	        this.id = id + 100;
        }

	    public void doTurn(Board board, int[] fields) {
	        System.out.println(id + "'s turn");
            ArrayList<Integer> emptyFields = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                if (fields[i] == 0) {
                    emptyFields.add(i);
                }
            }

            Collections.shuffle(emptyFields);

            board.setField(id, emptyFields.get(0));
        }

        @Override
        public void run() {
	        board.registerPlayer(id);

	        while(playing) {
	            int state = board.getGameState();

                if (state == id) {
                    doTurn(board, board.getFields());
                }
                else if (state > 0 && state <= 3) {
                    playing = false;
                }
            }
        }
    }

    public static void main(String[] args) {
	    Board board = new Board();
	    Player player1 = new Player(board, 1);
	    Player player2 = new Player(board, 2);

        board.start();
	    player1.start();
	    player2.start();
    }
}
