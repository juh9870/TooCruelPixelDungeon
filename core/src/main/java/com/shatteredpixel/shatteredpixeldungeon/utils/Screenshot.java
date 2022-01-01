package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.watabou.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.Deflater;


public class Screenshot {
    public static boolean makeScreenshot() {
        FileHandle fh = FileUtils.getFileHandle(Files.FileType.External, "TCPD Screenshots/", new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS", Locale.US).format(Calendar.getInstance().getTime())+".png");

        Pixmap pixmap= Pixmap.createFromFrameBuffer(0,0, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        PixmapIO.writePNG(fh, pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
        return false;
    }
}
