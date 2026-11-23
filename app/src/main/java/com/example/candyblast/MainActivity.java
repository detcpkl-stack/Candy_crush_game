package com.example.candyblast;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    GameView game;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        game = new GameView();
        setContentView(game);
    }

    class GameView extends View {
        final int N=8, TYPES=6;
        int[][] board=new int[N][N];
        int[] candyColors={Color.rgb(255,75,95),Color.rgb(45,190,255),Color.rgb(255,190,45),
            Color.rgb(90,220,115),Color.rgb(180,95,245),Color.rgb(255,120,205)};
        Paint p=new Paint(3);
        Random random=new Random();
        int score=0, moves=30, level=1, selectedR=-1, selectedC=-1;
        float cell, boardLeft, boardTop, downX, downY;
        String banner="Match 3 or more candies!";
        boolean finished=false, won=false;
        GameView(){super(MainActivity.this); setLayerType(View.LAYER_TYPE_SOFTWARE,null); newBoard();}
        void newBoard(){
            for(int r=0;r<N;r++) for(int c=0;c<N;c++){
                int v; do {v=random.nextInt(TYPES);} while((c>=2&&board[r][c-1]==v&&board[r][c-2]==v)||(r>=2&&board[r-1][c]==v&&board[r-2][c]==v));
                board[r][c]=v;
            }
            invalidate();
        }
        int dp(float x,float y){return (int)(x*getResources().getDisplayMetrics().density);}
        void text(String s,float x,float y,float size,int color,boolean bold){
            p.setColor(color);p.setTextSize(size);p.setTypeface(bold?Typeface.create("sans-serif-black",0):Typeface.create("sans-serif",0));p.setTextAlign(Paint.Align.CENTER);p.setStyle(Paint.Style.FILL);canvas.drawText(s,x,y,p);
        }
        Canvas canvas;
        @Override protected void onDraw(Canvas cv){
            canvas=cv; float w=getWidth(),h=getHeight();
            canvas.drawColor(Color.rgb(255,244,251));
            p.setShader(new LinearGradient(0,0,w,h,Color.rgb(255,205,230),Color.rgb(210,235,255),Shader.TileMode.CLAMP));canvas.drawRect(0,0,w,h,p);p.setShader(null);
            text("🍬 CANDY BLAST",w/2,dp(52),dp(29),Color.rgb(150,45,130),true);
            text("LEVEL "+level,w/2,dp(91),dp(18),Color.rgb(90,55,130),true);
            text("SCORE",w*.25f,dp(140),dp(15),Color.rgb(90,55,130),true);
            text(""+score,w*.25f,dp(170),dp(25),Color.rgb(255,90,130),true);
            text("MOVES",w*.75f,dp(140),dp(15),Color.rgb(90,55,130),true);
            text(""+moves,w*.75f,dp(170),dp(25),Color.rgb(255,90,130),true);
            float margin=dp(14); cell=Math.min((w-margin*2)/N,(h-dp(265))/N);
            boardLeft=(w-cell*N)/2; boardTop=dp(205);
            p.setColor(Color.argb(120,255,255,255));canvas.drawRoundRect(boardLeft-dp(7),boardTop-dp(7),boardLeft+cell*N+dp(7),boardTop+cell*N+dp(7),dp(20),dp(20),p);
            for(int r=0;r<N;r++)for(int c=0;c<N;c++) drawCandy(r,c);
            text(banner,w/2,boardTop+cell*N+dp(42),dp(17),Color.rgb(120,50,120),true);
            text("Swipe candies to match 3!",w/2,boardTop+cell*N+dp(72),dp(15),Color.rgb(90,75,130),false);
            drawButton(w/2, h-dp(55), dp(160), dp(44), "NEW GAME");
            if(finished){
                p.setColor(Color.argb(205,255,255,255));canvas.drawRoundRect(dp(22),h*.35f,w-dp(22),h*.65f,dp(25),dp(25),p);
                text(won?"LEVEL COMPLETE!":"GAME OVER",w/2,h*.44f,dp(26),Color.rgb(175,50,130),true);
                text(won?"You earned "+(score/100+1)+" stars!":"Try again to beat this level.",w/2,h*.49f,dp(17),Color.rgb(100,70,120),false);
                drawButton(w/2,h*.56f,dp(160),dp(46),won?"NEXT LEVEL":"RETRY");
            }
        }
        void drawCandy(int r,int c){
            float x=boardLeft+c*cell,y=boardTop+r*cell, pad=cell*.09f;
            p.setColor(Color.argb(35,80,50,100));canvas.drawRoundRect(x+pad,y+pad,x+cell-pad,y+cell-pad,cell*.24f,cell*.24f,p);
            float cx=x+cell/2,cy=y+cell/2,rad=cell*.37f;
            p.setShader(new RadialGradient(cx-rad*.25f,cy-rad*.35f,rad,
                new int[]{Color.WHITE,candyColors[board[r][c]],dark(candyColors[board[r][c]])},
                new float[]{0,.35f,1},Shader.TileMode.CLAMP));
            canvas.drawCircle(cx,cy,rad,p);p.setShader(null);
            p.setColor(Color.argb(130,255,255,255));canvas.drawOval(cx-rad*.55f,cy-rad*.65f,cx-rad*.1f,cy-rad*.35f,p);
            if(r==selectedR&&c==selectedC){p.setColor(Color.WHITE);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(3));canvas.drawCircle(cx,cy,rad+dp(2),p);p.setStyle(Paint.Style.FILL);}
        }
        int dark(int color){return Color.rgb((int)(Color.red(color)*.75),(int)(Color.green(color)*.75),(int)(Color.blue(color)*.75));}
        void drawButton(float cx,float cy,float bw,float bh,String label){
            p.setColor(Color.rgb(255,100,160));canvas.drawRoundRect(cx-bw/2,cy-bh/2,cx+bw/2,cy+bh/2,bh/2,bh/2,p);
            text(label,cx,cy+dp(6),dp(18),Color.WHITE,true);
        }
        int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            float x=e.getX(),y=e.getY();
            if(e.getAction()==MotionEvent.ACTION_DOWN){downX=x;downY=y;return true;}
            if(e.getAction()==MotionEvent.ACTION_UP){
                if(finished){if(y>getHeight()*.34f&&y<getHeight()*.68f){if(won)level++;resetGame();}return true;}
                if(y>getHeight()-dp(95)){resetGame();return true;}
                if(y<boardTop||y>boardTop+cell*N)return true;
                int c=(int)((x-boardLeft)/cell),r=(int)((y-boardTop)/cell);
                if(Math.abs(x-downX)>dp(12)||Math.abs(y-downY)>dp(12)){
                    int dc=0,dr=0;
                    if(Math.abs(x-downX)>Math.abs(y-downY))dc=x>downX?1:-1;else dr=y>downY?1:-1;
                    swapAndResolve(r,c,r+dr,c+dc);
                }else select(r,c);
                return true;
            }return true;
        }
        void select(int r,int c){
            if(selectedR<0){selectedR=r;selectedC=c;}
            else {swapAndResolve(selectedR,selectedC,r,c);selectedR=selectedC=-1;}
            invalidate();
        }
        void swapAndResolve(int r1,int c1,int r2,int c2){
            if(r2<0||r2>=N||c2<0||c2>=N||Math.abs(r1-r2)+Math.abs(c1-c2)!=1)return;
            int t=board[r1][c1];board[r1][c1]=board[r2][c2];board[r2][c2]=t;
            if(!hasMatch()){t=board[r1][c1];board[r1][c1]=board[r2][c2];board[r2][c2]=t;banner="No match—try again!";}
            else {moves--;cascade();banner="Sweet combo!";if(score>=level*700){finished=true;won=true;banner="Level completed!";}else if(moves<=0){finished=true;won=false;banner="Out of moves!";}}
            selectedR=selectedC=-1;invalidate();
        }
        boolean hasMatch(){for(int r=0;r<N;r++)for(int c=0;c<N;c++)if(matchAt(r,c))return true;return false;}
        boolean matchAt(int r,int c){
            int v=board[r][c], count=1;
            for(int k=c-1;k>=0&&board[r][k]==v;k--)count++;
            for(int k=c+1;k<N&&board[r][k]==v;k++)count++;
            if(count>=3)return true;count=1;
            for(int k=r-1;k>=0&&board[k][c]==v;k--)count++;
            for(int k=r+1;k<N&&board[k][c]==v;k++)count++;
            return count>=3;
        }
        void cascade(){
            boolean again=true;
            while(again){
                boolean[][] mark=new boolean[N][N];again=false;
                for(int r=0;r<N;r++)for(int c=0;c<N;c++){
                    int v=board[r][c],len=1;
                    while(c+len<N&&board[r][c+len]==v)len++;
                    if(len>=3){for(int k=0;k<len;k++)mark[r][c+k]=true;again=true;}c+=len-1;
                }
                for(int c=0;c<N;c++)for(int r=0;r<N;r++){
                    int v=board[r][c],len=1;
                    while(r+len<N&&board[r+len][c]==v)len++;
                    if(len>=3){for(int k=0;k<len;k++)mark[r+k][c]=true;again=true;}r+=len-1;
                }
                int cleared=0;
                for(int r=0;r<N;r++)for(int c=0;c<N;c++)if(mark[r][c]){board[r][c]=-1;cleared++;}
                if(cleared>0)score+=cleared*60;
                for(int c=0;c<N;c++){int write=N-1;for(int r=N-1;r>=0;r--)if(board[r][c]>=0)board[write--][c]=board[r][c];while(write>=0)board[write--][c]=random.nextInt(TYPES);}
            }
        }
        void resetGame(){score=0;moves=30;finished=false;won=false;selectedR=selectedC=-1;banner="Match 3 or more candies!";newBoard();}
    }
}
