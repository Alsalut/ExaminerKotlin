package com.alex.examinerkotlin;

// Просмотр списка вопросов

import static com.alex.examinerkotlin.MainActivity.listQuestion;
import static com.alex.examinerkotlin.MainActivity.listTask;
import static com.alex.examinerkotlin.MainActivity.listTest;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowActivity extends ListActivity implements IConstant, View.OnClickListener, AdapterView.OnItemClickListener
{
   // объявляем переменные
   private ImageButton imageButtonPlusShow, imageButtonNextShow;
   private TextView textView;
   ArrayAdapter< String > arrayAdapter;
   int count = 1;

   @Override
   protected void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_show );

      initVariable(); // сопоставляем переменные виджетам
      setListener(); // назначаем слушателей
   }

   @Override
   protected void onResume()
   {
      super.onResume();

      showList(); // показываем список
   }

   // сопоставляем переменные виджетам
   private void initVariable()
   {
      imageButtonPlusShow = findViewById( R.id.image_button_plus_activity_show );
      imageButtonNextShow = findViewById( R.id.image_button_next_activity_show );
      textView = findViewById( R.id.text_view_activity_show );
   }

   // назначаем слушателей
   private void setListener()
   {
      imageButtonPlusShow.setOnClickListener( this );
      imageButtonNextShow.setOnClickListener( this );

      getListView().setOnItemClickListener( this );
   }

   // показываем список
   private void showList()
   {
      switch( count++ )
      {
         case 1:
            show( listQuestion, R.string.show_questions ); // список "Вопросы"
            break;
         case 2:
            show( listTest, R.string.show_tests ); // список "Тесты"
            break;
         case 3:
            show( listTask, R.string.show_tasks ); // список "Задачи"
            count = 1; // снова делаем count=1 , чтобы зациклить показ ( "Просмотр вопросов" -> "Просмотр тестов" -> "Просмотр задач" )
      }

      // назначаем адаптер списку
      setListAdapter( arrayAdapter );
   }

   // функция показа списка
   private void show( ArrayList< String > list, int title )
   {
      // показываем заголовок списка ( "Просмотр вопросов" или "Просмотр тестов" или "Просмотр задач" )
      textView.setText( title );

      // создаём адаптёр arrayAdapter для списка ( "Вопросы" или "Тесты" или "Задачи" )
      arrayAdapter = new ArrayAdapter<>( this, R.layout.list_view, list );
   }

   // выполнится при нажатии на кнопку
   @Override
   public void onClick( View btn )
   {
      switch( btn.getId() )
      {
         case R.id.image_button_plus_activity_show:
            teleport( 0, --count, -1 ); // переходим в окно "Редактирование вопроса и ответа" ( EditingActivity )
            break;
         case R.id.image_button_next_activity_show:
            showList();
      }
   }

   // выполнится при кратком нажатии на элемент списка
   @Override
   public void onItemClick( AdapterView< ? > adapterView, View element, int index, long l )
   {
      teleport( 1, --count, index ); // переходим в окно "Редактирование вопроса и ответа" ( EditingActivity )
   }

   // переходим в окно "Редактирование вопроса и ответа" ( EditingActivity )
   private void teleport( final int add, final int count, final int index )
   {
      // --count чтобы при возврате на эту Activity отобразился тот же список

      Intent intent = new Intent( this, EditingActivity.class ); // создаём intent
      intent.putExtra( ADD, add );     // 0 - значит пустые поля EditText ( создаём новую пару вопрос + ответ )
                                       // 1 - значит заполненные поля EditText ( редактируем пару вопрос + ответ )
      intent.putExtra( COUNT, count ); // по значению count определим, какой список нужно редактировать :
                                       // 1 - "Вопросы"
                                       // 2 - "Тесты"
                                       // 3 - "Задачи"
      intent.putExtra( INDEX, index ); // индекс элемента в этом списке
                                       // если -1, то создаём новую пару вопрос + ответ
      startActivity( intent );         // переход на EditingActivity для :
                                       // добавления новой пары вопрос + ответ
                                       // или
                                       // редактирования пары вопрос + ответ
   }
}