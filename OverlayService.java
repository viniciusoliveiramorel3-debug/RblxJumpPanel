package com.example.rblxjumppanel;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {
    WindowManager wm;
    WindowManager.LayoutParams panelLp, handleLp, jumpLp, targetLp;
    LinearLayout panel;
    TextView handle, jump, target;
    boolean locked=true, panelOpen=true, jumpCreated=false, targetCreated=false;
    final int blue=0xff2399ff;
    int type;
    @Override public IBinder onBind(Intent i){return null;}
    @Override public void onCreate(){
        super.onCreate(); wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        type=android.os.Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
        panelLp=new WindowManager.LayoutParams(dp(218),-2,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        panelLp.gravity=Gravity.TOP|Gravity.LEFT; panelLp.x=12; panelLp.y=100;
        panel=new LinearLayout(this); panel.setOrientation(LinearLayout.VERTICAL); panel.setPadding(dp(8),dp(8),dp(8),dp(8)); panel.setBackgroundColor(0xee0d141e);
        TextView title=new TextView(this); title.setText("  RBLX • ADM"); title.setTextColor(blue); title.setTextSize(18); panel.addView(title);
        Button close=button("X • RECOLHER"); close.setOnClickListener(v->setPanel(false)); panel.addView(close);
        Button create=button("CRIAR BOTÃO PULAR"); create.setOnClickListener(v->createJump()); panel.addView(create);
        Button targetBtn=button("MOSTRAR / OCULTAR ALVO"); targetBtn.setOnClickListener(v->toggleTarget()); panel.addView(targetBtn);
        Button lockBtn=button("LOCK / UNLOCK"); lockBtn.setOnClickListener(v->{locked=!locked; lockBtn.setText(locked?"LOCK • TRAVADO":"UNLOCK • MOVER"); Toast.makeText(this,locked?"Botão travado":"Arraste botão e alvo para ajustar",Toast.LENGTH_SHORT).show();}); panel.addView(lockBtn);
        Button remove=button("REMOVER BOTÃO"); remove.setOnClickListener(v->removeJump()); panel.addView(remove);
        wm.addView(panel,panelLp);
        handle=new TextView(this); handle.setText("≡"); handle.setTextColor(Color.WHITE); handle.setTextSize(24); handle.setGravity(Gravity.CENTER); handle.setBackgroundColor(0xee17456b);
        handleLp=new WindowManager.LayoutParams(dp(48),dp(48),type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        handleLp.gravity=Gravity.TOP|Gravity.LEFT; handleLp.x=8; handleLp.y=150;
        handle.setOnClickListener(v->setPanel(!panelOpen)); wm.addView(handle,handleLp); handle.setVisibility(View.GONE);
    }
    Button button(String s){Button b=new Button(this); b.setText(s); b.setTextSize(10); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff17456b)); return b;}
    void setPanel(boolean open){panelOpen=open; panel.setVisibility(open?View.VISIBLE:View.GONE); handle.setVisibility(open?View.GONE:View.VISIBLE);}
    void createJump(){
        if(jumpCreated){Toast.makeText(this,"Botão já criado",Toast.LENGTH_SHORT).show();return;}
        jump=new TextView(this); jump.setText("PULAR"); jump.setTextColor(Color.WHITE); jump.setTextSize(13); jump.setGravity(Gravity.CENTER); jump.setBackgroundColor(0xee0878c9);
        jumpLp=new WindowManager.LayoutParams(dp(72),dp(54),type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        jumpLp.gravity=Gravity.TOP|Gravity.LEFT; jumpLp.x=dp(240); jumpLp.y=dp(360);
        jump.setOnTouchListener(new View.OnTouchListener(){
            float sx,sy; int ox,oy; boolean moved;
            public boolean onTouch(View v,MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN: sx=e.getRawX();sy=e.getRawY();ox=jumpLp.x;oy=jumpLp.y;moved=false;return true;
                    case MotionEvent.ACTION_MOVE: if(!locked){int dx=(int)(e.getRawX()-sx),dy=(int)(e.getRawY()-sy);if(Math.abs(dx)+Math.abs(dy)>6)moved=true;jumpLp.x=ox+dx;jumpLp.y=oy+dy;wm.updateViewLayout(jump,jumpLp);}return true;
                    case MotionEvent.ACTION_UP:
                        if(!moved) sendJumpTap();
                        return true;
                } return true;
            }
        });
        wm.addView(jump,jumpLp); jumpCreated=true; createTarget();
    }
    void createTarget(){
        if(targetCreated)return;
        target=new TextView(this); target.setText("ALVO"); target.setTextColor(Color.WHITE); target.setTextSize(10); target.setGravity(Gravity.CENTER); target.setBackgroundColor(0xddff7a00);
        targetLp=new WindowManager.LayoutParams(dp(54),dp(40),type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        targetLp.gravity=Gravity.TOP|Gravity.LEFT;
        targetLp.x=dp(300); targetLp.y=dp(400);
        target.setOnTouchListener(new View.OnTouchListener(){
            float sx,sy;int ox,oy;boolean moved;
            public boolean onTouch(View v,MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:sx=e.getRawX();sy=e.getRawY();ox=targetLp.x;oy=targetLp.y;moved=false;return true;
                    case MotionEvent.ACTION_MOVE:if(!locked){int dx=(int)(e.getRawX()-sx),dy=(int)(e.getRawY()-sy);if(Math.abs(dx)+Math.abs(dy)>6)moved=true;targetLp.x=ox+dx;targetLp.y=oy+dy;wm.updateViewLayout(target,targetLp);}return true;
                    case MotionEvent.ACTION_UP: if(!moved) Toast.makeText(OverlayService.this,"Destrave com UNLOCK para mover o ALVO",Toast.LENGTH_SHORT).show(); return true;
                }return true;
            }
        });
        wm.addView(target,targetLp); targetCreated=true;
    }
    void toggleTarget(){if(!targetCreated){createTarget();}else target.setVisibility(target.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);}
    void sendJumpTap(){
        TapAccessibilityService a=TapAccessibilityService.get();
        if(a==null){Toast.makeText(this,"Ative Acessibilidade nas configurações primeiro",Toast.LENGTH_SHORT).show();return;}
        if(!targetCreated || target.getVisibility()!=View.VISIBLE){Toast.makeText(this,"Mostre o ALVO e posicione sobre o pulo original",Toast.LENGTH_SHORT).show();return;}
        float x=targetLp.x+targetLp.width/2f, y=targetLp.y+targetLp.height/2f;
        a.tap(x,y);
    }
    void removeJump(){try{if(jumpCreated&&jump!=null){wm.removeView(jump);jump=null;jumpCreated=false;}if(targetCreated&&target!=null){wm.removeView(target);target=null;targetCreated=false;}}catch(Exception ignored){}}
    int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    @Override public void onDestroy(){try{removeJump();if(panel!=null)wm.removeView(panel);if(handle!=null)wm.removeView(handle);}catch(Exception ignored){}super.onDestroy();}
}
