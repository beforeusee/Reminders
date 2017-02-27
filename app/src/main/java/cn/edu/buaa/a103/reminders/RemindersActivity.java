package cn.edu.buaa.a103.reminders;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;
    private Button mAddButton;

    //版本
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView= (ListView) findViewById(R.id.lv_reminders);
        mListView.setDivider(null);
        mDbAdapter=new RemindersDbAdapter(this);
        mDbAdapter.open();

/*        //检查是否有任何已保存的实例；如果没有就先创建一些示例数据
        if (savedInstanceState==null){
            //清除所有数据
            mDbAdapter.deleteAllReminders();
            //Add some data
            insertSomeReminders();
        }*/

        mAddButton= (Button) findViewById(R.id.bt_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireCustomDialog(null);
            }
        });

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
                        //edit reminder
                        if (position==0){
                            int nId=getIdFromPosition(masterListPosition);
                            Reminder reminder=mDbAdapter.fecthReminderById(nId);
                            fireCustomDialog(reminder);
                        }else {
                            mDbAdapter.deleteRemindersById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        }
                        dialog.dismiss();  //将对话框彻底删除
                    }
                });
                Toast.makeText(RemindersActivity.this,"clicked"+masterListPosition,Toast.LENGTH_SHORT).show();
            }
        });

        //多选模式下的操作
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

    //插入编辑对话框
    private void fireCustomDialog(final Reminder reminder){
        //custom dialog
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView titleView= (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom= (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton= (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox= (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout= (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation=(reminder!=null);

        //this is for an edit
        if (isEditOperation){
            titleView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant()==1);
            editCustom.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText=editCustom.getText().toString();
                if (isEditOperation){
                    Reminder reminderEdited=new Reminder(reminder.getId(),reminderText,checkBox.isChecked()?1:0);
                    mDbAdapter.updateReminder(reminderEdited);
                }else {
                    mDbAdapter.createReminder(reminderText,checkBox.isChecked());  //创建新的reminder
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });
        Button buttonCancel= (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //插入示例的函数
    private void insertSomeReminders() {
        mDbAdapter.createReminder("This is a reminder",true);
        mDbAdapter.createReminder("This is a reminder that support CHOICE_MODE_MULTIPLE_MODAL",true);
        mDbAdapter.createReminder("This reminder uses SQLite",true);
    }

    @Override
    protected void onDestroy() {
        mDbAdapter.close();          //当执行App的onDestroy时，关闭数据库
        super.onDestroy();
    }
}
