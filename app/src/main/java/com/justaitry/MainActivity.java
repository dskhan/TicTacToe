package com.justaitry;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;

    private boolean computerMode = false;
    private TextView modeNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeButtons();

        modeNameTextView = findViewById(R.id.mode_name_text_view);
        Button modeSwitchButton = findViewById(R.id.mode_switch_button);
        modeSwitchButton.setOnClickListener(v -> {
            computerMode = !computerMode;
            modeNameTextView.setText(computerMode ? "Current Mode: Player vs Computer" : "Current Mode: Player vs Player");
            resetBoard();
        });
    }

    private void initializeButtons() {
        buttons[0][0] = findViewById(R.id.button_00);
        buttons[0][1] = findViewById(R.id.button_01);
        buttons[0][2] = findViewById(R.id.button_02);
        buttons[1][0] = findViewById(R.id.button_10);
        buttons[1][1] = findViewById(R.id.button_11);
        buttons[1][2] = findViewById(R.id.button_12);
        buttons[2][0] = findViewById(R.id.button_20);
        buttons[2][1] = findViewById(R.id.button_21);
        buttons[2][2] = findViewById(R.id.button_22);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        if (!"".equals(clickedButton.getText().toString())) {
            return;
        }

        if (player1Turn) {
            clickedButton.setText("X");
        } else {
            clickedButton.setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
            if (computerMode && !player1Turn) {
                makeComputerMove();
            }
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][1].equals(field[i][2]) && !field[i][0].isEmpty()) {
                return true;
            }
            if (field[0][i].equals(field[1][i]) && field[1][i].equals(field[2][i]) && !field[0][i].isEmpty()) {
                return true;
            }
        }

        // Check diagonals
        if (field[0][0].equals(field[1][1]) && field[1][1].equals(field[2][2]) && !field[0][0].isEmpty()) {
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[1][1].equals(field[2][0]) && !field[0][2].isEmpty()) {
            return true;
        }

        return false;
    }



    private void player1Wins() {
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void player2Wins() {
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
    }
    private void makeComputerMove() {
        if (roundCount < 9) {
            int[] move = minimax(2, "O");
            buttons[move[0]][move[1]].setText("O");
            roundCount++;
            if (checkForWin()) player2Wins();
            else if (roundCount == 9) draw();
            else player1Turn = !player1Turn;
        }
    }

    private int[] minimax(int depth, String player) {
        List<int[]> emptyCells = getEmptyCells();
        int bestScore = (player.equals("O")) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int[] bestMove = {-1, -1};
        if (emptyCells.isEmpty() || depth == 0) bestScore = evaluate();
        else {
            for (int[] cell : emptyCells) {
                buttons[cell[0]][cell[1]].setText(player);
                currentScore = minimax(depth - 1, (player.equals("O")) ? "X" : "O")[0];
                if ((player.equals("O") && currentScore > bestScore) || (player.equals("X") && currentScore < bestScore)) {
                    bestScore = currentScore;
                    bestMove = cell;
                }
                buttons[cell[0]][cell[1]].setText("");
            }
        }
        return bestMove;
    }

    private List<int[]> getEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }

    private int evaluate() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return (field[i][0].equals("O")) ? 10 : -10;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return (field[0][i].equals("O")) ? 10 : -10;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return (field[0][0].equals("O")) ? 10 : -10;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return (field[0][2].equals("O")) ? 10 : -10;
        }

        return 0;
    }

}
