package com.example.candyblast;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.*;
import android.view.*;
import android.media.AudioManager;
import android.media.ToneGenerator;
import java.util.Random;

public class MainActivity extends Activity {
    GameView game;
    @Override public void onCreate(Bundle b){super.onCreate(b);game=new GameView();setContentView(game);}
    @Override protected void onPause(){if(game!=null)game.stopMusic();super.onPause();}
    @Override protected void onResume(){super.onResume();if(game!=null)game.startMusic();}

    class GameView extends View {
        final int N=8,TYPES=6;
        int[][] board=new int[N][N];
        int[] colors={Color.rgb(255,65,93),Color.rgb(50,165,255),Color.rgb(255,177,32),Color.rgb(45,205,120),Color.rgb(160,80,240),Color.rgb(255,90,185)};
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); Random random=new Random();
        int score=0,moves=30,level=1,selR=-1,selC=-1; float cell,left,top,downX,downY;
        String banner="Match 3 or more candies!"; boolean finished=false,won=false,soundOn=true,musicOn=true;
        ToneGenerator audio=new ToneGenerator(AudioManager.STREAM_MUSIC,65); Handler musicHandler=new Handler(); int musicStep=0; Runnable musicRunnable;
        Canvas cv;
        GameView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);fillBoard();musicRunnable=new Runnable(){public void run(){if(musicOn&&!finished){int[] notes={ToneGenerator.TONE_PROP_BEEP,ToneGenerator.TONE_PROP_ACK,ToneGenerator.TONE_PROP_BEEP2,ToneGenerator.TONE_PROP_NACK};audio.startTone(notes[musicStep++%notes.length],85);}musicHandler.postDelayed(this,950);}};}
        int dp(float x){return (int)(x*getResources().getDisplayMetrics().density);} void startMusic(){if(musicOn)musicHandler.removeCallbacks(musicRunnable);if(musicOn)musicHandler.postDelayed(musicRunnable,600);}void stopMusic(){musicHandler.removeCallbacks(musicRunnable);}void tone(int t,int ms){if(soundOn)audio.startTone(t,ms);}
        void fillBoard(){for(int r=0;r<N;r++)for(int c=0;c<N;c++){int v;do{v=random.nextInt(TYPES);}while((c>1&&board[r][c-1]==v&&board[r][c-2]==v)||(r>1&&board[r-1][c]==v&&board[r-2][c]==v));board[r][c]=v;}invalidate();}
        void text(String s,float x,float y,float size,int color,boolean bold){p.setShader(null);p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create(bold?"sans-serif-black":"sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));p.setTextAlign(Paint.Align.CENTER);p.setStyle(Paint.Style.FILL);cv.drawText(s,x,y,p);}
        @Override protected void onDraw(Canvas c){cv=c;float w=getWidth(),h=getHeight();p.setShader(new LinearGradient(0,0,w,h,Color.rgb(255,220,239),Color.rgb(207,233,255),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);
            p.setShader(new LinearGradient(0,0,w,dp(115),Color.rgb(255,82,160),Color.rgb(155,65,220),Shader.TileMode.CLAMP));c.drawRoundRect(dp(12),dp(12),w-dp(12),dp(115),dp(25),dp(25),p);p.setShader(null);
            text("CANDY BLAST",w/2,dp(52),dp(29),Color.WHITE,true);text("SWEET MATCH ADVENTURE",w/2,dp(82),dp(12),Color.rgb(255,238,255),true);
            card(w*.25f,dp(148),dp(120),dp(66));card(w*.75f,dp(148),dp(120),dp(66));text("SCORE",w*.25f,dp(139),dp(12),Color.rgb(145,75,160),true);text(""+score,w*.25f,dp(174),dp(25),Color.rgb(245,70,145),true);text("MOVES",w*.75f,dp(139),dp(12),Color.rgb(145,75,160),true);text(""+moves,w*.75f,dp(174),dp(25),Color.rgb(245,70,145),true);
            float margin=dp(12);cell=Math.min((w-margin*2)/N,(h-dp(300))/N);cell=Math.max(dp(27),cell);left=(w-cell*N)/2;top=dp(225);float boardSize=cell*N;p.setColor(Color.argb(55,80,40,100));c.drawRoundRect(left-dp(6),top-dp(6),left+boardSize+dp(6),top+boardSize+dp(6),dp(20),dp(20),p);p.setColor(Color.WHITE);c.drawRoundRect(left,top,left+boardSize,top+boardSize,dp(15),dp(15),p);
            for(int r=0;r<N;r++)for(int col=0;col<N;col++){float x=left+col*cell,y=top+r*cell;p.setColor(Color.rgb(249,231,246));c.drawRoundRect(x+dp(2),y+dp(2),x+cell-dp(2),y+cell-dp(2),dp(8),dp(8),p);candy(r,col);}
            float progY=top+boardSize+dp(15);text("LEVEL "+level,w/2,progY,dp(15),Color.rgb(130,55,145),true);float bw=w*.7f,bx=w/2-bw/2,by=progY+dp(10);p.setColor(Color.WHITE);c.drawRoundRect(bx,by,bx+bw,by+dp(12),dp(6),dp(6),p);p.setColor(Color.rgb(255,120,185));c.drawRoundRect(bx,by,bx+bw*Math.min(1,score/(float)(level*700)),by+dp(12),dp(6),dp(6),p);
            float foot=h-dp(45);text(banner,w/2,foot-dp(31),dp(14),Color.rgb(125,45,140),true);button(w*.27f,foot,dp(105),"NEW",Color.rgb(255,100,160));button(w*.62f,foot,dp(105),musicOn?"MUSIC ON":"MUSIC OFF",musicOn?Color.rgb(125,80,220):Color.GRAY);button(w*.88f,foot,dp(78),soundOn?"SOUND":"MUTE",soundOn?Color.rgb(65,170,210):Color.GRAY);
            if(finished){p.setColor(Color.argb(215,255,255,255));c.drawRoundRect(dp(20),h*.35f,w-dp(20),h*.65f,dp(24),dp(24),p);text(won?"LEVEL COMPLETE!":"GAME OVER",w/2,h*.44f,dp(25),Color.rgb(175,50,130),true);text(won?"Score: "+score:"Try again!",w/2,h*.5f,dp(17),Color.rgb(100,70,120),false);button(w/2,h*.57f,dp(190),won?"NEXT LEVEL":"RETRY",Color.rgb(255,100,160));}
        }
        void card(float x,float y,float bw,float bh){p.setColor(Color.argb(35,80,30,100));cv.drawRoundRect(x-bw/2+dp(2),y-bh/2+dp(3),x+bw/2+dp(2),y+bh/2+dp(3),dp(16),dp(16),p);p.setColor(Color.WHITE);cv.drawRoundRect(x-bw/2,y-bh/2,x+bw/2,y+bh/2,dp(16),dp(16),p);}
        void button(float x,float y,float width,String label,int color){p.setColor(color);cv.drawRoundRect(x-width/2,y-dp(19),x+width/2,y+dp(19),dp(20),dp(20),p);text(label,x,y+dp(5),dp(12),Color.WHITE,true);}
        int lighter(int x){return Color.rgb(Math.min(255,Color.red(x)+55),Math.min(255,Color.green(x)+55),Math.min(255,Color.blue(x)+55));}int darker(int x){return Color.rgb((int)(Color.red(x)*.68),(int)(Color.green(x)*.68),(int)(Color.blue(x)*.68));}
        void candy(int r,int col){int type=board[r][col];if(type<0)return;float x=left+col*cell,y=top+r*cell,cx=x+cell/2,cy=y+cell/2,rad=cell*.36f;int base=colors[type];p.setColor(Color.argb(35,40,20,70));cv.drawOval(cx-rad*.85f,cy-rad*.65f+dp(2),cx+rad*.85f,cy+rad*.9f+dp(3),p);p.setShader(new RadialGradient(cx-rad*.35f,cy-rad*.42f,rad*1.45f,new int[]{lighter(base),base,darker(base)},new float[]{0,.52f,1},Shader.TileMode.CLAMP));cv.drawCircle(cx,cy,rad,p);p.setShader(null);p.setColor(Color.argb(160,255,255,255));cv.drawOval(cx-rad*.55f,cy-rad*.62f,cx-rad*.12f,cy-rad*.32f,p);p.setColor(Color.argb(165,255,255,255));p.setStrokeWidth(dp(2));
            Path shape=new Path();switch(type){case 0:shape.moveTo(cx,cy+rad*.35f);shape.cubicTo(cx-rad*.8f,cy-rad*.1f,cx-rad*.5f,cy-rad*.8f,cx,cy-rad*.35f);shape.cubicTo(cx+rad*.5f,cy-rad*.8f,cx+rad*.8f,cy-rad*.1f,cx,cy+rad*.35f);cv.drawPath(shape,p);break;case 1:shape.moveTo(cx,cy-rad*.5f);shape.cubicTo(cx+rad*.65f,cy+rad*.15f,cx+rad*.45f,cy+rad*.6f,cx,cy+rad*.55f);shape.cubicTo(cx-rad*.45f,cy+rad*.6f,cx-rad*.65f,cy+rad*.15f,cx,cy-rad*.5f);cv.drawPath(shape,p);break;case 2:for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float rr=i%2==0?rad*.55f:rad*.25f;float px=cx+(float)Math.cos(a)*rr,py=cy+(float)Math.sin(a)*rr;if(i==0)shape.moveTo(px,py);else shape.lineTo(px,py);}shape.close();cv.drawPath(shape,p);break;case 3:shape.moveTo(cx,cy-rad*.55f);shape.quadTo(cx+rad*.7f,cy,cx,cy+rad*.5f);shape.quadTo(cx-rad*.7f,cy,cx,cy-rad*.55f);cv.drawPath(shape,p);cv.drawLine(cx-rad*.3f,cy+rad*.25f,cx+rad*.25f,cy-rad*.25f,p);break;case 4:shape.moveTo(cx,cy-rad*.5f);shape.lineTo(cx+rad*.45f,cy);shape.lineTo(cx,cy+rad*.5f);shape.lineTo(cx-rad*.45f,cy);shape.close();cv.drawPath(shape,p);break;default:cv.drawCircle(cx,cy,rad*.4f,p);cv.drawLine(cx-rad*.45f,cy-rad*.55f,cx+rad*.45f,cy+rad*.55f,p);cv.drawLine(cx+rad*.45f,cy-rad*.55f,cx-rad*.45f,cy+rad*.55f,p);}
            if(r==selR&&col==selC){p.setStyle(Paint.Style.STROKE);p.setColor(Color.WHITE);p.setStrokeWidth(dp(3));cv.drawRoundRect(x+dp(2),y+dp(2),x+cell-dp(2),y+cell-dp(2),dp(10),dp(10),p);p.setStyle(Paint.Style.FILL);}}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){float x=e.getX(),y=e.getY();if(e.getAction()==MotionEvent.ACTION_DOWN){downX=x;downY=y;return true;}if(e.getAction()==MotionEvent.ACTION_UP){if(finished){if(y>getHeight()*.35f&&y<getHeight()*.65f){if(won)level++;reset();}return true;}float fy=getHeight()-dp(45);if(y>fy-dp(28)){if(x<getWidth()*.45f)reset();else if(x<getWidth()*.76f){musicOn=!musicOn;if(musicOn)startMusic();else stopMusic();}else soundOn=!soundOn;invalidate();return true;}if(y<top||y>top+cell*N)return true;int col=(int)((x-left)/cell),r=(int)((y-top)/cell);if(r<0||r>=N||col<0||col>=N)return true;if(Math.abs(x-downX)>dp(12)||Math.abs(y-downY)>dp(12)){int dc=0,dr=0;if(Math.abs(x-downX)>Math.abs(y-downY))dc=x>downX?1:-1;else dr=y>downY?1:-1;swap(r,col,r+dr,col+dc);}else select(r,col);return true;}return true;}
        void select(int r,int c){if(selR<0){selR=r;selC=c;}else{swap(selR,selC,r,c);selR=selC=-1;}invalidate();}
        void swap(int r1,int c1,int r2,int c2){if(r2<0||r2>=N||c2<0||c2>=N||Math.abs(r1-r2)+Math.abs(c1-c2)!=1)return;int t=board[r1][c1];board[r1][c1]=board[r2][c2];board[r2][c2]=t;if(!hasMatch()){t=board[r1][c1];board[r1][c1]=board[r2][c2];board[r2][c2]=t;banner="No match — try again!";tone(ToneGenerator.TONE_PROP_NACK,110);}else{moves--;tone(ToneGenerator.TONE_PROP_BEEP2,110);cascade();banner="Sweet match! Keep going!";if(score>=level*700){finished=true;won=true;banner="Level completed!";tone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,350);}else if(moves<=0){finished=true;won=false;banner="Out of moves!";}}selR=selC=-1;invalidate();}
        boolean hasMatch(){for(int r=0;r<N;r++)for(int c=0;c<N;c++)if(matchAt(r,c))return true;return false;}
        boolean matchAt(int r,int c){int v=board[r][c],n=1;for(int k=c-1;k>=0&&board[r][k]==v;k--)n++;for(int k=c+1;k<N&&board[r][k]==v;k++)n++;if(n>=3)return true;n=1;for(int k=r-1;k>=0&&board[k][c]==v;k--)n++;for(int k=r+1;k<N&&board[k][c]==v;k++)n++;return n>=3;}
        void cascade(){boolean again=true;while(again){boolean[][] mark=new boolean[N][N];again=false;for(int r=0;r<N;r++)for(int c=0;c<N;c++){int v=board[r][c],len=1;while(c+len<N&&board[r][c+len]==v)len++;if(len>=3){for(int k=0;k<len;k++)mark[r][c+k]=true;again=true;}c+=len-1;}for(int c=0;c<N;c++)for(int r=0;r<N;r++){int v=board[r][c],len=1;while(r+len<N&&board[r+len][c]==v)len++;if(len>=3){for(int k=0;k<len;k++)mark[r+k][c]=true;again=true;}r+=len-1;}int cleared=0;for(int r=0;r<N;r++)for(int c=0;c<N;c++)if(mark[r][c]){board[r][c]=-1;cleared++;}if(cleared>0)score+=cleared*60;for(int c=0;c<N;c++){int write=N-1;for(int r=N-1;r>=0;r--)if(board[r][c]>=0)board[write--][c]=board[r][c];while(write>=0)board[write--][c]=random.nextInt(TYPES);}}}
        void reset(){score=0;moves=30;finished=false;won=false;selR=selC=-1;banner="Match 3 or more candies!";fillBoard();startMusic();}
    }
}
