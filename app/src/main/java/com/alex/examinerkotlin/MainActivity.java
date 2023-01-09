package com.alex.examinerkotlin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements IConstant, View.OnClickListener
{
   // объявляем переменные
   private ImageButton imageButtonPlus, imageButtonNext, imageButtonShowAnswer, imageButtonShowQuestion;
   private TextView textViewQuestion;

   // индекс для прохода по всем вопросам
   int indexCount = 0;

   // локальная база данных
   SharedPreferences sharedPreferences;
   SharedPreferences.Editor editor;

   // создаём списки для вопросов, тестов, задач и ответов к ним
   static ArrayList< String > listQuestion = new ArrayList();
   static ArrayList< String > listAnswerQuestion = new ArrayList();
   static ArrayList< String > listTest = new ArrayList();
   static ArrayList< String > listAnswerTest = new ArrayList();
   static ArrayList< String > listTask = new ArrayList();
   static ArrayList< String > listAnswerTask = new ArrayList();

   // создаём списки для индексов
   static ArrayList< Integer > listIndexQuestion = new ArrayList<>();
   static ArrayList< Integer > listIndexTest = new ArrayList<>();
   static ArrayList< Integer > listIndexTask = new ArrayList<>();

   @Override
   protected void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_main );

      initVariable(); // сопоставляем переменные виджетам
      setListener(); // назначаем слушателей
      initLocalBase(); // инициализация локальной базы данных
   }

   @Override
   protected void onResume()
   {
      super.onResume();

      getStringFromLocalBase(); // получаем строки из локальной базы данных
      fillSetIndex(); // заполняем списки индексов
      showQuestion(); // показываем вопрос
      //      writeToLocalBase(); // запись в локальную базу данных
   }

   // сопоставляем переменные виджетам
   private void initVariable()
   {
      imageButtonPlus = findViewById( R.id.image_button_plus );
      imageButtonNext = findViewById( R.id.image_button_next );
      imageButtonShowAnswer = findViewById( R.id.image_button_show_answer );
      imageButtonShowQuestion = findViewById( R.id.image_button_show_question );
      textViewQuestion = findViewById( R.id.text_view_question );
   }

   // назначаем слушателей
   private void setListener()
   {
      imageButtonPlus.setOnClickListener( this );
      imageButtonNext.setOnClickListener( this );
      imageButtonShowAnswer.setOnClickListener( this );
      imageButtonShowQuestion.setOnClickListener( this );
   }

   // инициализация локальной базы данных
   private void initLocalBase()
   {
      sharedPreferences = getSharedPreferences( LOCAL_BASE, MODE_PRIVATE );
      editor = sharedPreferences.edit();
   }

   // получаем строки из локальной базы данных
   private void getStringFromLocalBase()
   {
      // получаем строки из локальной базы данных
      final String stringQuestion = sharedPreferences.getString( STR_QUESTION, EMPTY );
      final String stringAnswerQuestion = sharedPreferences.getString( STR_ANSWER_QUESTION, EMPTY );
      final String stringTest = sharedPreferences.getString( STR_TEST, EMPTY );
      final String stringAnswerTest = sharedPreferences.getString( STR_ANSWER_TEST, EMPTY );
      final String stringTask = sharedPreferences.getString( STR_TASK, EMPTY );
      final String stringAnswerTask = sharedPreferences.getString( STR_ANSWER_TASK, EMPTY );

      // заполняем списки
      fillListFromString( stringQuestion, stringAnswerQuestion, stringTest, stringAnswerTest, stringTask, stringAnswerTask );
   }

   // заполняем списки
   private void fillListFromString( final String Question, final String AnswerQuestion, final String Test, final String AnswerTest, final String Task, final String AnswerTask )
   {
      // заполняем списки
      // из строк через сплиттер
      fillList( Question, listQuestion );
      fillList( AnswerQuestion, listAnswerQuestion );

      fillList( Test, listTest );
      fillList( AnswerTest, listAnswerTest );

      fillList( Task, listTask );
      fillList( AnswerTask, listAnswerTask );
   }

   // заполняем списки
   // из строк через сплиттер
   private void fillList( final String Quiz, ArrayList< String> listQuiz )
   {
      // очищаем список
      listQuiz.clear();

      // заполняем список listQuiz
      // из строки Quiz через сплиттер SPLITTER
      if( !Quiz.equals( EMPTY ) ) listQuiz.addAll( Arrays.asList( Quiz.split( SPLITTER ) ) );
   }

   // заполняем списки индексов
   private void fillSetIndex()
   {
      fillSet( listQuestion, listIndexQuestion, QUESTION );
      fillSet( listTest, listIndexTest, TEST );
      fillSet( listTask, listIndexTask, TASK );
   }

   // выполнится при нажатии на кнопку
   @Override
   public void onClick( View view )
   {
      switch( view.getId() )
      {
         case R.id.image_button_plus:
            startActivity( new Intent( this, ShowActivity.class) ); // переходит в окно "Просмотр списка вопросов"
            break;
         case R.id.image_button_next:
            showBtnAnswer(); // делаем кнопку "Ответ" видимой
            indexCount++; // увеличиваем индекс
            if( isEnd( indexCount, listIndexQuestion, listIndexTest, listIndexTask ) )
               finishAndRemoveTask(); // если конец теста, закрываем приложение
            else showQuestion(); // показываем вопрос
            break;
         case R.id.image_button_show_answer:
            showAnswer(); // показываем ответ
            showBtnQuestion(); // делаем кнопку "Вопрос" видимой
            break;
         case R.id.image_button_show_question:
            showQuestion(); // показываем вопрос
            showBtnAnswer(); // делаем кнопку "Ответ" видимой
      }
   }

   // если дошли до конца всех вопросов, тестов и задач
   // возвращает true
   private boolean isEnd( int index, ArrayList< Integer > IndexQuestion, ArrayList< Integer > IndexTest, ArrayList< Integer > IndexTask )
   {
      return index == ( IndexQuestion.size() + IndexTest.size() + IndexTask.size() );
   }

   // делаем кнопку "Вопрос" видимой
   private void showBtnQuestion()
   {
      imageButtonShowAnswer.setVisibility( View.INVISIBLE );
      imageButtonShowQuestion.setVisibility( View.VISIBLE );
   }

   // делаем кнопку "Ответ" видимой
   private void showBtnAnswer()
   {
      imageButtonShowQuestion.setVisibility( View.INVISIBLE );
      imageButtonShowAnswer.setVisibility( View.VISIBLE );
   }

   // функция заполняет список индексов
   // передаём список вопросов, тестов или задач
   // объявленный ранее список индексов для вопросов, тестов или задач
   // и константу ( QUESTION, TEST или TASK )
   private void fillSet( final ArrayList listQuiz, ArrayList listIndex, int count )
   {
      // узнаём количество элементов listQuiz ( т.е. вопросов, тестов или задач )
      final int allElements = listQuiz.size();

      // если список listQuiz ( вопросов ) пуст
      // то выходим из функции
      if( allElements == 0 ) return;

      // очищаем список индексов
      listIndex.clear();

      // создаём множество индексов
      LinkedHashSet< Integer > setIndex = new LinkedHashSet<>();

      // очищаем множество индексов
      setIndex.clear();

      // если список listQuiz ( вопросов ) меньше нужного кол-ва для тестов
      // то множество setIndex ( индексов ) будет равно кол-ву вопросов
      if( allElements < count ) count = allElements;

      // задаём рандомайзер
      // в зависимости от текущего времени
      Random random = new Random( System.currentTimeMillis() );

      // бесконечный цикл
      // для заполнения множества setIndex ( индексов )
      while( true )
      {
         // получаем случайный индекс вопроса, теста или задачи
         final int randomIndex = random.nextInt( allElements );

         // добавляем его в множество setIndex ( индексов )
         // будет добавлен только уникальный индекс
         setIndex.add( randomIndex );

         // если в множестве setIndex ( индексов )
         // уже count элементов ( или 20, 12 или 3 элемента ),
         // то заполняем список listIndex ( индексов )
         // и выходим из цикла
         if( setIndex.size() == count )
         {
            for( int element : setIndex )
            {
               listIndex.add( element );
            }
            break;
         }
      }
   }

   // показываем вопрос
   private void showQuestion()
   {
      // создаём константы
      // для улучшения читабельности кода
      final int I = indexCount;
      final int A = listIndexQuestion.size();
      final int B = listIndexTest.size();

      // индексы вопросов берём в listIndex*
      if( I < A )
      {
         setTitle( R.string.title_question ); // устанавливаем title "Вопрос"
         textViewQuestion.setText( listQuestion.get( listIndexQuestion.get( I ) ) ); // показываем вопрос
      }
      else if( I < ( A + B ) )
      {
         setTitle( getString( R.string.title_test) ); // устанавливаем title "Тест"
         textViewQuestion.setText( listTest.get( listIndexTest.get( I - A ) ) ); // показываем тест
      }
      else
      {
         setTitle( getString( R.string.title_task) ); // устанавливаем title "Задача"
         textViewQuestion.setText( listTask.get( listIndexTask.get( I - A - B ) ) ); // показываем задачу
      }
   }

   // показываем ответ
   private void showAnswer()
   {
      // устанавливаем title "Ответ"
      setTitle( getString( R.string.title_answer) );

      // создаём константы
      // для улучшения читабельности кода
      final int I = indexCount;
      final int A = listIndexQuestion.size();
      final int B = listIndexTest.size();

      // индексы ответов берём в listIndex*
      if( I < A )
         textViewQuestion.setText( listAnswerQuestion.get( listIndexQuestion.get( I ) ) ); // показываем вопрос
      else if( I < ( A + B ) )
         textViewQuestion.setText( listAnswerTest.get( listIndexTest.get( I - A ) ) ); // показываем тест
      else
         textViewQuestion.setText( listAnswerTask.get( listIndexTask.get( I - A - B ) ) ); // показываем задачу
   }

   // выполнится при нажатии кнопки "Назад"
   @Override
   public void onBackPressed()
   {
      super.onBackPressed();

      // полностью закрываем приложение
      finishAndRemoveTask();
   }

   // запись в локальную базу данных
   private void writeToLocalBase()
   {
      String strQuestion = "Как оформляется однострочный комментарий ?&SPLITTER&Чем отличается var от val ?&SPLITTER&Как присвоить переменной двоичное, восьмеричное и шестнадцатиричное значения ?&SPLITTER&Какие есть операторы сравнения ?&SPLITTER&Разница между print() и println() ?&SPLITTER&Как в println() вставить значение переменной ?&SPLITTER&Типы переменных&SPLITTER&Какие типы присваиваются переменным по-умолчанию ?&SPLITTER&Как явно указать тип переменной ?";
      String strAnswerQuestion = "// - однострочный комментарий&SPLITTER&var - переменная\nval - константа&SPLITTER&a = 0b01001 - двоичное значение\nb = 0xEF72A - шестнадцатиричное\nвосьмеричное не предусмотрено&SPLITTER&<, >, <=, >=&SPLITTER&println() переводит курсор на новую строку&SPLITTER&println( \"x = $num\" ) - с помощью знака $&SPLITTER&Byte, Int, Long, Float, Double, Char, String, Boolean&SPLITTER&Int, Double&SPLITTER&y = 250L, z = 4.7F";

      editor.putString( STR_QUESTION, strQuestion );
      editor.putString( STR_ANSWER_QUESTION, strAnswerQuestion );
      editor.apply();
   }
}