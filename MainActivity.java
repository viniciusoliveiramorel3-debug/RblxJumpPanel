package com.example.rblxjumppanel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {
    int blue = Color.rgb(35,153,255);
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,24,24,24);
        root.setBackgroundColor(Color.rgb(13,20,30));
        TextView title = new TextView(this); title.setText("RBLX • JUMP PANEL"); title.setTextColor(blue); title.setTextSize(24);
        root.addView(title);
        TextView info = new TextView(this); info.setTextColor(Color.WHITE); info.setPadding(0,18,0,18);
        info.setText("PAINEL EXTERNO • PRETO/AZUL MILITAR\\n\\n1. Autorize a sobreposição.\\n2. Ative a acessibilidade para permitir toques.\\n3. Inicie o painel.\\n4. No jogo, use ALVO para posicionar o ponto sobre o botão de pulo original; depois trave-o.\\n\\nO botão externo envia um toque curto por vez. Não cria pulo infinito nem altera as regras do jogo.");
        root.addView(info);
        Button overlay = btn("1 • PERMITIR SOBREPOSIÇÃO");
        overlay.setOnClickListener(v -> { if (!Settings.canDrawOverlays(this)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); });
        root.addView(overlay);
        Button access = btn("2 • ATIVAR ACESSIBILIDADE");
        access.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(access);
        Button start = btn("3 • INICIAR PAINEL");
        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) { info.setText("Primeiro ative a permissão de sobreposição."); return; }
            startService(new Intent(this, OverlayService.class));
            info.setText("Painel iniciado. Volte ao jogo e ajuste o ALVO azul sobre o botão de pulo original.");
        });
        root.addView(start);
        Button stop = btn("PARAR PAINEL");
        stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class))); root.addView(stop);
        setContentView(root);
    }
    Button btn(String s) {
        Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xff17456b));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.topMargin=10; b.setLayoutParams(lp); return b;
    }
}
