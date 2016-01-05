package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by sudarsan on 6/1/16.
 */
public class TodayWidgetIntentService extends IntentService {
    private String[] fragmentdate = new String[1];

    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    @Override
    protected void onHandleIntent(Intent intent) {
        Date fragmentdateNow = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        fragmentdate[0] = mformat.format(fragmentdateNow);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        Uri scoreForTodayUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = getContentResolver().query(scoreForTodayUri,null,null,fragmentdate,null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.scores_list_item);

            views.setTextViewText(R.id.home_name,data.getString(COL_HOME));
            views.setTextViewText(R.id.away_name, data.getString(COL_AWAY));
            views.setTextViewText(R.id.score_textview,
                    Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS)));
            views.setTextViewText(R.id.data_textview, data.getString(COL_MATCHTIME));
            views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    data.getString(COL_HOME)));
            views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    data.getString(COL_AWAY)
            ));

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.today_widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
