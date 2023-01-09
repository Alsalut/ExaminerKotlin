package com.alex.examinerkotlin;

// Редактирование вопроса и ответа
// Создание новой пары : вопрос + ответ

import static com.alex.examinerkotlin.MainActivity.listAnswerQuestion;
import static com.alex.examinerkotlin.MainActivity.listAnswerTask;
import static com.alex.examinerkotlin.MainActivity.listAnswerTest;
import static com.alex.examinerkotlin.MainActivity.listQuestion;
import static com.alex.examinerkotlin.MainActivity.listTask;
import static com.alex.examinerkotlin.MainActivity.listTest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditingActivity extends AppCompatActivity implements IConstant, View.OnClickListener
{
   // объявляем переменные
   private ImageButton imageButtonDelete, imageButtonSave;
   private EditText editTextUp, editTextDown;

   private int add, count, index;

   @Override
   protected void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_editing );

      initVariable(); // сопоставляем переменные виджетам
      setListener(); // назначаем слушателей
   }

   @Override
   protected void onResume()
   {
      super.onResume();

      inIntent(); // получаем Intent
      fillEditText(); // заполняем поля EditText
   }

   // сопоставляем переменные виджетам
   private void initVariable()
   {
      imageButtonDelete = findViewById( R.id.image_button_delete );
      imageButtonSave = findViewById( R.id.image_button_save );
      editTextUp = findViewById( R.id.edit_text_up );
      editTextDown = findViewById( R.id.edit_text_down );
   }

   // назначаем слушателей
   private void setListener()
   {
      imageButtonDelete.setOnClickListener( this );
      imageButtonSave.setOnClickListener( this );
   }

   // получаем Intent
   private void inIntent()
   {
      Intent intent = getIntent();
      add = intent.getIntExtra( ADD, -1 );     // 0 - значит пустые поля EditText ( создаём новую пару вопрос + ответ )
                                                         // 1 - значит заполненные поля EditText ( редактируем пару вопрос + ответ )
      count = intent.getIntExtra( COUNT, -1 ); // по значению count определим, какой список нужно редактировать :
                                                         // 1 - "Вопросы"
                                                         // 2 - "Тесты"
                                                         // 3 - "Задачи"
      index = intent.getIntExtra( INDEX, -1 ); // индекс элемента в этом списке
                                                         // если -1, то создаём новую пару вопрос + ответ
   }

   // заполняем поля EditText
   private void fillEditText()
   {
      switch( count )
      {
         case 1:
            fill( index, listQuestion, listAnswerQuestion ); // списки "Вопрос" + "Ответ"
            break;
         case 2:
            fill( index, listTest, listAnswerTest ); // списки "Тест" + "Ответ"
            break;
         case 3:
            fill( index, listTask, listAnswerTask ); // списки "Задача" + "Ответ"
      }
   }

   // заполняем поля EditText
   private void fill( final int index, ArrayList< String> list, ArrayList< String> listAnswer )
   {
      if( index == -1 ) clearEditText(); // очищаем поля EditText
      else
      {
         editTextUp.setText( list.get( index ) );           // вписываем "Вопрос" или "Тест" или "Задачу"
         editTextDown.setText( listAnswer.get( index ) );   // вписываем ответ на "Вопрос" или "Тест" или "Задачу"
      }
   }

   // выполнится при нажатии на кнопку
   @Override
   public void onClick( View view )
   {
      switch( view.getId() )
      {
         case R.id.image_button_delete:
            clearEditText(); // очистка полей EditText
            break;
         case R.id.image_button_save:
            saveElement(); // сохраняем новый или изменённый элемент
      }
   }

   // очистка полей EditText
   private void clearEditText()
   {
      editTextUp.setText( CLEAR );
      editTextDown.setText( CLEAR );
   }

   // сохраняем новый или изменённый элемент
   private void saveElement()
   {
      switch( count )
      {
         case 1:
            save( index, listQuestion, listAnswerQuestion ); // списки "Вопрос" + "Ответ"
            break;
         case 2:
            save( index, listTest, listAnswerTest ); // списки "Тест" + "Ответ"
            break;
         case 3:
            save( index, listTask, listAnswerTask ); // списки "Задача" + "Ответ"
      }

      // сохраняем в Локальную базу данных
      saveToLocalBase();

      // завершаем эту Activity
      finish();
   }

   // сохраняем новый или изменённый элемент
   private void save( final int index, ArrayList< String> list, ArrayList< String> listAnswer )
   {
      // если (add == 0) ( т.е. добавляем новый "Вопрос" + "Ответ" )
      // и хотя бы поле editText ( "Вопрос" ) пустое,
      // то ничего не добавляем, просто выходим
      // иначе добавляем текст из полей editText в конец списков "Вопрос" + "Ответ"
      // и выходим
      if( (add == 0) & ((editTextUp.getText().toString()).equals( CLEAR )) ) return;
      else if( add == 0 )
      {
         list.add( editTextUp.getText().toString() );
         listAnswer.add( editTextDown.getText().toString() );
      }

      // если (add == 1) ( т.е. редактируем элемент списка )
      // и хотя бы поле editText ( "Вопрос" ) пустое,
      // то удаляем ( "Вопрос" + "Ответ" ) по индексу ( index )
      // и выходим
      // иначе, заменяем "Вопрос" + "Ответ" по индексу ( index ) текстом из полей editText
      if( (add == 1) & ((editTextUp.getText().toString()).equals( CLEAR )) )
      {
         list.remove( index );
         listAnswer.remove( index );
      }
      else if( add == 1 )
      {
         list.set( index, editTextUp.getText().toString() );
         listAnswer.set( index, editTextDown.getText().toString() );
      }
   }

   // сохраняем в Локальную базу данных
   private void saveToLocalBase()
   {
      switch( count )
      {
         case 1:
            saveToBase( STR_QUESTION, listQuestion, STR_ANSWER_QUESTION, listAnswerQuestion ); // списки "Вопрос" + "Ответ"
            break;
         case 2:
            saveToBase( STR_TEST, listTest, STR_ANSWER_TEST, listAnswerTest ); // списки "Тест" + "Ответ"
            break;
         case 3:
            saveToBase( STR_TASK, listTask, STR_ANSWER_TASK, listAnswerTask ); // списки "Задача" + "Ответ"
      }
   }

   // сохраняем в Локальную базу данных
   private void saveToBase( final String str, ArrayList< String> list, final String strAnswer, ArrayList< String> listAnswer )
   {
      SharedPreferences sharedPreferences = getSharedPreferences( LOCAL_BASE, MODE_PRIVATE );
      SharedPreferences.Editor editor = sharedPreferences.edit();

      editor.putString( str, createString( list ) );
      editor.putString( strAnswer, createString( listAnswer ) );
      editor.apply();
   }

   // возвращаем строку из списка
   // с помощью сплиттера ( "&SPLITTER&" )
   private String createString( ArrayList< String> list )
   {
      StringBuilder text = new StringBuilder( list.get( 0 ) );

      for( String element : list )
      {
         if( element.equals( list.get( 0 ) ) ) continue; // пропускаем первый элемент списка

         text.append( SPLITTER ).append( element ); // добавляем сплиттер и следующий элемент
      }

      return text.toString(); // возвращаем готовую строку
   }
}