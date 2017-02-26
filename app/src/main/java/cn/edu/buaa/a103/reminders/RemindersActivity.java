package cn.edu.buaa.a103.reminders;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView= (ListView) findViewById(R.id.lv_reminders);
        mListView.setDivider(null);
        mDbAdapter=new RemindersDbAdapter(this);
        mDbAdapter.open();

        if (savedInstanceState==null){
            //清除所有数据
            mDbAdapter.deleteAllReminders();
            //Add some data
            insertSomeReminders();

        }

        Cursor cursor=mDbAdapter.fetchAllReminders();

        //from columns defined in the db
        String[] from=new String[]{
                RemindersDbAdapter.COL_CONTENT
        };

        //to the ids of views in the layout
        int[] to=new int[]{R.id.row_text};

        mCursorAdapter=new ReminderSimpleCursorAdapter(RemindersActivity.this,R.layout.reminders_row,
                cursor,from,to,0);
        //The arrayAdapter is the controller in our model-view-controller relationship.(controller)
//        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.reminders_row,R.id.row_text,
//                new String[]{"first record","second record","third record"});

        //the cursorAdapter(controller) is now updating the listView(view) with data from the db(model)
        mListView.setAdapter(mCursorAdapter);

        //Abbreviated for brevity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(RemindersActivity.this);

                ListView modeListView=new ListView(RemindersActivity.this);
                String[] modes=new String[]{"Edit Reminder","Delete Reminder"};
                ArrayAdapter<String> modeAdapter=new ArrayAdapter<String>(RemindersActivity.this,
                        android.R.layout.simple_list_item_1,android.R.id.text1,modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog=builder.create();
                dialog.show();

                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            Toast.makeText(RemindersActivity.this,"edit"+masterListPosition,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(RemindersActivity.this,"delete"+masterListPosition,
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();  //将对话框彻底删除
                    }
                });
                Toast.makeText(RemindersActivity.this,"clicked"+masterListPosition,Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater=mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete_reminder:
                            for (int nC=mCursorAdapter.getCount()-1;nC>=0;nC--){
                                if (mListView.isItemChecked(nC)){
                                    mDbAdapter.deleteRemindersById(getIdFromPosition(nC));
                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

    }

    private int getIdFromPosition(int nC) {
        return (int) mCursorAdapter.getItemId(nC);
    }


    private void insertSomeReminders() {
        mDbAdapter.createReminder("Buy Learn Android Studio",true);
        mDbAdapter.createReminder("Send birthday gift",true);
        mDbAdapter.createReminder("Dinner at the Gage on Friday",false);
        mDbAdapter.createReminder("String squash racket",false);
        mDbAdapter.createReminder("Shovel and salt walkways",true);
        mDbAdapter.createReminder("Prepare Advanced Android syllabus",false);
        mDbAdapter.createReminder("Buy new office chair",false);
        mDbAdapter.createReminder("Buy phone for Ms Peng",false);
        mDbAdapter.createReminder("Buy birthday gift for Ms Peng",true);
        mDbAdapter.createReminder("Finish the DataSearch Module today",true);
        mDbAdapter.createReminder("Buy Learn Android Studio",true);
        mDbAdapter.createReminder("Send birthday gift",true);
        mDbAdapter.createReminder("Dinner at the Gage on Friday",false);
        mDbAdapter.createReminder("String squash racket",false);
        mDbAdapter.createReminder("Shovel and salt walkways",true);
        mDbAdapter.createReminder("Prepare Advanced Android syllabus",false);
        mDbAdapter.createReminder("Buy new office chair",false);
        mDbAdapter.createReminder("Buy phone for Ms Peng",false);
        mDbAdapter.createReminder("Buy birthday gift for Ms Peng",true);
        mDbAdapter.createReminder("Finish the DataSearch Module today",true);
        mDbAdapter.createReminder("Buy Learn Android Studio",true);
        mDbAdapter.createReminder("Send birthday gift",true);
        mDbAdapter.createReminder("Dinner at the Gage on Friday",false);
        mDbAdapter.createReminder("String squash racket",false);
        mDbAdapter.createReminder("Shovel and salt walkways",true);
        mDbAdapter.createReminder("Prepare Advanced Android syllabus",false);
        mDbAdapter.createReminder("Buy new office chair",false);
        mDbAdapter.createReminder("Buy phone for Ms Peng",false);
        mDbAdapter.createReminder("Buy birthday gift for Ms Peng",true);
        mDbAdapter.createReminder("Finish the DataSearch Module today",true);
    }
}
