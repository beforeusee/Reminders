package cn.edu.buaa.a103.reminders;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView= (ListView) findViewById(R.id.lv_reminders);
        mListView.setDivider(null);
        mDbAdapter=new RemindersDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor=mDbAdapter.fetchAllReminders();

        String[] from=new String[]{
                RemindersDbAdapter.COL_CONTENT
        };

        int[] to=new int[]{R.id.row_text};

        mCursorAdapter=new ReminderSimpleCursorAdapter(RemindersActivity.this,R.layout.reminders_row,
                cursor,from,to,0);
        //The arrayAdapter is the controller in our model-view-controller relationship.(controller)
//        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.reminders_row,R.id.row_text,
//                new String[]{"first record","second record","third record"});

        mListView.setAdapter(mCursorAdapter);
    }
}
