package com.example.mark_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int Current_Day_Num = 1;
    private int Week = 1;
    private Button Week_Btn;
    //private ListView Main_List;
    Cursor query = null;

    public ArrayList<Schedule_Item> Schedule_Items_List = new ArrayList<Schedule_Item>();
    //GridListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView Top_Menu = findViewById(R.id.top_navigation_menu); // Верхние и нижние MenuBar
        Top_Menu.setOnNavigationItemSelectedListener(top_menu_listener);
        BottomNavigationView Bot_Menu = findViewById(R.id.bottom_navigation_menu);
        Bot_Menu.setOnNavigationItemSelectedListener(bot_menu_listener);

        //Main_List = (ListView) findViewById(R.id.List);
        //Main_List.setAdapter(adapter);

        Update_List_via_Database(Week,Current_Day_Num);
    }

    public void Change_Week_Stage(View view){
        Week_Btn = findViewById(R.id.Week_Stage);
        if (Week == 1){
            Week = 2;
            Week_Btn.setText(R.string.Week_2);
        }
        else{
            Week = 1;
            Week_Btn.setText(R.string.Week_1);
        }
        Update_List_via_Database(Week,Current_Day_Num);
    }

    public void Update_List_via_Database (Integer Week, Integer Day){
        TextView debug = findViewById(R.id.debug_textview);
        ListView Main_List_View = findViewById(R.id.List);
        ArrayList<Schedule_Item> DB_Items_list = new ArrayList<>();
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("save_2.db", MODE_PRIVATE, null);
        Cursor cursor = null;

        switch (Week){  // В зависимости от текущей недели и дня недели выбирается нужная таблица из базы данных save_n.db
            case 1:
                switch (Day){
                    case 1:
                        cursor = db.rawQuery("SELECT * FROM Monday_1;",null);
                        break;
                    case 2:
                        cursor = db.rawQuery("SELECT * FROM Tuesday_1;", null);
                        break;
                    case 3:
                        cursor = db.rawQuery("SELECT * FROM Wednesday_1;", null);
                        break;
                    case 4:
                        cursor = db.rawQuery("SELECT * FROM Thursday_1;", null);
                        break;
                    case 5:
                        cursor = db.rawQuery("SELECT * FROM Friday_1;", null);
                        break;
                    case 6:
                        cursor = db.rawQuery("SELECT * FROM Saturday_1;", null);
                        break;
                    case 7:
                        cursor = db.rawQuery("SELECT * FROM Sunday_1;", null);
                        break;
                }
                break;
            case 2:
                switch (Day){
                    case 1:
                        cursor = db.rawQuery("SELECT * FROM Monday_2;", null);
                        break;
                    case 2:
                        cursor = db.rawQuery("SELECT * FROM Tuesday_2;", null);
                        break;
                    case 3:
                        cursor = db.rawQuery("SELECT * FROM Wednesday_2;", null);
                        break;
                    case 4:
                        cursor = db.rawQuery("SELECT * FROM Thursday_2;", null);
                        break;
                    case 5:
                        cursor = db.rawQuery("SELECT * FROM Friday_2;", null);
                        break;
                    case 6:
                        cursor = db.rawQuery("SELECT * FROM Saturday_2;", null);
                        break;
                    case 7:
                        cursor = db.rawQuery("SELECT * FROM Sunday_2;", null);
                        break;
                }
                break;
        }

        debug.setText(debug.getText()+"     Column count  " + cursor.getColumnCount());
        debug.setText(debug.getText()+"     Count  " + cursor.getCount());
        debug.setText(debug.getText()+"     week-" + Week);
        debug.setText(debug.getText()+"     day-" + Day);
        if(cursor.moveToFirst()){
            debug.setText(debug.getText()+"     FULL  ");
            do{
                String time0 = cursor.getString(0);
                String time1 = cursor.getString(1);
                String item_name = cursor.getString(2);
                String teacher_name = cursor.getString(3);
                String item_mode = cursor.getString(4);
                String item_auditorium = cursor.getString(5);
                String item_building = cursor.getString(6);

                DB_Items_list.add(new Schedule_Item(time0, time1, item_name, teacher_name, item_mode, item_auditorium, item_building));
                debug.setText(debug.getText()+ " " + time0+ " " + time1);
            }
            while(cursor.moveToNext());
        }
        else{
            debug.setText(debug.getText()+"     EMPTY  ");
        }

        cursor.close();
        db.close();
        try{
            GridListAdapter new_adapter = new GridListAdapter(MainActivity.this, DB_Items_list);
            Main_List_View.setAdapter(new_adapter);
        }
        catch (Exception e){
            TextView temp = findViewById(R.id.Empty_Day_Text);  // ошибка может быть только если нужная таблица в бд пустая, значит пар сегодня нет
            temp.setEnabled(true);                              // надпись об отсутствии пар включается
            temp.setText(e.toString());
        }
    }





    private BottomNavigationView.OnNavigationItemSelectedListener bot_menu_listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case  R.id.add_button: // Добавление строки в список предметов
                    Intent intent = new Intent(MainActivity.this, Activity_Add_new_Item.class);

                    String temp = String.valueOf(Current_Day_Num);
                    String temp2 = String.valueOf(Week);
                    intent.putExtra("Day", temp);
                    intent.putExtra("Week", temp2);

                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    break;
                case R.id.trash_button: // Удаление строки
                    debug();
                    break;
                case R.id.settings_button:

                    break;
                case R.id.menu_button:

                    break;
            }
            return true;
        }
    };

    private void debug(){

        try{
            Update_List_via_Database(Week,Current_Day_Num);

            TextView temp = findViewById(R.id.Empty_Day_Text);  // ошибка может быть только если нужная таблица в бд пустая, значит пар сегодня нет
            temp.setEnabled(true);                              // надпись об отсутствии пар включается
            temp.setText("Ok");
        }
        catch (Exception e){
            TextView temp = findViewById(R.id.Empty_Day_Text);  // ошибка может быть только если нужная таблица в бд пустая, значит пар сегодня нет
            temp.setEnabled(true);                              // надпись об отсутствии пар включается
            temp.setText(e.toString());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener top_menu_listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            TextView Header = findViewById(R.id.day_of_the_week);
            ListView Main_List = (ListView) findViewById(R.id.List);

            switch (menuItem.getItemId()){
                case R.id.left_arrow_button:
                    switch (Header.getText().toString()){
                        case "Понедельник":
                            Header.setText("Воскресенье");
                            Current_Day_Num = 7;

                            break;
                        case "Воскресенье":
                            Header.setText("Суббота");
                            Current_Day_Num = 6;

                            break;
                        case "Суббота":
                            Header.setText("Пятница");
                            Current_Day_Num = 5;

                            break;
                        case "Пятница":
                            Header.setText("Четверг");
                            Current_Day_Num = 4;

                            break;
                        case "Четверг":
                            Header.setText("Среда");
                            Current_Day_Num = 3;

                            break;
                        case "Среда":
                            Header.setText("Вторник");
                            Current_Day_Num = 2;

                            break;
                        case "Вторник":
                            Header.setText("Понедельник");
                            Current_Day_Num = 1;

                            break;
                    }
                    break;

                case R.id.right_arrow_button:
                    switch (Header.getText().toString()){
                        case "Понедельник":
                            Header.setText("Вторник");
                            Current_Day_Num = 2;

                            break;
                        case "Вторник":
                            Header.setText("Среда");
                            Current_Day_Num = 3;

                            break;
                        case "Среда":
                            Header.setText("Четверг");
                            Current_Day_Num = 4;

                            break;
                        case "Четверг":
                            Header.setText("Пятница");
                            Current_Day_Num = 5;

                            break;
                        case "Пятница":
                            Header.setText("Суббота");
                            Current_Day_Num = 6;

                            break;
                        case "Суббота":
                            Header.setText("Воскресенье");
                            Current_Day_Num = 7;

                            break;
                        case "Воскресенье":
                            Header.setText("Понедельник");
                            Current_Day_Num = 1;

                            break;
                    }
                    break;
            }
            Update_List_via_Database(Week,Current_Day_Num);
            return true;
        }
    };
}
