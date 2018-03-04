package com.example.lengzhang.mychess;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GamePlay extends AppCompatActivity implements View.OnClickListener {

    WindowManager wm;
    int wW, wH;
    private Chronometer chronometer;
    private TextView Msg_Box;

    Button[] chessboard;
    int[][] chessboard_state;       // [0 - idle; 1 - player; 2 - AI; 3 - Moveable Empty; 4 - Movealbe AI Space]
                                    // [0 - idle; 1 - King; 2 - Queen; 3 - Bishop; 4 - Rook; 5 - N; 6 - P]
    int moveFrom;

    List<String> MoveableList;         // MoveableList contains movealbe id of buttions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        wm = this.getWindowManager();
        wW = wm.getDefaultDisplay().getWidth();
        wH = wm.getDefaultDisplay().getHeight();
        initView();
    }

    private void initView() {
        // Initial Chronometer
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.start();

        // Initial Message Box
        Msg_Box = findViewById(R.id.game_play_msg_box);
        Msg_Box.setText(R.string.game_start);

        // Initial MoveableList
        MoveableList = new ArrayList<>();

        chessboard = new Button[64];
        for (int i = 0; i < 8; ++i) {
            chessboard[i*8] = findViewById(R.id.A1+i);
            chessboard[i*8].setOnClickListener(this);
            chessboard[i*8+1] = findViewById(R.id.B1+i);
            chessboard[i*8+1].setOnClickListener(this);
            chessboard[i*8+2] = findViewById(R.id.C1+i);
            chessboard[i*8+2].setOnClickListener(this);
            chessboard[i*8+3] = findViewById(R.id.D1+i);
            chessboard[i*8+3].setOnClickListener(this);
            chessboard[i*8+4] = findViewById(R.id.E1+i);
            chessboard[i*8+4].setOnClickListener(this);
            chessboard[i*8+5] = findViewById(R.id.F1+i);
            chessboard[i*8+5].setOnClickListener(this);
            chessboard[i*8+6] = findViewById(R.id.G1+i);
            chessboard[i*8+6].setOnClickListener(this);
            chessboard[i*8+7] = findViewById(R.id.H1+i);
            chessboard[i*8+7].setOnClickListener(this);
        }

        chessboard_state = new int[2][64];
        for (int i = 0; i < 64; ++i) {
            chessboard_state[0][i] = 0;
            chessboard_state[1][i] = 0;
        }

        // Initial Player Pieces
        ChangePieceState(0,0,1,4);
        ChangePieceState(0,1,1,5);
        ChangePieceState(0,2,1,3);
        ChangePieceState(0,3,1,2);
        ChangePieceState(0,4,1,1);
        ChangePieceState(0,5,1,3);
        ChangePieceState(0,6,1,4);
        ChangePieceState(0,7,1,5);

        // Initial AI Pieces
        ChangePieceState(7,0,2,4);
        ChangePieceState(7,1,2,5);
        ChangePieceState(7,2,2,3);
        ChangePieceState(7,3,2,2);
        ChangePieceState(7,4,2,1);
        ChangePieceState(7,5,2,3);
        ChangePieceState(7,6,2,4);
        ChangePieceState(7,7,2,5);

        for (int i = 0; i < 8; ++i) {
            ChangePieceState(1,i,1,6);
            ChangePieceState(6,i,2,6);
        }

        moveFrom = -1;
    }

    void midToast(String str, int showTime)
    {
        Toast toast = Toast.makeText(GamePlay.this, str, showTime);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.YELLOW);     //设置字体颜色
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if (moveFrom == -1) {
            moveFrom = v.getId();
            Msg_Box.setText("Clicked " + getId(moveFrom));
            MakeMoveableList();
        }
        else {
            if (v.getId() != moveFrom) {
                MovePiece(moveFrom, v.getId());
            }
            moveFrom = -1;
            CleanMoveableList();
        }
    }

    public String getId(int id) {
        return getResources().getResourceEntryName(id);
    }

    public int getId(String name) {
        return getResources().getIdentifier(name, "id", getPackageName());
    }
    // Letter:  0 - A; 1 - B; 2 - C; 3 - D; 4 - E; 5 - F; 6 - G; 7 - H
    // Number:  0 - 1; 1 - 2; 2 - 3; 3 - 4; 4 - 5; 5 - 6; 6 - 7; 7 - 8
    // State:   0 - idle; 1 - player; 2 - AI
    // Type:    0 - idle; 1 - King; 2 - Queen; 3 - Bishop; 4 - Rook; 5 - Knight(N); 6 - Pawn
    public void ChangePieceState(int Number, int Letter, int State, int Type) {
        Log.d("-I", "Number " + Number + " Letter " + Letter + " State " + State + " Type " + Type);
        if (State == 0) {
            if (((Number + Letter) % 2) == 0) {
                chessboard[Number * 8 + Letter].setBackgroundResource(R.color.SaddleBrown);
            }
            else {
                chessboard[Number * 8 + Letter].setBackgroundResource(R.color.SandyBrown);
            }
            chessboard[Number * 8 + Letter].setText("");
            chessboard_state[0][Number * 8 + Letter] = 0;
            chessboard_state[1][Number * 8 + Letter] = 0;
        }
        else if (State == 1 || State == 2 || State == 4) {

            if (State == 1) {
                chessboard[Number * 8 + Letter].setBackgroundResource(R.drawable.whiteshape);
                chessboard[Number * 8 + Letter].setTextColor(getColor(R.color.Black));
            }
            else if (State == 2) {
                chessboard[Number * 8 + Letter].setBackgroundResource(R.drawable.blackshape);
                chessboard[Number * 8 + Letter].setTextColor(getColor(R.color.White));
            }
            else if (State == 4) {
                chessboard[Number * 8 + Letter].setBackgroundResource(R.drawable.greenshape);
                chessboard[Number * 8 + Letter].setTextColor(getColor(R.color.White));
            }
            if (Type == 1) {
                chessboard[Number * 8 + Letter].setText("K");
            }
            else if (Type == 2) {
                chessboard[Number * 8 + Letter].setText("Q");
            }
            else if (Type == 3) {
                chessboard[Number * 8 + Letter].setText("B");
            }
            else if (Type == 4) {
                chessboard[Number * 8 + Letter].setText("R");
            }
            else if (Type == 5) {
                chessboard[Number * 8 + Letter].setText("N");
            }
            else if (Type == 6) {
                chessboard[Number * 8 + Letter].setText("P");
            }
            chessboard_state[0][Number * 8 + Letter] = State;
            chessboard_state[1][Number * 8 + Letter] = Type;
        }
        else if (State == 3) {
            Log.d("-I", "Painting");
            chessboard[Number * 8 + Letter].setBackgroundResource(R.drawable.greenrectangle);
            chessboard[Number * 8 + Letter].setText("");
            chessboard_state[0][Number * 8 + Letter] = State;
            chessboard_state[1][Number * 8 + Letter] = Type;
        }
    }

    public void GetPieceState(int Number, int Letter, int[] State, int[] Type) {
        State[0] = chessboard_state[0][Number * 8 + Letter];
        Type[0] = chessboard_state[1][Number * 8 + Letter];
        Log.d("-I", "GetPieceState Number " + Number + " Letter " + Letter + " State " + State[0] + " Type " + Type[0]);
    }

    public void GetPieceState(int id, int[] Number, int[] Letter, int[] State, int[] Type) {
        String str = getId(id);
        Number[0] = (str.charAt(1) - '1');
        Letter[0] = (str.charAt(0) - 'A');
        State[0] = chessboard_state[0][Number[0] * 8 + Letter[0]];
        Type[0] = chessboard_state[1][Number[0] * 8 + Letter[0]];
    }

    public void MovePiece(int from_id, int to_id) {
        int[] from_n = new int[]{-1};
        int[] from_l = new int[]{-1};
        int[] from_s = new int[]{-1};
        int[] from_t = new int[]{-1};
        GetPieceState(from_id, from_n, from_l, from_s, from_t);
        int[] to_n = new int[]{-1};
        int[] to_l = new int[]{-1};
        int[] to_s = new int[]{-1};
        int[] to_t = new int[]{-1};
        GetPieceState(to_id, to_n, to_l, to_s, to_t);
        ChangePieceState(to_n[0], to_l[0], from_s[0], from_t[0]);
        ChangePieceState(from_n[0], from_l[0], to_s[0], to_t[0]);

        Msg_Box.setText("Moved " + getId(from_id) + " To " + getId(to_id));
    }

    // Letter:  0 - A; 1 - B; 2 - C; 3 - D; 4 - E; 5 - F; 6 - G; 7 - H
    // Number:  0 - 1; 1 - 2; 2 - 3; 3 - 4; 4 - 5; 5 - 6; 6 - 7; 7 - 8
    // State:   0 - idle; 1 - player; 2 - AI
    // Type:    0 - idle; 1 - King; 2 - Queen; 3 - Bishop; 4 - Rook; 5 - Knight(N); 6 - FirstTimePawn; 7 - Pawn
    public void MakeMoveableList() {
        int[] from_n = new int[]{-1};
        int[] from_l = new int[]{-1};
        int[] from_s = new int[]{-1};
        int[] from_t = new int[]{-1};
        GetPieceState(moveFrom, from_n, from_l, from_s, from_t);
        // For King
        if (from_t[0] == 1) {
            // For Player
            if (from_s[0] == 1) {
                // D2 E2 F2
                // D1 E1 F1
                Log.d("-I", "Start from Number " + from_n[0] + " Letter " + from_l[0]);
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        int letter = from_l[0] + i;
                        int number = from_n[0] + j;
                        Log.d("-I", "Checked: Number " + number + ", Letter " + letter);
                        if (letter >= 0 && letter <= 7 && number >= 0 && number <= 7) {
                            Log.d("-I", "In");
                            if (chessboard_state[0][number * 8 + letter] == 0) {
                                //char cl = (char)('A' + letter);
                                //char cn = (char)('0' + number);
                                //String name = "" + cl + cn;
                                //int id = getId(name);
                                //Log.d("-I", ""+id);
                                MoveableList.add("" + (char)('A' + letter) + (char)('0' + number));
                                Log.d("-I", "Added"+(char)('A' + letter) + (char)('0' + number));
                            }
                        }
                    }
                }
            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For Queen
        else if (from_t[0] == 2) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For Bishop
        else if (from_t[0] == 3) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For Rook
        else if (from_t[0] == 4) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For Knight(N)
        else if (from_t[0] == 5) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For FirstTimePawn
        else if (from_t[0] == 6) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }
        // For Pawn
        else if (from_t[0] == 7) {
            // For Player
            if (from_s[0] == 1) {

            }
            // For AI
            else if (from_s[0] == 2) {

            }
        }

        // Mark Moveable Space
        for (int i = 0; i < MoveableList.size(); ++i) {
            Log.d("-I", "Marking " + MoveableList.get(i));
            int move_n = MoveableList.get(0).charAt(1) - '0';
            int move_l = MoveableList.get(0).charAt(0) - 'A';
            int[] move_s = new int[]{-1};
            int[] move_t = new int[]{-1};
            GetPieceState(move_n, move_l, move_s, move_t);
            Log.d("-I", "Number " + move_n + " Letter " + move_l + " State " + move_s[0] + " Type " + move_t[0]);
            if (move_s[0] == 0) {
                ChangePieceState(move_n, move_l, 3, move_t[0]);
            }
            else if (move_s[0] == 2) {
                ChangePieceState(move_n, move_l, 4, move_t[0]);
            }
        }
    }

    public  void CleanMoveableList() {
        for (int i = 0; i < MoveableList.size(); ++i) {
            int move_n = MoveableList.get(0).charAt(1) - '1';
            int move_l = MoveableList.get(0).charAt(0) - 'A';
            int[] move_s = new int[]{-1};
            int[] move_t = new int[]{-1};
            GetPieceState(move_n, move_l, move_s, move_t);
            if (move_s[0] == 3) {
                ChangePieceState(move_n, move_l, 0, move_t[0]);
            }
            else if (move_s[0] == 4) {
                ChangePieceState(move_n, move_l, 2, move_t[0]);
            }
        }
        while (!MoveableList.isEmpty()) {
            MoveableList.remove(0);
        }
    }
}
