package at.lukads13.vocab;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Lädt eine neue APK von GitHub herunter und öffnet den Android-Installationsdialog.
 *
 * JS: Capacitor.Plugins.AppUpdater
 *   download({ url })        → { path, bytes }, sendet Ereignis "progress" { percent }
 *   canInstall()             → { allowed }   (darf diese App andere Apps installieren?)
 *   openInstallSettings()    → öffnet die Einstellung „Apps aus dieser Quelle zulassen“
 *   install({ path })        → { started } oder { needsPermission: true }
 */
@CapacitorPlugin(name = "AppUpdater")
public class AppUpdaterPlugin extends Plugin {

    @PluginMethod
    public void download(PluginCall call) {
        final String url = call.getString("url");
        if (url == null || url.isEmpty()) {
            call.reject("url fehlt");
            return;
        }
        new Thread(() -> {
            HttpURLConnection con = null;
            try {
                File dir = new File(getContext().getCacheDir(), "updates");
                if (!dir.exists() && !dir.mkdirs()) throw new IOException("Ordner nicht anlegbar");
                File out = new File(dir, "update.apk");
                if (out.exists() && !out.delete()) throw new IOException("Alte Datei nicht löschbar");

                // GitHub leitet auf einen anderen Server weiter – Weiterleitungen selbst folgen
                URL u = new URL(url);
                int redirects = 0;
                while (true) {
                    con = (HttpURLConnection) u.openConnection();
                    con.setInstanceFollowRedirects(false);
                    con.setConnectTimeout(15000);
                    con.setReadTimeout(30000);
                    con.setRequestProperty("Accept", "application/octet-stream");
                    int code = con.getResponseCode();
                    if (code >= 300 && code < 400 && redirects < 6) {
                        String loc = con.getHeaderField("Location");
                        con.disconnect();
                        if (loc == null) throw new IOException("Weiterleitung ohne Ziel");
                        u = new URL(u, loc);
                        redirects++;
                        continue;
                    }
                    if (code != HttpURLConnection.HTTP_OK) throw new IOException("HTTP " + code);
                    break;
                }

                long total = con.getContentLengthLong();
                long done = 0;
                int lastPct = -1;
                try (InputStream in = new BufferedInputStream(con.getInputStream());
                     OutputStream os = new FileOutputStream(out)) {
                    byte[] buf = new byte[16384];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        os.write(buf, 0, n);
                        done += n;
                        if (total > 0) {
                            int pct = (int) (done * 100 / total);
                            if (pct != lastPct) {
                                lastPct = pct;
                                JSObject ev = new JSObject();
                                ev.put("percent", pct);
                                notifyListeners("progress", ev);
                            }
                        }
                    }
                }
                JSObject ret = new JSObject();
                ret.put("path", out.getAbsolutePath());
                ret.put("bytes", done);
                call.resolve(ret);
            } catch (Exception e) {
                call.reject("Download fehlgeschlagen: " + e.getMessage());
            } finally {
                if (con != null) con.disconnect();
            }
        }).start();
    }

    private boolean installAllowed() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                || getContext().getPackageManager().canRequestPackageInstalls();
    }

    @PluginMethod
    public void canInstall(PluginCall call) {
        JSObject r = new JSObject();
        r.put("allowed", installAllowed());
        call.resolve(r);
    }

    @PluginMethod
    public void openInstallSettings(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent i = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + getContext().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        }
        call.resolve();
    }

    @PluginMethod
    public void install(PluginCall call) {
        String path = call.getString("path");
        if (path == null) {
            call.reject("path fehlt");
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            call.reject("Datei nicht gefunden");
            return;
        }
        if (!installAllowed()) {
            JSObject r = new JSObject();
            r.put("needsPermission", true);
            call.resolve(r);
            return;
        }
        try {
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            JSObject r = new JSObject();
            r.put("started", true);
            call.resolve(r);
        } catch (Exception e) {
            call.reject("Installation konnte nicht gestartet werden: " + e.getMessage());
        }
    }
}
